package baios.magicgirl.phone.sound;

import baios.magicgirl.phone.MagicGirlPhone;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(
            BuiltInRegistries.SOUND_EVENT,
            MagicGirlPhone.MODID
    );


    public static final Supplier<SoundEvent> PHONE_BEEP_SOUND;



    static {
        PHONE_BEEP_SOUND =REGISTRY.register(
                "phone_beep_sound",
                () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MagicGirlPhone.MODID, "phone_beep_sound"))
        );
    }

    public static final DeferredSoundType PHONE_SOUND_TYPE =new DeferredSoundType(1.0f,1.0f,ModSounds.PHONE_BEEP_SOUND,ModSounds.PHONE_BEEP_SOUND,ModSounds.PHONE_BEEP_SOUND,ModSounds.PHONE_BEEP_SOUND,ModSounds.PHONE_BEEP_SOUND);
}

