package net.turtleboi.pawparcels.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack hearthstoneStack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(hearthstoneStack)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Optional<BoundHearth> boundLocation = getBoundLocation(hearthstoneStack);
        if (boundLocation.isEmpty()) {
            player.displayClientMessage(Component.literal("This Hearthstone is not bound to a Hearth."), true);
            return InteractionResult.FAIL;
        }

        BoundHearth boundHearth = boundLocation.get();
        ServerLevel targetLevel = Objects.requireNonNull(player.level().getServer()).getLevel(boundHearth.levelResourceKey);
        if (targetLevel == null) {
            player.displayClientMessage(Component.literal("That Hearth's levelResourceKey is unavailable."), true);
            return InteractionResult.FAIL;
        }

        BlockState hearthState = targetLevel.getBlockState(boundHearth.blockPos);
        if (!(hearthState.getBlock() instanceof HearthBlock)) {
            player.displayClientMessage(Component.literal("The bound Hearth is gone."), true);
            return InteractionResult.FAIL;
        }

        targetLevel.getChunkAt(boundHearth.blockPos);
        if (player instanceof ServerPlayer serverPlayer) {
            Vec3 destination = Vec3.atCenterOf(boundHearth.blockPos).add(0.0, 1.0, 0.0);
            Direction facing = hearthState.getValue(HearthBlock.FACING);
            Vec3 safePosition = findSafeSpot(targetLevel, boundHearth.blockPos, facing).orElse(destination);
            serverPlayer.teleportTo(
                    targetLevel,
                    safePosition.x,
                    safePosition.y,
                    safePosition.z,
                    Set.of(),
                    serverPlayer.getYRot(),
                    serverPlayer.getXRot(),
                    true
            );

            player.getCooldowns().addCooldown(hearthstoneStack, cooldownTicks);
            RandomSource random = level.getRandom();
            float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
            float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.FIRECHARGE_USE,
                    SoundSource.PLAYERS,
                    volume,
                    pitch);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
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

        for (int r = 1; r <= 3; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;
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
}
