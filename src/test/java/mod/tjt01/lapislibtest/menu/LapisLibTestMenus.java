package mod.tjt01.lapislibtest.menu;

import mod.tjt01.lapislibtest.LapisLibTest;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LapisLibTestMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LapisLibTest.MODID);

    public static final RegistryObject<MenuType<TestCraftingMenu>> CRAFTING_MENU
            = MENU_TYPES.register("crafting_menu", () -> new MenuType<>(TestCraftingMenu::new));
    public static final RegistryObject<MenuType<TestMachineMenu>> MACHINE_MENU
            = MENU_TYPES.register("machine_menu", () -> IForgeMenuType.create(TestMachineMenu::create));

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }
}
