package net.turtleboi.pawparcels.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Weapon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.item.custom.*;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PawParcels.MOD_ID);

    public static final DeferredItem<Item> SUGAR_COOKIE = registerItem("sugar_cookie",
            () -> new Item.Properties().food(ModFoods.SUGAR_COOKIE), properties -> new SugarCookieItem(properties, 20 * 30, 0));

    public static final DeferredItem<Item> SILVER_BELL = registerItem("silver_bell",
            () -> new Item.Properties().stacksTo(1), properties -> new SilverBellItem(properties, 20 * 60, 20 * 90, 12.0f));

    public static final DeferredItem<Item> NOEL_STAFF = registerItem("noel_staff",
            () -> new Item.Properties()
                    .stacksTo(1)
                    .durability(1561)
                    .attributes(NoelStaffItem.createNoelStaffAttributes())
                    .component(DataComponents.WEAPON, new Weapon(1)),
            NoelStaffItem::new);

    public static final DeferredItem<Item> MISTLE_TOE = registerItem("mistletoe",
            () -> new Item.Properties().stacksTo(1), Item::new);

    public static final DeferredItem<Item> HEARTHSTONE = registerItem("hearthstone",
            () -> new Item.Properties().stacksTo(1), HearthstoneItem::new);

    public static final DeferredItem<Item> COMMON_GIFT = registerItem("common_gift",
            () -> new Item.Properties(), GiftItem::new);

    public static final DeferredItem<Item> UNCOMMON_GIFT = registerItem("uncommon_gift",
            () -> new Item.Properties().rarity(Rarity.UNCOMMON), GiftItem::new);

    public static final DeferredItem<Item> RARE_GIFT = registerItem("rare_gift",
            () -> new Item.Properties().rarity(Rarity.RARE), GiftItem::new);

    public static final DeferredItem<Item> EPIC_GIFT = registerItem("epic_gift",
            () -> new Item.Properties().rarity(Rarity.EPIC), GiftItem::new);

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
