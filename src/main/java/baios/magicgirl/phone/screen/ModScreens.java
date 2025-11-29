package baios.magicgirl.phone.screen;

import baios.magicgirl.phone.menu.ModMenus;
import baios.magicgirl.phone.menu.PhoneMainMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ModScreens {
    @SubscribeEvent
    public static void clientLoad(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PHONE_MENU.get(), PhoneScreen::new);
        event.register(ModMenus.PHONE_MAIN_MENU.get(), PhoneMainScreen::new);
    }

    public interface ScreenAccessor {
        void updateMenuState(int elementType, String name, Object elementState);
    }
}
