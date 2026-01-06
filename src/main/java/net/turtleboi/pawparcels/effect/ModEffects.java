package net.turtleboi.pawparcels.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.effect.custom.BrittleEffect;
import net.turtleboi.pawparcels.effect.custom.JollyEffect;
import net.turtleboi.pawparcels.effect.custom.SunderedEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, PawParcels.MOD_ID);

    public static final Holder<MobEffect> JOLLY = MOB_EFFECTS.register("jolly",
            () -> new JollyEffect(MobEffectCategory.BENEFICIAL, 14747722));

    public static final Holder<MobEffect> BRITTLE = MOB_EFFECTS.register("brittle",
            () -> new BrittleEffect(MobEffectCategory.HARMFUL, 14799593));

    public static final Holder<MobEffect> SUNDERED = MOB_EFFECTS.register("sundered",
            () -> new SunderedEffect(MobEffectCategory.HARMFUL, 3093074));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
