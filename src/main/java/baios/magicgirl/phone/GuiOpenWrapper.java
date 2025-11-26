package baios.magicgirl.phone;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class GuiOpenWrapper {
    public static void openFirstGui(){
        Minecraft.getInstance().setScreen(new PhoneGui(Component.translatable("test")));
    }
}
