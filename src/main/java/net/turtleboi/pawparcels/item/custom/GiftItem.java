package net.turtleboi.pawparcels.item.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.turtleboi.pawparcels.PawParcels;

import java.util.List;

public class GiftItem extends Item {
    private final ResourceKey<LootTable> lootTableKey;
    public GiftItem(Properties properties, Identifier lootTableId) {
        super(properties);
        this.lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack giftStack = player.getItemInHand(interactionHand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer)) {
            return InteractionResult.CONSUME;
        }

        if (!player.getAbilities().instabuild) {
            giftStack.shrink(1);
        }

        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(this.lootTableKey);

        if (lootTable == LootTable.EMPTY) {
            PawParcels.LOGGER.warn("Gift loot table missing: {}", this.lootTableKey.identifier());
            return InteractionResult.CONSUME;
        }
        
        LootParams lootParams = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.GIFT);

        List<ItemStack> rolls = lootTable.getRandomItems(lootParams);
        for (ItemStack roll : rolls) {
            if (!roll.isEmpty()) {
                if (!player.getInventory().add(roll.copy())) {
                    player.drop(roll, false, false);
                }
            }
        }

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.PLAYERS, 0.7F, 1.1F);
        return InteractionResult.CONSUME;
    }
}
