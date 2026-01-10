package net.turtleboi.pawparcels.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.turtleboi.pawparcels.block.custom.HearthBlock;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class HearthstoneItem extends Item {
    private static final String rootData = "hearth_root";
    private static final String dimensionData = "hearth_dimension";
    private static final String xData = "hearth_x";
    private static final String yData = "hearth_y";
    private static final String zData = "hearth_z";
    private static final String chargedData = "hearth_charged";

    private static final int minimumChargeTicks = 20 * 2;
    private static final int useDurationTicks = minimumChargeTicks * 4;

    private final int cooldownTicks;
    public HearthstoneItem(Properties properties, int cooldownTicks) {
        super(properties);
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        ItemStack itemStack = useOnContext.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(blockPos);
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        if (!(state.getBlock() instanceof HearthBlock)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.literal("Must bind this stone to a Hearth"), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            setBoundLocation(itemStack, level.dimension(), blockPos);
            player.displayClientMessage(Component.literal(
                    "Hearthstone bound to Hearth at " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ()
            ), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return useDurationTicks;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack hearthstoneStack = player.getItemInHand(interactionHand);
        if (player.getCooldowns().isOnCooldown(hearthstoneStack)) {
            return InteractionResult.PASS;
        }

        player.startUsingItem(interactionHand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (livingEntity instanceof Player player) {
            setCharged(itemStack, false);
            int usedTicks = this.getUseDuration(itemStack, livingEntity) - timeLeft;
            if (usedTicks < minimumChargeTicks) {
                return false;
            }

            if (player.getCooldowns().isOnCooldown(itemStack)) {
                return false;
            }

            if (level.isClientSide()) {
                return false;
            }

            Optional<BoundHearth> boundLocation = getBoundLocation(itemStack);
            if (boundLocation.isEmpty()) {
                player.displayClientMessage(Component.literal("This Hearthstone is not bound to a Hearth."), true);
                return false;
            }

            BoundHearth boundHearth = boundLocation.get();
            ServerLevel targetLevel = Objects.requireNonNull(player.level().getServer()).getLevel(boundHearth.levelResourceKey);
            if (targetLevel == null) {
                player.displayClientMessage(Component.literal("That Hearth's levelResourceKey is unavailable."), true);
                return false;
            }

            BlockState hearthState = targetLevel.getBlockState(boundHearth.blockPos);
            if (!(hearthState.getBlock() instanceof HearthBlock)) {
                player.displayClientMessage(Component.literal("The bound Hearth is gone."), true);
                return false;
            }

            targetLevel.getChunkAt(boundHearth.blockPos);

            if (player instanceof ServerPlayer serverPlayer) {
                Vec3 destination = Vec3.atCenterOf(boundHearth.blockPos).add(0.0, 1.0, 0.0);
                Direction facing = hearthState.getValue(HearthBlock.FACING);
                Vec3 safePosition = findSafeSpot(targetLevel, boundHearth.blockPos, facing).orElse(destination);

                boolean teleportedTo = serverPlayer.teleportTo(
                        targetLevel,
                        safePosition.x,
                        safePosition.y,
                        safePosition.z,
                        Set.of(),
                        serverPlayer.getYRot(),
                        serverPlayer.getXRot(),
                        true
                );

                if (!teleportedTo) {
                    player.displayClientMessage(Component.literal("Teleport failed."), true);
                    return false;
                }

                player.getCooldowns().addCooldown(itemStack, cooldownTicks);
                RandomSource random = targetLevel.getRandom();
                float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
                float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
                targetLevel.playSound(
                        null,
                        BlockPos.containing(safePosition),
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.PLAYERS,
                        volume,
                        pitch
                );
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStopUsing(ItemStack itemStack, LivingEntity livingEntity, int timeLeft) {
        setCharged(itemStack, false);
        super.onStopUsing(itemStack, livingEntity, timeLeft);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int remainingUseDuration) {
        if (level.isClientSide()) {
            if (livingEntity instanceof Player player) {
                int usedTicks = this.getUseDuration(itemStack, livingEntity) - remainingUseDuration;
                spawnChargingParticles(level, player, usedTicks);

                if (usedTicks == minimumChargeTicks) {
                    setCharged(itemStack, true);
                    level.playLocalSound(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.AMETHYST_BLOCK_RESONATE,
                            SoundSource.PLAYERS,
                            0.8F,
                            1.2F,
                            false
                    );
                }
            }
            return;
        }

        if (remainingUseDuration == 1) {
            if (livingEntity instanceof Player player) {
                player.releaseUsingItem();
            }
        }
    }

    private static void setBoundLocation(ItemStack itemStack, ResourceKey<Level> levelResourceKey, BlockPos blockPos) {
        CustomData dataComponents = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbtData = dataComponents.copyTag();

        CompoundTag dataRoot = nbtData.getCompound(HearthstoneItem.rootData).orElseGet(CompoundTag::new);
        dataRoot.putString(dimensionData, levelResourceKey.identifier().toString());
        dataRoot.putInt(xData, blockPos.getX());
        dataRoot.putInt(yData, blockPos.getY());
        dataRoot.putInt(zData, blockPos.getZ());

        nbtData.put(HearthstoneItem.rootData, dataRoot);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
    }

    private static Optional<BoundHearth> getBoundLocation(ItemStack stack) {
        CustomData dataComponents = stack.get(DataComponents.CUSTOM_DATA);
        if (dataComponents == null) return Optional.empty();

        CompoundTag nbtData = dataComponents.copyTag();
        Optional<CompoundTag> rootOpt = nbtData.getCompound(rootData);
        if (rootOpt.isEmpty()) return Optional.empty();
        CompoundTag dataRoot = rootOpt.get();

        Optional<String> dimenstionStringOpt = dataRoot.getString(dimensionData);
        if (dimenstionStringOpt.isEmpty()) return Optional.empty();

        Identifier dimensionId = Identifier.tryParse(dimenstionStringOpt.get());
        if (dimensionId == null) return Optional.empty();

        Optional<Integer> xOpt = dataRoot.getInt(xData);
        Optional<Integer> yOpt = dataRoot.getInt(yData);
        Optional<Integer> zOpt = dataRoot.getInt(zData);

        if (xOpt.isEmpty() || yOpt.isEmpty() || zOpt.isEmpty()) return Optional.empty();

        ResourceKey<Level> levelResourceKey = ResourceKey.create(Registries.DIMENSION, dimensionId);

        BlockPos blockPos = new BlockPos(xOpt.get(), yOpt.get(), zOpt.get());
        return Optional.of(new BoundHearth(levelResourceKey, blockPos));
    }

    private record BoundHearth(ResourceKey<Level> levelResourceKey, BlockPos blockPos) {

    }

    private static Optional<Vec3> findSafeSpot(ServerLevel serverLevel, BlockPos hearthPos, Direction facing) {
        BlockPos basePos = hearthPos.relative(facing);

        if (isSafeStand(serverLevel, basePos)) {
            return Optional.of(center(basePos));
        }

        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    BlockPos candidate = basePos.offset(dx, 0, dz);
                    if (isSafeStand(serverLevel, candidate)) {
                        return Optional.of(center(candidate));
                    }
                }
            }
        }

        return Optional.empty();
    }

    private static boolean isSafeStand(ServerLevel level, BlockPos feet) {
        BlockPos head = feet.above();
        BlockPos below = feet.below();

        boolean floor = level.getBlockState(below).isFaceSturdy(level, below, net.minecraft.core.Direction.UP);
        boolean feetClear = level.getBlockState(feet).getCollisionShape(level, feet).isEmpty();
        boolean headClear = level.getBlockState(head).getCollisionShape(level, head).isEmpty();

        return floor && feetClear && headClear;
    }

    private static Vec3 center(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return isCharged(itemStack);
    }

    private static void setCharged(ItemStack itemStack, boolean charged) {
        CustomData dataComponents = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbtData = dataComponents.copyTag();
        CompoundTag dataRoot = nbtData.getCompound(HearthstoneItem.rootData).orElseGet(CompoundTag::new);

        dataRoot.putBoolean(chargedData, charged);
        nbtData.put(HearthstoneItem.rootData, dataRoot);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
    }

    private static boolean isCharged(ItemStack itemStack) {
        CustomData dataComponents = itemStack.get(DataComponents.CUSTOM_DATA);
        if (dataComponents == null) return false;

        CompoundTag nbtData = dataComponents.copyTag();
        Optional<CompoundTag> rootOpt = nbtData.getCompound(rootData);
        if (rootOpt.isEmpty()) return false;

        CompoundTag dataRoot = rootOpt.get();
        return dataRoot.getBoolean(chargedData).orElse(false);
    }

    private static void spawnChargingParticles(Level level, Player player, int usedTicks) {
        RandomSource random = level.getRandom();
        double baseX = player.getX();
        double baseY = player.getY() + 0.1;
        double baseZ = player.getZ();
        if (random.nextInt(20) == 0) {
            level.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    baseX + (random.nextDouble() - 0.5) * 0.2,
                    baseY - 0.2 + random.nextDouble() * 0.2,
                    baseZ + (random.nextDouble() - 0.5) * 0.2,
                    0.0, 0.02, 0.0
            );
        }

        float castProgress = usedTicks / (float) minimumChargeTicks;
        castProgress = Mth.clamp(castProgress, 0.0F, 1.0F);
        float radius = Mth.lerp(castProgress, 1.15F, 0.5F);

        int clampedChargeTicks = Math.min(usedTicks, minimumChargeTicks);
        double chargeAngle = (clampedChargeTicks / (double) minimumChargeTicks) * (Math.PI * 2.0);

        int extraTicks = Math.max(0, usedTicks - minimumChargeTicks);
        double particleAngle = chargeAngle + (extraTicks * (Math.PI * 2.0) / 40.0);

        double particleY = baseY + ((castProgress * (player.getBbHeight() / 2)) - 0.1);
        double x1 = baseX + Math.cos(particleAngle) * radius;
        double z1 = baseZ + Math.sin(particleAngle) * radius;

        double x2 = baseX + Math.cos(particleAngle + Math.PI) * radius;
        double z2 = baseZ + Math.sin(particleAngle + Math.PI) * radius;

        double vx1 = (random.nextDouble() - 0.5) * 0.003;
        double vz1 = (random.nextDouble() - 0.5) * 0.003;
        double vx2 = (random.nextDouble() - 0.5) * 0.003;
        double vz2 = (random.nextDouble() - 0.5) * 0.003;
        double vy = 0.004 + random.nextDouble() * 0.004;

        level.addParticle(ParticleTypes.FLAME, x1, particleY, z1, vx1, vy, vz1);
        level.addParticle(ParticleTypes.FLAME, x2, particleY, z2, vx2, vy, vz2);

        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.SMALL_FLAME, x1, particleY, z1, 0.0, vy * 1.2, 0.0);
        }
    }
}
