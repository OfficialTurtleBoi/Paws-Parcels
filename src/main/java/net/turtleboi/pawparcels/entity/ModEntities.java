package net.turtleboi.pawparcels.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.entities.MittenMouseEntity;
import net.turtleboi.pawparcels.entity.entities.OrnamentEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PawParcels.MOD_ID);

    public static final Supplier<EntityType<OrnamentEntity>> ORNAMENT =
            ENTITY_TYPES.register("ornament", () -> EntityType.Builder.of(OrnamentEntity::new, MobCategory.MISC)
                    .sized(0.3125f, 0.3125f).build(ResourceKey.create(
                            Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "ornament"))));

    public static final Supplier<EntityType<MittenMouseEntity>> MITTEN_MOUSE =
            ENTITY_TYPES.register("mitten_mouse", () -> EntityType.Builder.of(MittenMouseEntity::new, MobCategory.AMBIENT)
                    .sized(0.375f, 0.375f).build(ResourceKey.create(
                            Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "mitten_mouse"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
