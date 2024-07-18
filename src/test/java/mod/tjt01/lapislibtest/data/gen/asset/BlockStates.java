package mod.tjt01.lapislibtest.data.gen.asset;

import mod.tjt01.lapislibtest.LapisLibTest;
import mod.tjt01.lapislibtest.block.LapisLibTestBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, LapisLibTest.MODID, existingFileHelper);
    }

    private void simpleBlockAndItem(Block block, ModelFile modelFile) {
        this.simpleBlock(block, modelFile);
        this.simpleBlockItem(block, modelFile);
    }

    @Override
    protected void registerStatesAndModels() {
        this.simpleBlockAndItem(
                LapisLibTestBlocks.CRAFTING_BLOCK.get(),
                this.models().cubeBottomTop(
                        LapisLibTestBlocks.CRAFTING_BLOCK.getId().toString(),
                        blockTexture(LapisLibTestBlocks.CRAFTING_BLOCK.get()),
                        mcLoc("block/iron_block"),
                        modLoc("block/test_crafting_block_top")
                )
        );
    }
}
