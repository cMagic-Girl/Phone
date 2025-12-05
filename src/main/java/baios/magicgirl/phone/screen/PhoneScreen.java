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

import java.util.*;


public class PhoneScreen extends AbstractContainerScreen<PhoneMenu> implements ModScreens.ScreenAccessor {

    private final Level world;
    private final Player entity;
    private final String phoneName;
    private int phoneHeight, phoneWidth, phonePosX;

    private boolean menuStateUpdateActive = false;

    // 资源
    private static final ResourceLocation phoneBackground = ResourceLocation.parse("magic_girl_phone:textures/gui/background.png");
    private static final ResourceLocation phoneScreenMain = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen.png");
    private static final ResourceLocation emaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/ema.png");
    private static final ResourceLocation hiroAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/hiro.png");
    private static final ResourceLocation kokoAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/koko.png");
    private static final ResourceLocation sheriiAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/sherii.png");
    private static final ResourceLocation hannaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/hanna.png");
    private static final ResourceLocation ananAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/anan.png");
    private static final ResourceLocation noaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/noa.png");
    private static final ResourceLocation reiaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/reia.png");
    private static final ResourceLocation miriaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/miria.png");
    private static final ResourceLocation nanokaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/nanoka.png");
    private static final ResourceLocation maagoAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/maago.png");
    private static final ResourceLocation arisaAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/arisa.png");
    private static final ResourceLocation meruruAvatar = ResourceLocation.parse("magic_girl_phone:textures/gui/player/meruru.png");

    // 屏幕ID
    protected interface screenType {
        int HOME = 0;
        int CHAT = 1;
        int Recorder = 2;
        int SETTING = 3;
    }

    private int screenID = screenType.HOME;

    // GUI状态
    private String chatTarget = "default";

    // 玩家信息映射
    protected Map<String, String> playerMap = Map.ofEntries(
            Map.entry("ema_phone", "ema"),
            Map.entry("hiro_phone", "hiro"),
            Map.entry("koko_phone", "koko"),
            Map.entry("sherii_phone", "sherii"),
            Map.entry("hanna_phone", "hanna"),
            Map.entry("anan_phone", "anan"),
            Map.entry("noa_phone", "noa"),
            Map.entry("reia_phone", "reia"),
            Map.entry("miria_phone", "miria"),
            Map.entry("nanoka_phone", "nanoka"),
            Map.entry("maago_phone", "maago"),
            Map.entry("arisa_phone", "arisa"),
            Map.entry("meruru_phone", "meruru")
    );

    protected Map<String, ResourceLocation> avatarMap = Map.ofEntries(
            Map.entry("ema_phone", emaAvatar),
            Map.entry("hiro_phone", hiroAvatar),
            Map.entry("koko_phone", kokoAvatar),
            Map.entry("sherii_phone", sheriiAvatar),
            Map.entry("hanna_phone", hannaAvatar),
            Map.entry("anan_phone", ananAvatar),
            Map.entry("noa_phone", noaAvatar),
            Map.entry("reia_phone", reiaAvatar),
            Map.entry("miria_phone", miriaAvatar),
            Map.entry("nanoka_phone", nanokaAvatar),
            Map.entry("maago_phone", maagoAvatar),
            Map.entry("arisa_phone", arisaAvatar),
            Map.entry("meruru_phone", meruruAvatar)
    );

    // 以下为屏幕组件
    private Button home;
    private Button chatApp;
    private Button recorderApp;
    private Button sendMessageButton;
    private Button recorderButton;
    private EditBox messageInputBox;
    private ChatPlayerList chatPlayerList;

    // 以下是组件定位
    private int timeLabelX = 212;
    private int phoneNameLabelY = 124;


    // 构造
    public PhoneScreen(PhoneMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.entity = container.entity;

        // 获取手机名称
        this.phoneName = Objects.requireNonNullElse(container.phoneName, "phone");

        // 设置GUI的宽高
        this.imageWidth = 360;
        this.imageHeight = 200;

        this.phoneWidth = 120;
        this.phoneHeight = 200;

        //这里初始化的位置其实存在问题，因为在blit前leftPos和topPos为0
        this.phonePosX = this.leftPos + this.imageWidth / 2 - this.phoneWidth / 2;


    }

    // 初始化
    @Override
    protected void init() {
        super.init();

        // Home按钮
        this.home = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.home"), e -> {
            this.screenComponentManager(screenType.HOME);
        }).bounds(this.leftPos + (this.imageWidth / 2) - 12, this.topPos + 175, 24, 24).build();
        this.addRenderableWidget(home);

        //App按钮
        this.chatApp = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.chat_app"), e -> {
            this.screenComponentManager(screenType.CHAT);
        }).bounds(this.leftPos + 130, this.topPos + 30, 24, 24).build();
        this.addRenderableWidget(chatApp);

        this.recorderApp = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.recorder_app"), e -> {
            this.screenComponentManager(screenType.Recorder);
        }).bounds(this.leftPos + 160, this.topPos + 30, 24, 24).build();
        this.addRenderableWidget(recorderApp);

        // App内的组件
        this.sendMessageButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.send_button"), e -> {
            String msg = messageInputBox.getValue();

            int age = 18;
            MyData payload = new MyData(msg, age);
            entity.sendSystemMessage(Component.literal(playerMap.get(phoneName) +"向"+this.chatTarget+"发送消息了:"+ msg));
            messageInputBox.setValue("");
            // 2. 通过PacketDistributor发送到服务端
            //PacketDistributor.sendToServer(payload);

        }).bounds(this.leftPos + 300, this.topPos + 173, 30, 18).build();
        this.addRenderableWidget(sendMessageButton);

        this.recorderButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.recorder_start"), e -> {


        }).bounds(this.leftPos + 210, this.topPos + 100, 60, 36).build();
        this.addRenderableWidget(recorderButton);


        this.messageInputBox = new EditBox(
                this.font, // 字体
                this.leftPos + 130, // X坐标（相对于GUI左侧偏移）
                this.topPos + 173, // Y坐标（相对于GUI顶部偏移）
                165, // 宽度
                18, // 高度
                Component.translatable("gui.magic_girl_phone.phone_screen.message")// 提示文本
        );
        this.messageInputBox.setMaxLength(8192);
        this.messageInputBox.setResponder(content -> {
            //if (!menuStateUpdateActive)
                //menu.sendMenuStateUpdate(entity, 0, "message", content, false);
        });
        this.addWidget(this.messageInputBox);

        this.chatPlayerList = new ChatPlayerList(this.minecraft, 111, 154, 0, 34);
        this.chatPlayerList.setX(this.leftPos + 4);
        this.chatPlayerList.setY(this.topPos + 15);

        for (String phoneName : playerMap.keySet()) {
            if (Objects.equals(phoneName, this.phoneName)) {
                continue;
            }
            String playerName = playerMap.get(phoneName);
            this.chatPlayerList.addPlayerEntry(avatarMap.get(phoneName), playerName, () -> this.onPlayerSelected(this.chatPlayerList.getSelectedEntry().orElse(null)));
        }


        // 添加列表到屏幕
        this.addWidget(this.chatPlayerList);

        // 初始化时显示
        this.screenComponentManager(screenID);

    }

    // ========== 核心：Screen处理选中逻辑（业务代码写在这里） ==========
    private void onPlayerSelected(ChatPlayerEntry selectedEntry) {
        if (selectedEntry == null) {
            this.chatTarget = "default";
            this.messageInputBox.visible = false;
            this.sendMessageButton.visible = false;
            System.out.println("未选中任何玩家");
            return;
        }
        this.messageInputBox.visible = true;
        this.sendMessageButton.visible = true;
        this.chatTarget = selectedEntry.getPlayerName();
    }

    protected void screenComponentManager(int screenID) {
        // 切换屏幕
        switch (screenID) {
            case screenType.HOME:
                this.screenID = screenType.HOME;

                this.chatApp.visible = true;
                this.recorderApp.visible = true;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = false;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = false;

                this.timeLabelX = 211;
                this.phoneNameLabelY = 124;
                this.home.setPosition(this.leftPos + (this.imageWidth / 2) - 12, this.topPos + 173);

                break;
            case screenType.CHAT:
                this.screenID = screenType.CHAT;

                this.chatApp.visible = false;
                this.recorderApp.visible = false;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = true;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = false;

                this.timeLabelX = 91;
                this.phoneNameLabelY = 4;
                this.home.setPosition(this.leftPos + (this.phoneWidth / 2) - 12, this.topPos + 173);
                break;
            case screenType.Recorder:
                this.screenID = screenType.Recorder;

                this.chatApp.visible = false;
                this.recorderApp.visible = false;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = false;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = true;

                this.timeLabelX = 91;
                this.phoneNameLabelY = 4;
                this.home.setPosition(this.leftPos + (this.phoneWidth / 2) - 12, this.topPos + 173);
                break;
        }
    }

    // 以下是屏幕渲染相关
    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
        // 绘制透明的背景
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x00000000, 0x00000000);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // 启用混合模式以支持透明度
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, phoneScreenMain);
        RenderSystem.setShaderColor(1, 1, 1, 1f);

        guiGraphics.blit(phoneBackground, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        //初次渲染手机后，leftPos和topPos才会有值，所以再设置一次手机左上角坐标
        this.phonePosX = this.leftPos + this.imageWidth / 2 - this.phoneWidth / 2;

        //渲染手机
        switch (screenID) {
            case screenType.HOME:
                guiGraphics.blit(phoneScreenMain, this.phonePosX, this.topPos, 0, 0, this.phoneWidth, this.phoneHeight, this.phoneWidth, this.phoneHeight);
                guiGraphics.blit(phoneScreenMain, this.phonePosX + 3, this.topPos + 3, 0, 0, this.phoneWidth - 6, this.phoneHeight - 30, this.phoneWidth - 6, this.phoneHeight - 30);
                break;
            case screenType.CHAT, screenType.Recorder:
                guiGraphics.blit(phoneScreenMain, this.leftPos, this.topPos, 0, 0, this.phoneWidth, this.phoneHeight, this.phoneWidth, this.phoneHeight);
                guiGraphics.blit(phoneScreenMain, this.leftPos + 3, this.topPos + 3, 0, 0, this.phoneWidth - 6, this.phoneHeight - 30, this.phoneWidth - 6, this.phoneHeight - 30);
                guiGraphics.blit(phoneScreenMain, this.leftPos + this.phoneWidth + 3, this.topPos, 0, 0, this.imageWidth - this.phoneWidth - 3, this.phoneHeight, this.imageWidth - this.phoneWidth - 3, this.phoneHeight);
                break;
        }


        //禁用混合模式
        RenderSystem.disableBlend();
        this.chatPlayerList.getSelectedEntry().ifPresent(entry -> {
            String tip = "当前选中：" + entry.getPlayerName();
            guiGraphics.drawString(
                    this.font,
                    Component.literal(tip),
                    10, 10, // 提示文本坐标
                    0xFFFFFF // 文本颜色
            );
        });
    }

    protected void renderScreen(GuiGraphics guiGraphics) {
        if (screenID == screenType.HOME) {
            guiGraphics.blit(phoneScreenMain, this.leftPos + 110, this.topPos + 5, 0, 0, 200, 160, 200, 160);
            // 禁用混合模式
        }
    }

    // 渲染标签
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 空实现，这样就不会渲染默认的'物品栏'标签和容器标题
        long dayTime = world.getDayTime();
        int hour = (int) (dayTime % 24000 / 1000) + 6;
        if (hour >= 24) {
            hour %= 24;
        }
        int minute = (int) (dayTime % 1000 / 20);
        String time = String.format("%02d:%02d", hour, minute);

        guiGraphics.drawString(this.font, Component.literal(time), this.timeLabelX, 6, -12829636, false);
        guiGraphics.drawString(this.font, Component.literal(this.phoneName), this.phoneNameLabelY, 6, -12829636, false);

    }

    //每一帧都会调用该渲染方法
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        messageInputBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        chatPlayerList.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }


    // 屏幕渲染部分结束

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String messageValue = messageInputBox.getValue();
        super.resize(minecraft, width, height);
        messageInputBox.setValue(messageValue);
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

