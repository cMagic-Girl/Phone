package baios.magicgirl.phone.item;

import baios.magicgirl.phone.MagicGirlPhone;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items Registries =
            DeferredRegister.createItems(MagicGirlPhone.MODID);

    public static final DeferredItem<Item> EMA_PHONE;

    static {
        EMA_PHONE = Registries.register("ema_phone",EmaPhone::new);
    }

}
