package net.turtleboi.pawparcels.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PawParcels.MOD_ID);

    public static final DeferredItem<Item> SUGAR_COOKIE = ITEMS.register("sugar_cookie",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILVER_BELL = ITEMS.register("silver_bell",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NOEL_STAFF = ITEMS.register("noel_staff",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MISTLE_TOE = ITEMS.register("mistletoe",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HEARTHSTONE = ITEMS.register("hearthstone",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
