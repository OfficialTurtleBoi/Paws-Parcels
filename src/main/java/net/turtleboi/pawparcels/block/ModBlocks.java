package net.turtleboi.pawparcels.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.item.ModItems;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(PawParcels.MOD_ID);

    public static final DeferredBlock<Block> HEARTH = registerSimpleBlock("hearth",
            () -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS));

    private static DeferredBlock<Block> registerSimpleBlock(String name, Supplier<BlockBehaviour.Properties> baseProperties) {
        return registerBlock(name, baseProperties, Block::new);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<BlockBehaviour.Properties> baseProperties, Function<BlockBehaviour.Properties, T> blockFactory) {
        DeferredBlock<T> holder = BLOCKS.register(name, (Identifier id) -> {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
            return blockFactory.apply(baseProperties.get().setId(key));
        });
        registerBlockItem(name, holder);
        return holder;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, (Identifier id) -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
            return new BlockItem(block.get(), new Item.Properties().setId(key));
        });
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
