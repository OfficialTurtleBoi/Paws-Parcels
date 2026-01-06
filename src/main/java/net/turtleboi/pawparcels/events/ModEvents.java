package net.turtleboi.pawparcels.events;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.effect.ModEffects;
import net.turtleboi.pawparcels.item.ModItems;
import net.turtleboi.pawparcels.mixin.MerchantMenuAccessor;

@EventBusSubscriber(modid = PawParcels.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onLivingHurtPost(LivingDamageEvent.Post event) {
        Entity attacker = event.getSource().getEntity();
        Entity victim = event.getEntity();

        //Mistletoe logic
        if (attacker instanceof Player player) {
            if (player.getOffhandItem().is(ModItems.MISTLE_TOE.get())) {
                if (victim instanceof LivingEntity livingVictim) {
                    livingVictim.addEffect(new MobEffectInstance(ModEffects.BRITTLE, 20 * 30, 0));
                    //System.out.println("Brittle added!");
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtPre(LivingDamageEvent.Pre event) {
        Entity attacker = event.getSource().getEntity();
        Entity victim = event.getEntity();

        //Brittle logic
        if (victim instanceof LivingEntity livingVictim) {
            MobEffectInstance brittleEffect = livingVictim.getEffect(ModEffects.BRITTLE);
            if (brittleEffect != null) {
                int amplifier = brittleEffect.getAmplifier() + 1;
                float baseDamage = event.getOriginalDamage();

                float multiplier = (float) Math.pow(1.25f, amplifier);
                float finalDamage = baseDamage * multiplier;

                int ticksReduced = Math.round(10f * baseDamage * multiplier);
                int remainingTicks = Math.max(0, brittleEffect.getDuration() - ticksReduced);
                event.setNewDamage(finalDamage);
                livingVictim.removeEffect(ModEffects.BRITTLE);
                if (remainingTicks > 0) {
                    livingVictim.removeEffect(ModEffects.BRITTLE);
                    livingVictim.addEffect(new MobEffectInstance(
                            ModEffects.BRITTLE,
                            remainingTicks,
                            brittleEffect.getAmplifier(),
                            brittleEffect.isAmbient(),
                            brittleEffect.isVisible(),
                            brittleEffect.showIcon()
                    ));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance jolly = entity.getEffect(ModEffects.JOLLY);
        if (jolly != null) {
            int amplifier = jolly.getAmplifier() + 1;
            float multiplier = 1.0f + 0.25f * amplifier;
            float baseHeal = event.getAmount();
            event.setAmount(baseHeal * multiplier);
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (event.getContainer() instanceof MerchantMenu menu) {
            Merchant merchant = ((MerchantMenuAccessor) menu).pawparcels$getTrader();
            if (merchant instanceof LivingEntity livingMerchant) {
                MobEffectInstance jolly = livingMerchant.getEffect(ModEffects.JOLLY);
                if (jolly == null) return;

                int amplifier = jolly.getAmplifier() + 1;
                float discountMultiplier = Math.max(0.05f, 1.0f - 0.25f * amplifier);

                MerchantOffers offers = menu.getOffers();
                if (offers.isEmpty()) return;

                for (MerchantOffer offer : offers) {
                    applyDiscount(offer, discountMultiplier);
                }
            }
        }


    }

    private static void applyDiscount(MerchantOffer offer, float discountMultiplier) {
        ItemCost itemCostA = offer.getItemCostA();
        int baseCost = itemCostA.count();
        int demandCost = Math.max(0, (int) Math.floor((baseCost * offer.getDemand()) * offer.getPriceMultiplier()));

        int targetCost = Math.max(1, (int) Math.ceil(baseCost * discountMultiplier));
        int newSpecial = targetCost - (baseCost + demandCost);

        offer.setSpecialPriceDiff(Math.min(offer.getSpecialPriceDiff(), newSpecial));
    }

}
