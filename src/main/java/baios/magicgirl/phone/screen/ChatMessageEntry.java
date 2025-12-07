package baios.magicgirl.phone.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ChatMessageEntry extends ContainerObjectSelectionList.Entry<ChatMessageEntry> {

    private final Font font= Minecraft.getInstance().font;
    private final String playerName;
    private ResourceLocation playerAvatar;

    private String message="这是一条测试消息";

    // 状态
    private boolean isMine;
    private boolean isHovered;

    public ChatMessageEntry(ResourceLocation playerAvatar, String message, String name,boolean isMine) {
        this.playerAvatar = playerAvatar;
        this.playerName = I18n.get("gui.magic_girl_phone." + name);
        this.message = message;
        this.isMine = isMine;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        this.isHovered = hovered;
        renderBackground(guiGraphics, left, top, width, height);
        RenderSystem.enableBlend(); // 透明头像适配
        guiGraphics.drawString(this.font, this.playerName, left + 28, top + 3 , 0xFFFFFF);
        guiGraphics.drawString(this.font, message, left + 28, top + 13 , 0xFFFFFF);
        guiGraphics.blit(this.playerAvatar, left + 2, top + 1, 0, 0, 25, 25, 25, 25);
        RenderSystem.disableBlend();
    }

    private void renderBackground(GuiGraphics guiGraphics, int left, int top, int width, int height) {
        int bgColor = 0;
        if(isHovered) {
            bgColor = 0x20FFFFFF; // 悬停态：白色半透
        }
        if (bgColor != 0) {
            guiGraphics.fill(left, top, left + width, top + height, bgColor);
        }
    }


    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of();
    }
    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
}
