package baios.magicgirl.phone.item;

import baios.magicgirl.phone.MagicGirlPhone;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Moditems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MagicGirlPhone.MODID);

    public static final DeferredItem<Item> PHONE =
            ITEMS.register("phone_1", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
