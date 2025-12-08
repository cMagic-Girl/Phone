package baios.magicgirl.phone.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ChatPlayerEntry extends ContainerObjectSelectionList.Entry<ChatPlayerEntry> {

    private final Font font = Minecraft.getInstance().font;
    private final ResourceLocation playerAvatar;

    private final String playerId;
    private final String playerName;
    private String lastMessage;
    private int unreadCount = 1;

    // 状态
    private boolean isUnread;
    private boolean isHovered;
    private boolean isSelected = false;

    private final Consumer<ChatPlayerEntry> onClickCallback;

    public ChatPlayerEntry(ResourceLocation playerAvatar, String name, String lastMessage, Consumer<ChatPlayerEntry> onClickCallback) {
        super();
        this.playerAvatar = playerAvatar;
        this.playerId = name;
        this.playerName = I18n.get("gui.magic_girl_phone." + name);
        this.onClickCallback = onClickCallback;
        this.lastMessage = lastMessage;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        this.isHovered = hovered;
        renderBackground(guiGraphics, left, top, width, height);
        RenderSystem.enableBlend(); // 透明头像适配
        guiGraphics.drawString(this.font, playerName, left + 87, top + 3, 0xFFFFFF);
        guiGraphics.drawString(this.font, lastMessage, left + 87, top + 15, 0x808080);
        guiGraphics.blit(this.playerAvatar, left + 58, top + 1, 0, 0, 28, 28, 28, 28);
        RenderSystem.disableBlend();
    }

    private void renderBackground(GuiGraphics guiGraphics, int left, int top, int width, int height) {
        int bgColor = 0;
        if (this.isSelected) {
            bgColor = 0x405588FF; // 选中态：浅蓝色半透
        } else if (isHovered) {
            bgColor = 0x20FFFFFF; // 悬停态：白色半透
        }
        if (bgColor != 0) {
            guiGraphics.fill(left, top, left + width, top + height, bgColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // 仅响应左键
        if (button == 0) {
            // 标记当前项为选中态
            if (this.onClickCallback != null) {
                this.onClickCallback.accept(this);
            }
            return true;
        }
        return false;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public ResourceLocation getPlayerAvatar() {
        return playerAvatar;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
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

