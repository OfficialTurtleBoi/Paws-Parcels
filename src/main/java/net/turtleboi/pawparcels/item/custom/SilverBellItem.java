package net.turtleboi.pawparcels.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.turtleboi.pawparcels.effect.ModEffects;

import java.util.List;

public class SilverBellItem extends Item {
    private final int jollyTicks;
    private final int cooldownTicks;
    private final double radius;
    public SilverBellItem(Properties properties, int jollyTicks, int cooldownTicks, float radius) {
        super(properties);
        this.jollyTicks = jollyTicks;
        this.cooldownTicks = cooldownTicks;
        this.radius = radius;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack silverStack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(silverStack)) {
            return InteractionResult.FAIL;
        }

        RandomSource random = level.getRandom();
        float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
        float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;

        level.playSound(
                player,
                player.getOnPos().above(),
                SoundEvents.BELL_BLOCK,
                SoundSource.PLAYERS,
                volume,
                pitch);

        AABB effectRadius = player.getBoundingBox().inflate(radius);
        List<LivingEntity> jollyTargets = level.getEntitiesOfClass(
                LivingEntity.class,
                effectRadius,
                livingEntity -> livingEntity.isAlive() && livingEntity != player && !isHostile(livingEntity)
        );

        MobEffectInstance jollyEffect = new MobEffectInstance(ModEffects.JOLLY, jollyTicks, 0);
        for (LivingEntity livingEntity : jollyTargets) {
            livingEntity.addEffect(jollyEffect);
        }

        player.getCooldowns().addCooldown(silverStack, cooldownTicks);
        return InteractionResult.CONSUME;
    }

    private static boolean isHostile(LivingEntity livingEntity) {
        if (livingEntity instanceof Enemy) {
            return true;
        }
        if (livingEntity instanceof Monster) {
            return true;
        }
        if (livingEntity instanceof Mob mob) {
            return mob.isAggressive();
        }
        return false;
    }
}
