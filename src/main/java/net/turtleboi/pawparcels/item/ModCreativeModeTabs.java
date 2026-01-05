package net.turtleboi.pawparcels.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PawParcels.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PAWPARCELS_TAB =
            CREATIVE_MODE_TAB.register("pawparcels_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SUGAR_COOKIE.get()))
                    .title(Component.translatable("creativetab.pawparcels.pawparcels_tab"))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.HEARTH.get());
                        output.accept(ModItems.SUGAR_COOKIE.get());
                        output.accept(ModItems.SILVER_BELL.get());
                        output.accept(ModItems.NOEL_STAFF.get());
                        output.accept(ModItems.MISTLE_TOE.get());
                        output.accept(ModItems.HEARTHSTONE.get());
                    })
                    .build()
            );

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
