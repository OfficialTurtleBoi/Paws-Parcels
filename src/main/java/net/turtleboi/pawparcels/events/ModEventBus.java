package net.turtleboi.pawparcels.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.ModEntities;
import net.turtleboi.pawparcels.entity.entities.MittenMouseEntity;

@EventBusSubscriber(modid = PawParcels.MOD_ID)
public class ModEventBus {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.MITTEN_MOUSE.get(),
                MittenMouseEntity.createAttributes().build()
        );
    }
}
