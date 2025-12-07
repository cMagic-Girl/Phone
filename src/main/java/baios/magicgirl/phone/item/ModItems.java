package baios.magicgirl.phone.item;

import baios.magicgirl.phone.MagicGirlPhone;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items REGISTRY =
            DeferredRegister.createItems(MagicGirlPhone.MODID);

    public static final DeferredItem<Item> EMA_PHONE;
    public static final DeferredItem<Item> HIRO_PHONE;
    public static final DeferredItem<Item> KOKO_PHONE;
    public static final DeferredItem<Item> SHERII_PHONE;
    public static final DeferredItem<Item> HANNA_PHONE;
    public static final DeferredItem<Item> ANAN_PHONE;
    public static final DeferredItem<Item> NOA_PHONE;
    public static final DeferredItem<Item> REIA_PHONE;
    public static final DeferredItem<Item> MIRIA_PHONE;
    public static final DeferredItem<Item> NANOKA_PHONE;
    public static final DeferredItem<Item> MAAGO_PHONE;
    public static final DeferredItem<Item> ARISA_PHONE;
    public static final DeferredItem<Item> MERURU_PHONE;

    static {
        EMA_PHONE = REGISTRY.register("ema_phone",()->new PhoneItem("ema_phone"));
        HIRO_PHONE = REGISTRY.register("hiro_phone",()->new PhoneItem("hiro_phone"));
        KOKO_PHONE = REGISTRY.register("koko_phone",()->new PhoneItem("koko_phone"));
        SHERII_PHONE = REGISTRY.register("sherii_phone",()->new PhoneItem("sherii_phone"));
        HANNA_PHONE = REGISTRY.register("hanna_phone",()->new PhoneItem("hanna_phone"));
        ANAN_PHONE = REGISTRY.register("anan_phone",()->new PhoneItem("anan_phone"));
        NOA_PHONE = REGISTRY.register("noa_phone",()->new PhoneItem("noa_phone"));
        REIA_PHONE = REGISTRY.register("reia_phone",()->new PhoneItem("reia_phone"));
        MIRIA_PHONE = REGISTRY.register("miria_phone",()->new PhoneItem("miria_phone"));
        NANOKA_PHONE = REGISTRY.register("nanoka_phone",()->new PhoneItem("nanoka_phone"));
        MAAGO_PHONE = REGISTRY.register("maago_phone",()->new PhoneItem("maago_phone"));
        ARISA_PHONE = REGISTRY.register("arisa_phone",()->new PhoneItem("arisa_phone"));
        MERURU_PHONE = REGISTRY.register("meruru_phone",()->new PhoneItem("meruru_phone"));
    }

}
