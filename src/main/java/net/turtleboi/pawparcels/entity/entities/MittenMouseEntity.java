package net.turtleboi.pawparcels.entity.entities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.turtleboi.pawparcels.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class MittenMouseEntity extends Animal {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private static final Ingredient FOOD = Ingredient.of(
            Items.SWEET_BERRIES,
            ModItems.SUGAR_COOKIE.get()
    );
    public MittenMouseEntity(EntityType<? extends Animal > type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, FOOD, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.TEMPT_RANGE, 10.0D);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return (MittenMouseEntity) this.getType().create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupIdleAnimation();
        }
    }

    private void setupIdleAnimation() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D) {
            this.idleAnimationState.stop();
        } else if (!this.idleAnimationState.isStarted()) {
            this.idleAnimationState.start(this.tickCount);
        }
    }

    @Override
    public boolean isFood(@NotNull ItemStack foodStack) {
        return FOOD.test(foodStack);
    }
}
