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
    private final boolean isMine;


    // 状态
    private boolean isHovered;

    public ChatMessageEntry(ResourceLocation playerAvatar, String message, String name, boolean isMine) {
        this.playerAvatar = playerAvatar;

        // 如果 name 为空（用于后续行），就不绘制名字
        if (name == null || name.isEmpty()) {
            this.playerName = "";
        } else {
            this.playerName = I18n.get("gui.magic_girl_phone." + name);
        }

        int nameWidth = this.playerName.isEmpty() ? 0 : CharWidthCalculator.calculateCeilWidth(playerName);
        this.messageWidth = CharWidthCalculator.calculateChineseEquivalentWidth(message);
        this.isMine = isMine;

        this.messageLineDataMap = CharWidthCalculator.splitByWidthWithLineWidth(message);
        totalLines = messageLineDataMap.size();

        this.message = message;

        if (isMine) {
            this.playerNameX = (nameWidth == 2) ? 170 : 160;
            this.avatarX = 190;
            this.messageX = 187.2f - this.messageWidth * 8.9f;
        } else {
            this.avatarX = 2;
            this.playerNameX = 30;
            this.messageX = 30.0f;
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height,
                       int mouseX, int mouseY, boolean hovered, float partialTick) {
        this.isHovered = hovered;
        renderBackground(guiGraphics, left, top, width, height);
        RenderSystem.enableBlend();

        // 只有非空名字才绘制
        if (this.playerName != null && !this.playerName.isEmpty()) {
            guiGraphics.drawString(this.font, this.playerName, left + playerNameX, top + 3, 0xFFFFFF);
        }

        // 文本绘制（换行 + 占位行判断）
        int maxCharsPerLine = 20; // 保持和行宽设定一致(34?)
        int yText = top + 16;

        // 如果是占位消息（只有空格），直接跳过绘制文字，只占高度
        if (message.trim().isEmpty()) {
            yText += this.font.lineHeight;
        } else {
            for (int i = 0; i < message.length(); i += maxCharsPerLine) {
                int end = Math.min(i + maxCharsPerLine, message.length());
                String line = message.substring(i, end);

                int xText;
                if (isMine) {
                    int rightBoundary = left + avatarX - 3;
                    int lineWidthPx = font.width(line);
                    xText = rightBoundary - lineWidthPx;
                } else {
                    xText = left + 30;
                }

                guiGraphics.drawString(this.font, line, xText, yText, 0xFFFFFF, false);
                yText += this.font.lineHeight;
            }
        }

        // 只有非空头像才绘制
        if (this.playerAvatar != null) {
            guiGraphics.blit(this.playerAvatar, left + avatarX, top + 3, 0, 0, 25, 25, 25, 25);
        }

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
