package baios.magicgirl.phone.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import baios.magicgirl.phone.menu.PhoneMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;



public class PhoneScreen extends AbstractContainerScreen<PhoneMenu> implements ModScreens.ScreenAccessor {
    // 标题
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private EditBox message;
    private boolean menuStateUpdateActive = false;
    private static final ResourceLocation texture = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen.png");



    public PhoneScreen(PhoneMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 320;
        this.imageHeight = 200;

    }
    @Override
    protected void init() {
        super.init();

        message = new EditBox(
                this.font, // 字体
                this.leftPos + 110, // X坐标（相对于GUI左侧偏移）
                this.topPos + 173, // Y坐标（相对于GUI顶部偏移）
                165, // 宽度
                18, // 高度
                Component.translatable("gui.magic_girl_phone.phone_screen.message")// 提示文本
        );
        message.setMaxLength(8192);
        message.setResponder(content -> {
            if (!menuStateUpdateActive)
                menu.sendMenuStateUpdate(entity, 0, "message", content, false);
        });
        this.addWidget(this.message);

        Button button_empty = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.send_button"), e -> {
        }).bounds(this.leftPos + 280, this.topPos + 173, 30, 18).build();
        this.addRenderableWidget(button_empty);
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 空实现，这样就不会渲染默认的'物品栏'标签和容器标题
        guiGraphics.drawString(this.font, Component.translatable("gui.magic_girl_phone.phone_screen.player_list"), 4, 4, -12829636, false);
        // 如果你想添加自定义标题，可以在这里添加
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // 启用混合模式以支持透明度
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);

        RenderSystem.setShaderColor(1, 1, 1, 1f);

        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        guiGraphics.blit(texture, this.leftPos + 110, this.topPos + 5, 0, 0, 200, 160, 200, 160);
        // 禁用混合模式
        RenderSystem.disableBlend();
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        message.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    // 处理按键输入
    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        if (message.isFocused())
            return message.keyPressed(key, b, c);
        return super.keyPressed(key, b, c);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String messageValue = message.getValue();
        super.resize(minecraft, width, height);
        message.setValue(messageValue);
    }
    // 自定义背景渲染
    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x00000000, 0x00000000);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // 调用父类的背景渲染方法（这会绘制游戏场景）
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
    }


    @Override
    public void updateMenuState(int elementType, String name, Object elementState) {
        menuStateUpdateActive = true;
        if (elementType == 0 && elementState instanceof String stringState) {
            if (name.equals("message"))
                message.setValue(stringState);
        }
        menuStateUpdateActive = false;
    }
    @Override
    public boolean isPauseScreen() {
        // 返回false表示这不是暂停界面（类似背包）
        return false;
    }
}

