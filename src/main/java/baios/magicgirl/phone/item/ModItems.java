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
        EMA_PHONE = Registries.register("ema_phone",()->new PhoneItem("ema_phone"));
        HIRO_PHONE = Registries.register("hiro_phone",()->new PhoneItem("hiro_phone"));
        KOKO_PHONE = Registries.register("koko_phone",()->new PhoneItem("koko_phone"));
        SHERII_PHONE = Registries.register("sherii_phone",()->new PhoneItem("sherii_phone"));
        HANNA_PHONE = Registries.register("hanna_phone",()->new PhoneItem("hanna_phone"));
        ANAN_PHONE = Registries.register("anan_phone",()->new PhoneItem("anan_phone"));
        NOA_PHONE = Registries.register("noa_phone",()->new PhoneItem("noa_phone"));
        REIA_PHONE = Registries.register("reia_phone",()->new PhoneItem("reia_phone"));
        MIRIA_PHONE = Registries.register("miria_phone",()->new PhoneItem("miria_phone"));
        NANOKA_PHONE = Registries.register("nanoka_phone",()->new PhoneItem("nanoka_phone"));
        MAAGO_PHONE = Registries.register("maago_phone",()->new PhoneItem("maago_phone"));
        ARISA_PHONE = Registries.register("arisa_phone",()->new PhoneItem("arisa_phone"));
        MERURU_PHONE = Registries.register("meruru_phone",()->new PhoneItem("meruru_phone"));
    }

}
