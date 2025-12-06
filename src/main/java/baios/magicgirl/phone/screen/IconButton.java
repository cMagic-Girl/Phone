package baios.magicgirl.phone.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IconButton extends Button {
    private final ResourceLocation icon;

    public IconButton(int x, int y, int w, int h, ResourceLocation icon, OnPress onPress) {
        super(x, y, w, h, Component.empty(), onPress, DEFAULT_NARRATION);
        this.icon = icon;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        // 绘制自定义 PNG 图标
        graphics.blit(icon, this.getX(), this.getY(),
                0, 0, this.width, this.height,
                this.width, this.height);
    }
}
