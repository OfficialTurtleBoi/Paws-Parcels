package net.turtleboi.pawparcels;

import net.turtleboi.pawparcels.block.ModBlocks;
import net.turtleboi.pawparcels.entity.ModEntities;
import net.turtleboi.pawparcels.item.ModCreativeModeTabs;
import net.turtleboi.pawparcels.item.ModItems;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(PawParcels.MOD_ID)
public class PawParcels {
    public static final String MOD_ID = "pawparcels";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PawParcels(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
