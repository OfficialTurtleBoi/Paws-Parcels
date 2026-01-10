package net.turtleboi.pawparcels.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.Weapon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.turtleboi.pawparcels.PawParcels;
import net.turtleboi.pawparcels.entity.ModEntities;
import net.turtleboi.pawparcels.item.custom.*;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PawParcels.MOD_ID);

    public static final DeferredItem<Item> SUGAR_COOKIE = registerItem("sugar_cookie",
            () -> new Item.Properties().food(ModFoods.SUGAR_COOKIE), properties -> new SugarCookieItem(properties, 20 * 30, 0));

    public static final DeferredItem<Item> SILVER_BELL = registerItem("silver_bell",
            () -> new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1), properties -> new SilverBellItem(properties, 20 * 60, 20 * 90, 12.0f));

    public static final DeferredItem<Item> NOEL_STAFF = registerItem("noel_staff",
            () -> new Item.Properties()
                    .stacksTo(1)
                    .durability(1561)
                    .attributes(NoelStaffItem.createNoelStaffAttributes())
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.WEAPON, new Weapon(1)),
            NoelStaffItem::new);

    public static final DeferredItem<Item> MISTLE_TOE = registerItem("mistletoe",
            () -> new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1), Item::new);

    public static final DeferredItem<Item> HEARTHSTONE = registerItem("hearthstone",
            () -> new Item.Properties().rarity(Rarity.RARE).stacksTo(1), properties -> new HearthstoneItem(properties, 20 * 90));

    public static final DeferredItem<Item> COMMON_GIFT = registerItem("common_gift",
            () -> new Item.Properties(),
            properties -> new GiftItem(properties, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "gifts/common")));

    public static final DeferredItem<Item> UNCOMMON_GIFT = registerItem("uncommon_gift",
            () -> new Item.Properties().rarity(Rarity.UNCOMMON),
            properties -> new GiftItem(properties, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "gifts/uncommon")));

    public static final DeferredItem<Item> RARE_GIFT = registerItem("rare_gift",
            () -> new Item.Properties().rarity(Rarity.RARE),
            properties -> new GiftItem(properties, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "gifts/rare")));

    public static final DeferredItem<Item> EPIC_GIFT = registerItem("epic_gift",
            () -> new Item.Properties().rarity(Rarity.EPIC),
            properties -> new GiftItem(properties, Identifier.fromNamespaceAndPath(PawParcels.MOD_ID, "gifts/epic")));


    public static final DeferredItem<SpawnEggItem> MITTEN_MOUSE_SPAWN_EGG = ITEMS.registerItem("mitten_mouse_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.MITTEN_MOUSE.get())));

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
