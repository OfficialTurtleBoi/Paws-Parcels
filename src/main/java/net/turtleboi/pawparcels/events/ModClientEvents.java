package net.turtleboi.pawparcels.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.ModEntities;
import net.turtleboi.pawparcels.entity.models.OrnamentModel;
import net.turtleboi.pawparcels.entity.renderers.OrnamentRenderer;

@EventBusSubscriber(modid = PawParcels.MOD_ID)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OrnamentModel.ORNAMENT_LAYER, OrnamentModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ORNAMENT.get(), OrnamentRenderer::new);
    }
}
