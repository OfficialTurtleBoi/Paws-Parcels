package net.turtleboi.pawparcels.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PawParcels.MOD_ID);

    public static final DeferredItem<Item> SUGAR_COOKIE = registerSimpleItem("sugar_cookie",
            () -> new Item.Properties());

    public static final DeferredItem<Item> SILVER_BELL = registerSimpleItem("silver_bell",
            () -> new Item.Properties());

    public static final DeferredItem<Item> NOEL_STAFF = registerSimpleItem("noel_staff",
            () -> new Item.Properties());

    public static final DeferredItem<Item> MISTLE_TOE = registerSimpleItem("mistletoe",
            () -> new Item.Properties());

    public static final DeferredItem<Item> HEARTHSTONE = registerSimpleItem("hearthstone",
            () -> new Item.Properties());

    private static DeferredItem<Item> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
        return registerItem(name, properties, Item::new);
    }

    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<Item.Properties> props, Function<Item.Properties, T> itemFactory) {
        return ITEMS.register(name, (Identifier id) -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
            return itemFactory.apply(props.get().setId(key));
        });
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
