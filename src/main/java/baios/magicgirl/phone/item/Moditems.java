package baios.magicgirl.phone.item;

import baios.magicgirl.phone.MagicGirlPhone;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Moditems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MagicGirlPhone.MODID);

    public static final DeferredItem<Item> EMA_PHONE =
            ITEMS.register("ema_phone", () -> new EmaPhone(
                    new Item.Properties()
                            .stacksTo(1) // 堆叠数量
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
