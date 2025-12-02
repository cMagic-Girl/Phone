package baios.magicgirl.phone.screen;

import baios.magicgirl.phone.data.MyData;
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
import net.neoforged.neoforge.network.PacketDistributor;


public class PhoneScreen extends AbstractContainerScreen<PhoneMenu> implements ModScreens.ScreenAccessor {
    // 标题
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private EditBox messageInputBox;
    private boolean menuStateUpdateActive = false;
    private static final ResourceLocation phoneScreenMain = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen.png");
    private static final ResourceLocation phoneScreenSidebar=ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen_sidebar.png");

    private int screenID = 0;


    public PhoneScreen(PhoneMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 355;
        this.imageHeight = 200;
    }



    @Override
    protected void init() {
        super.init();

        Button sendMessageButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.send_button"), e -> {
            String msg = messageInputBox.getValue();
            if (entity != null) {
                entity.displayClientMessage(Component.literal(msg), false);
            }
            int age =18;
            MyData payload = new MyData(msg, age);
            messageInputBox.setValue("");
            // 2. 通过PacketDistributor发送到服务端
            PacketDistributor.sendToServer(payload);

        }).bounds(this.leftPos + 280, this.topPos + 173, 30, 18).build();
        this.addRenderableWidget(sendMessageButton);

        Button recorderButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.recorder_start"), e -> {


        }).bounds(this.leftPos + 200, this.topPos + 100, 60, 36).build();
        this.addRenderableWidget(recorderButton);

        Button home = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.home"), e -> {
            this.screenID = 0;
            messageInputBox.visible = false;
            recorderButton.visible = false;
            sendMessageButton.visible = false;

        }).bounds(this.leftPos + 2, this.topPos + 3, 24, 24).build();
        this.addRenderableWidget(home);

        Button chatApp = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.chat_app"), e -> {
            this.screenID = 1;
            messageInputBox.visible=true;
            sendMessageButton.visible = true;
            String msg = messageInputBox.getValue();
            if (entity != null) {
                entity.displayClientMessage(Component.literal(msg), false);
            }

        }).bounds(this.leftPos +2, this.topPos + 28, 24, 24).build();
        this.addRenderableWidget(chatApp);

        Button recorderApp = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.recorder_app"), e -> {
            this.screenID = 2;

            messageInputBox.visible = false;
            sendMessageButton.visible = false;

            recorderButton.visible = true;
        }).bounds(this.leftPos +2, this.topPos + 53, 24, 24).build();
        this.addRenderableWidget(recorderApp);

        messageInputBox = new EditBox(
                this.font, // 字体
                this.leftPos + 110, // X坐标（相对于GUI左侧偏移）
                this.topPos + 173, // Y坐标（相对于GUI顶部偏移）
                165, // 宽度
                18, // 高度
                Component.translatable("gui.magic_girl_phone.phone_screen.message")// 提示文本
        );
        messageInputBox.setMaxLength(8192);
        messageInputBox.setResponder(content -> {
            if (!menuStateUpdateActive)
                menu.sendMenuStateUpdate(entity, 0, "message", content, false);
        });
        this.addWidget(this.messageInputBox);

        if (screenID == 0) {
            messageInputBox.visible = false;
            sendMessageButton.visible = false;
            recorderButton.visible = false;
        }
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 空实现，这样就不会渲染默认的'物品栏'标签和容器标题
        long dayTime = world.getDayTime();
        int hour = (int) (dayTime % 24000 / 1000);
        int minute = (int) (dayTime % 1000 / 20);
        String time = String.format("%02d:%02d", hour, minute);
        guiGraphics.drawString(this.font, Component.literal(time), 0, 195, -12829636, false);
        if (screenID == 1) {

        }

        // 如果你想添加自定义标题，可以在这里添加
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // 启用混合模式以支持透明度
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, phoneScreenMain);
        RenderSystem.setShaderColor(1, 1, 1, 1f);

        //渲染主要背景
        guiGraphics.blit(phoneScreenSidebar, this.leftPos, this.topPos, 0, 0, 30, this.imageHeight, 30, this.imageHeight);
        guiGraphics.blit(phoneScreenMain, this.leftPos + 32, this.topPos, 0, 0, 320, this.imageHeight, 320, this.imageHeight);

        //根据
        if (screenID == 1) {
            guiGraphics.blit(phoneScreenMain, this.leftPos + 110, this.topPos + 5, 0, 0, 200, 160, 200, 160);
            // 禁用混合模式
        }

        RenderSystem.disableBlend();
    }


    //每一帧都会调用该渲染方法
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        messageInputBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    // 处理按键输入
    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        //文本输入框
        if (messageInputBox.isFocused())
            return messageInputBox.keyPressed(key, b, c);
        return super.keyPressed(key, b, c);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String messageValue = messageInputBox.getValue();
        super.resize(minecraft, width, height);
        messageInputBox.setValue(messageValue);
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
                messageInputBox.setValue(stringState);
        }
        menuStateUpdateActive = false;
    }

    @Override
    public boolean isPauseScreen() {
        // 返回false表示这不是暂停界面（类似背包）
        return false;
    }
}

