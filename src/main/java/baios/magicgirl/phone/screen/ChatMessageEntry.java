package baios.magicgirl.phone.screen;

import baios.magicgirl.phone.util.CharWidthCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class ChatMessageEntry extends ContainerObjectSelectionList.Entry<ChatMessageEntry> {

    private final Font font= Minecraft.getInstance().font;
    private final String playerName;
    private ResourceLocation playerAvatar;

    private String message="这是一条测试消息";

    private final float messageWidth;
    private int totalLines;
    private Map<Integer, CharWidthCalculator.LineData> messageLineDataMap;

    private final int avatarX ;
    private final int playerNameX ;
    private final float messageX;

    // 状态
    private boolean isHovered;

    public ChatMessageEntry(ResourceLocation playerAvatar, String message, String name,boolean isMine) {
        this.playerAvatar = playerAvatar;
        this.playerName = I18n.get("gui.magic_girl_phone." + name);
        int nameWidth = CharWidthCalculator.calculateCeilWidth(playerName);
        this.messageWidth = CharWidthCalculator.calculateChineseEquivalentWidth(message);

        // 按20单位宽度拆分消息，返回每行的内容+宽度
        this.messageLineDataMap = CharWidthCalculator.splitByWidthWithLineWidth(message);
        totalLines = messageLineDataMap.size();
        System.out.println(messageWidth);
        System.out.println(messageLineDataMap);
        System.out.println("lines:"+totalLines);

        this.message = message;
        if (isMine) {
            if (nameWidth ==2) {
                this.playerNameX = 170;
            }else {
                this.playerNameX = 160;
            }
            this.avatarX = 190;
            this.messageX = 187.2f - this.messageWidth*8.9f;
        }else {
            this.avatarX = 2;
            this.playerNameX = 30;
            this.messageX = 30.0f;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        this.isHovered = hovered;
        renderBackground(guiGraphics, left, top, width, height);
        RenderSystem.enableBlend(); // 透明头像适配
        guiGraphics.drawString(this.font, this.playerName, left + playerNameX, top + 3 , 0xFFFFFF);
        guiGraphics.drawString(this.font, message, left + this.messageX, top + 16.0f , 0xFFFFFF, false);
        guiGraphics.blit(this.playerAvatar, left + avatarX, top + 3, 0, 0, 25, 25, 25, 25);
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
