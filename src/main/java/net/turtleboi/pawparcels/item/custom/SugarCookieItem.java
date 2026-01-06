package net.turtleboi.pawparcels.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.turtleboi.pawparcels.effect.ModEffects;

public class SugarCookieItem extends Item {
    private final int jollyDurationTicks;
    private final int jollyAmplifier;

    public SugarCookieItem(Properties properties, int jollyDurationTicks, int jollyAmplifier) {
        super(properties);
        this.jollyDurationTicks = jollyDurationTicks;
        this.jollyAmplifier = jollyAmplifier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            entity.addEffect(new MobEffectInstance(ModEffects.JOLLY, jollyDurationTicks, jollyAmplifier));
        }

        return result;
    }
}
