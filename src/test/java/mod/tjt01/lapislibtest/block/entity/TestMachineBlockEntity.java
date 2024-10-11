package mod.tjt01.lapislibtest.block.entity;

import mod.tjt01.lapislib.capability.SimpleItemStackHandler;
import mod.tjt01.lapislibtest.data.recipe.LapisLibTestRecipeTypes;
import mod.tjt01.lapislibtest.data.recipe.TestMachineRecipe;
import mod.tjt01.lapislibtest.menu.TestMachineMenu;
import mod.tjt01.lapislibtest.network.ContainerFluidSyncPacket;
import mod.tjt01.lapislibtest.network.TestNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestMachineBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private static final Component DEFAULT_NAME = Component.translatable("container.lapislib_test.machine");

    private static final String TAG_ITEMS = "Items";
    private static final String TAG_FLUID = "Fluid";
    private static final String TAG_PROGRESS = "Progress";
    private static final String TAG_TOTAL_PROGRESS = "TotalProgress";
    private static final String TAG_USED_RECIPES = "RecipesUsed";
    private static final String TAG_CUSTOM_NAME = "CustomName";

    public final FluidTank fluid = new FluidTank(4000) {
        @Override
        protected void onContentsChanged() {
            TestMachineBlockEntity.this.setChanged();
            TestMachineBlockEntity.this.fluidChanged = true;
        }
    };

    public final SimpleItemStackHandler inventory = new SimpleItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot != 1;
        }

        @Override
        public void onContentsChanged() {
            TestMachineBlockEntity.this.setChanged();
        }
    };

    public final ContainerData data = new ContainerData() {
        public static final int KEY_PROGRESS = 0;
        public static final int KEY_TOTAL_PROGRESS = 1;

        public int progress = 0;
        public int totalProgress = 0;

        @Override
        public int get(int index) {
            return switch (index) {
                case KEY_PROGRESS -> TestMachineBlockEntity.this.progress;
                case KEY_TOTAL_PROGRESS -> TestMachineBlockEntity.this.totalProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case KEY_PROGRESS -> TestMachineBlockEntity.this.progress = value;
                case KEY_TOTAL_PROGRESS -> TestMachineBlockEntity.this.totalProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };


    protected final RangedWrapper inputWrapper = new RangedWrapper(inventory, 0, 1);
    protected final RangedWrapper outputWrapper = new RangedWrapper(inventory, 1, 2);

    protected final LazyOptional<FluidTank> fluidCap = LazyOptional.of(() -> this.fluid);
    protected final LazyOptional<RangedWrapper> itemCapInput = LazyOptional.of(() -> this.inputWrapper);
    protected final LazyOptional<RangedWrapper> itemCapOutput = LazyOptional.of(() -> this.outputWrapper);

    protected final TestMachineRecipeWrapper recipeWrapper = new TestMachineRecipeWrapper(this);

    protected int progress = 0;
    protected int totalProgress = 0;
    protected boolean fluidChanged;
    protected final RecipeManager.CachedCheck<TestMachineRecipeWrapper, TestMachineRecipe> cache;

    protected final HashSet<ResourceLocation> usedRecipes = new HashSet<>();

    @Nullable
    private Component customName;

    public TestMachineBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(LapisLibTestBlockEntityTypes.MACHINE.get(), pPos, pBlockState);
        this.cache = RecipeManager.createCheck(LapisLibTestRecipeTypes.MACHINE.get());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TestMachineBlockEntity self) {
        boolean changed = false;
        boolean hasFluid = !self.fluid.getFluid().isEmpty();
        boolean hasItem = !self.inventory.getStackInSlot(0).isEmpty();

        if (hasItem && hasFluid) {
            TestMachineRecipe recipe = self.cache.getRecipeFor(self.recipeWrapper, level).orElse(null);
            if (
                    recipe != null && self.canCraft(recipe)
            ) {
                self.progress ++;
                if (self.totalProgress <= 0) {
                    self.totalProgress = recipe.getCraftTime();
                }
                if (self.progress >= self.totalProgress) {
                    self.progress = 0;
                    ItemStack result = recipe.assemble(self.recipeWrapper);
                    self.inventory.setStackInSlot(
                            1,
                            ItemHandlerHelper.copyStackWithSize(
                                    result,
                                    result.getCount() + self.inventory.getStackInSlot(1).getCount()
                            )
                    );
                    self.inventory.extractItem(0, recipe.ingredient.count, false);

                    self.usedRecipes.add(recipe.getId());
                }
                changed = true;
            } else {
                self.progress = 0;
                self.totalProgress = 0;
                changed = true;
            }
        } else if (self.progress > 0) {
            self.progress = 0;
            self.totalProgress = 0;
            changed = true;
        }

        if (changed) {
            self.setChanged();
        }

        if (self.fluidChanged) {
            for (Player player: level.players()) {
                if (player instanceof ServerPlayer serverPlayer && player.containerMenu instanceof TestMachineMenu testMachineMenu && testMachineMenu.blockEntity == self) {
                    TestNetwork.CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new ContainerFluidSyncPacket(player.containerMenu.containerId, self.fluid.getFluid())
                    );
                }
            }
        }
    }

    public void awardUsedRecipes(ServerPlayer player) {
        List<Recipe<?>> recipes = new ArrayList<>();
        for (ResourceLocation id : usedRecipes) {
            player.getLevel().getRecipeManager().byKey(id).ifPresent(recipes::add);
        }
        player.awardRecipes(recipes);
        usedRecipes.clear();
    }

    protected boolean canCraft(TestMachineRecipe recipe) {
        if (this.inventory.getStackInSlot(0).isEmpty() || this.fluid.getFluid().isEmpty())
            return false;

        ItemStack result = recipe.assemble(this.recipeWrapper);

        if (result.isEmpty()) return false;

        ItemStack output = this.inventory.getStackInSlot(1);

        if (output.isEmpty()) return true;
        if (!result.sameItem(output)) return false;
        if (
                output.getCount() + result.getCount() > output.getMaxStackSize()
                        || output.getCount() + result.getCount() > result.getMaxStackSize()
        ) return false;
        return output.getCount() + result.getCount() <= this.inventory.getSlotLimit(1);
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCap.invalidate();
        itemCapInput.invalidate();
        itemCapOutput.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return side == Direction.DOWN ? itemCapOutput.cast() : itemCapInput.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAG_ITEMS, this.inventory.serializeNBT());
        tag.put(TAG_FLUID, this.fluid.writeToNBT(new CompoundTag()));
        tag.putInt(TAG_PROGRESS, this.progress);
        tag.putInt(TAG_TOTAL_PROGRESS, this.totalProgress);
        if (this.customName != null) {
            tag.putString(TAG_CUSTOM_NAME, Component.Serializer.toJson(customName));
        }
        ListTag usedRecipeList = new ListTag();
        for (ResourceLocation id: this.usedRecipes) {
            usedRecipeList.add(StringTag.valueOf(id.toString()));
        }
        tag.put(TAG_USED_RECIPES, usedRecipeList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND));
        this.fluid.readFromNBT(tag.getCompound(TAG_FLUID));
        this.progress = tag.getInt(TAG_PROGRESS);
        if (tag.contains(TAG_CUSTOM_NAME, Tag.TAG_STRING)) {
            this.customName = Component.Serializer.fromJson(tag.getString(TAG_CUSTOM_NAME));
        }
        ListTag list = tag.getList(TAG_USED_RECIPES, Tag.TAG_STRING);
        for (int i = 1; i < list.size(); i++) {
            String id = list.getString(i);
            this.usedRecipes.add(new ResourceLocation(id));
        }
    }

    @Override
    public Component getName() {
        return this.customName != null ? customName : DEFAULT_NAME;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TestMachineMenu(containerId, playerInventory, this, this.fluid.getFluid().copy());
    }

    @Override
    public Component getDisplayName() {
        return Nameable.super.getDisplayName();
    }

    public static class TestMachineRecipeWrapper extends RecipeWrapper {
        protected final TestMachineBlockEntity parent;
        public TestMachineRecipeWrapper(TestMachineBlockEntity parent) {
            super(parent.inventory);
            this.parent = parent;
        }

        public FluidStack getFluid() {
            return this.parent.fluid.getFluid();
        };
    }
}
