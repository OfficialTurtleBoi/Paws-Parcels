package net.turtleboi.pawparcels.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.pawparcels.entity.ModEntities;
import net.turtleboi.pawparcels.entity.entities.OrnamentEntity;

public class NoelStaffItem extends Item {
    public NoelStaffItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createNoelStaffAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack staffStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            player.swing(hand, true);
            return InteractionResult.SUCCESS;
        }

        Vec3 lookAngle = player.getLookAngle();
        Vec3 spawnPos = player.getEyePosition().add(lookAngle.scale(0.35));
        OrnamentEntity ornamentEntity = new OrnamentEntity(ModEntities.ORNAMENT.get(), level);
        ornamentEntity.setOwner(player);
        ornamentEntity.setVariant(OrnamentEntity.OrnamentVariant.getRandomVariant(level.random));
        Vec3 ornamentPos = new Vec3(spawnPos.x, spawnPos.y, spawnPos.z);
        ornamentEntity.moveOrInterpolateTo(ornamentPos, player.getYRot(), player.getXRot());

        float speed = 1.33f;
        float inaccuracy = 0.5f;
        ornamentEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);

        level.addFreshEntity(ornamentEntity);
        RandomSource random = level.getRandom();
        float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
        float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                volume,
                pitch);
        player.getCooldowns().addCooldown(staffStack, 20);
        staffStack.hurtAndBreak(1, player, player.getUsedItemHand());
        return InteractionResult.CONSUME;
    }
}
