package baios.magicgirl.phone.screen;

import baios.magicgirl.phone.data.ChatAppOpenGet;
import baios.magicgirl.phone.data.ChatHistoryGet;
import baios.magicgirl.phone.data.ChatMessageData;
import baios.magicgirl.phone.util.ChatHistoryNbtFile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
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
    private final String phoneOwer;
    private int phoneHeight, phoneWidth, phonePosX;
    public String test = "test";

    private boolean menuStateUpdateActive = false;


    // 纹理资源
    private static final ResourceLocation phoneBackground = ResourceLocation.parse("magic_girl_phone:textures/gui/background.png");
    private static final ResourceLocation phoneScreenMain = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen.png");
    private static final ResourceLocation phoneScreenFrame = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_frame.png");
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
        int Vscode = 3;
        int SETTING = 4;
    }

    // 滑动动画状态
    private boolean transitioning = false;       // 是否正在过渡
    private int transitionTicks = 0;             // 当前过渡帧数
    private int transitionDuration = 30;         // 动画总帧数（建议 10~16）
    private int currentScreenID = screenType.HOME; // 当前界面（用于动画绘制）
    private int targetScreenID = -1;             // 目标界面
    private boolean slideToApp = false;          // true=HOME→APP，false=APP→HOME
    // 位置缓存
    private int centerPhoneX; // 手机居中时的X
    private int leftPhoneX;   // 手机靠左时的X
    // 控件延迟显示标记
    private boolean pendingApplyScreen = false;

    // 当前屏幕ID
    private int screenID = screenType.HOME;

    // 聊天对象
    private String chatTarget = "default";
    private String chatTargetPhone = "default";

    // 聊天对象列表
    private List<String> playerList = List.of("ema_phone", "hiro_phone", "koko_phone", "sherii_phone", "hanna_phone", "anan_phone", "noa_phone", "reia_phone", "miria_phone", "nanoka_phone", "maago_phone", "arisa_phone", "meruru_phone");

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

    // 手机号映射
    private Map<String, String> phoneMap = Map.ofEntries(
            Map.entry("ema", "ema_phone"),
            Map.entry("hiro", "hiro_phone"),
            Map.entry("koko", "koko_phone"),
            Map.entry("sherii", "sherii_phone"),
            Map.entry("hanna", "hanna_phone"),
            Map.entry("anan", "anan_phone"),
            Map.entry("noa", "noa_phone"),
            Map.entry("reia", "reia_phone"),
            Map.entry("miria", "miria_phone"),
            Map.entry("nanoka", "nanoka_phone"),
            Map.entry("maago", "maago_phone"),
            Map.entry("arisa", "arisa_phone"),
            Map.entry("meruru", "meruru_phone")
    );

    // 头像映射
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

    public Map<String, String> lastMessageMap = new HashMap<>();

    // 以下为屏幕组件
    private Button home;
    private Button chatApp;
    private Button recorderApp;
    private Button vscodeApp;
    private Button sendMessageButton;
    private Button recorderButton;
    private EditBox messageInputBox;
    private ChatPlayerList chatPlayerList;
    private ChatHistoryList chatHistoryList;

    // 以下是组件定位
    private int timeLabelX = 180;


    // 构造
    public PhoneScreen(PhoneMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.entity = container.entity;


        // 获取手机名称
        this.phoneName = Objects.requireNonNullElse(container.phoneName, "phone");
        this.phoneOwer = playerMap.get(phoneName);

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
        // 根据玩家选择不同图标
        ResourceLocation icon = avatarMap.get(this.phoneName);
        this.home = new IconButton(
                this.leftPos + (this.imageWidth / 2) - 12,
                this.topPos + 167,
                20, 20,
                icon,
                e -> startTransition(screenType.HOME)
        );

        // App按钮
        // Chat
        ResourceLocation chatIcon = ResourceLocation.parse("magic_girl_phone:textures/gui/qq_icon.png");
        this.chatApp = new IconButton(
                this.leftPos + 137, this.topPos + 30,
                25, 25,
                chatIcon,
                e -> startTransition(screenType.CHAT)
        );


        // Recorder
        ResourceLocation recorderIcon = ResourceLocation.parse("magic_girl_phone:textures/gui/recorder_icon.png");
        this.recorderApp = new IconButton(
                this.leftPos + 167, this.topPos + 30,
                25, 25,
                recorderIcon,
                e -> startTransition(screenType.Recorder)
        );

        // vscode(先用recorder凑数)
        ResourceLocation vscodeIcon = ResourceLocation.parse("magic_girl_phone:textures/gui/vscode_icon.png");
        this.vscodeApp = new IconButton(
                this.leftPos + 197, this.topPos + 30,
                25, 25,
                vscodeIcon,
                e -> startTransition(screenType.Vscode)
        );

        // App内的组件
        this.sendMessageButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.send_button"), e -> {
            String msg = messageInputBox.getValue();
            if (!msg.isEmpty()) {
                ChatMessageData payload = new ChatMessageData(phoneName, this.chatTargetPhone, msg, (int) this.world.getDayTime());
                //entity.sendSystemMessage(Component.literal(playerMap.get(phoneName) +"向"+this.chatTarget+"发送消息了:"+ msg));
                messageInputBox.setValue("");
                // 2. 通过PacketDistributor发送到服务端
                PacketDistributor.sendToServer(payload);


            }
        }).bounds(this.leftPos + 320, this.topPos + 173, 30, 18).build();

        this.recorderButton = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.recorder_start"), e -> {
        }).bounds(this.leftPos + 210, this.topPos + 100, 60, 36).build();

        this.messageInputBox = new EditBox(
                this.font, // 字体
                this.leftPos + 130, // X坐标（相对于GUI左侧偏移）
                this.topPos + 173, // Y坐标（相对于GUI顶部偏移）
                185, // 宽度
                18, // 高度
                Component.translatable("gui.magic_girl_phone.phone_screen.message")// 提示文本
        );
        this.messageInputBox.setMaxLength(8192);
        this.messageInputBox.setResponder(content -> {
            //if (!menuStateUpdateActive)
            //menu.sendMenuStateUpdate(entity, 0, "message", content, false);
        });


        // 聊天列表
        this.chatPlayerList = new ChatPlayerList(this.minecraft, 102, 130, 0, 34);
        this.chatPlayerList.setX(this.leftPos + 8);
        this.chatPlayerList.setY(this.topPos + 25);


        //
        this.chatHistoryList = new ChatHistoryList(this.minecraft, 220, 150, 0, 34);
        this.chatHistoryList.setX(this.leftPos + 130);
        this.chatHistoryList.setY(this.topPos + 10);


        // 添加组件到屏幕
        this.addRenderableWidget(home);
        this.addRenderableWidget(chatApp);
        this.addRenderableWidget(recorderApp);
        this.addRenderableWidget(vscodeApp);
        this.addRenderableWidget(sendMessageButton);
        this.addRenderableWidget(recorderButton);
        this.addWidget(this.messageInputBox);
        this.addWidget(this.chatPlayerList);
        this.addWidget(this.chatHistoryList);

        //CompoundTag rootTag = ChatHistoryNbtFile.fileToNBT("phone_data.nbt");;
        //System.out.println(rootTag);
        //System.out.println(rootTag.get("phone_data"));


        // 初始化时显示
        this.screenComponentManager(screenID);

    }

    //聊天对象处理选中逻辑
    private void onPlayerSelected(ChatPlayerEntry selectedEntry) {
        if (screenID!=screenType.CHAT){
            return;
        }
        if (selectedEntry == null) {
            this.chatHistoryList.visible = false;
            this.messageInputBox.visible = false;
            this.sendMessageButton.visible = false;
            return;
        }
        this.chatHistoryList.visible = true;
        this.messageInputBox.visible = true;
        this.sendMessageButton.visible = true;

        if (!selectedEntry.getPlayerName().equals(this.chatTarget)) {
            this.chatHistoryList.clear();
            this.chatTarget = selectedEntry.getPlayerName();
            this.chatTargetPhone = phoneMap.get(selectedEntry.getPlayerId());
            ChatHistoryGet payload = new ChatHistoryGet(phoneName, chatTargetPhone);
            PacketDistributor.sendToServer(payload);
        }


    }

    public void chatHistoryUpdate() {
        this.chatHistoryList.clear();
        ChatHistoryGet payload = new ChatHistoryGet(phoneName, chatTargetPhone);
        PacketDistributor.sendToServer(payload);
    }

    public void chatHistoryAdd(String chatOrigin, String chatTarget, String chatMsg) {
        boolean isMine = chatOrigin.equals(this.phoneName);
        System.out.println("is Mine:"+isMine);
        this.chatHistoryList.addMessageEntry(avatarMap.get(chatOrigin), playerMap.get(chatOrigin), chatMsg, isMine);

    }

    // 切换屏幕
    protected void screenComponentManager(int screenID) {

        switch (screenID) {
            case screenType.HOME:
                this.screenID = screenType.HOME;

                this.chatApp.visible = true;
                this.recorderApp.visible = true;
                this.vscodeApp.visible = true;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = false;
                this.chatHistoryList.visible = false;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = false;

                this.timeLabelX = 200;
                this.home.setPosition(this.leftPos + (this.imageWidth / 2) - 12, this.topPos + 160);

                break;
            case screenType.CHAT:
                this.screenID = screenType.CHAT;

                this.chatApp.visible = false;
                this.recorderApp.visible = false;
                this.vscodeApp.visible = false;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = true;
                this.chatHistoryList.visible = false;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = false;

                this.timeLabelX = 80;
                this.home.setPosition(this.leftPos + (this.phoneWidth / 2) - 12, this.topPos + 160);

                ChatAppOpenGet chatAppOpenGet = new ChatAppOpenGet(phoneName);

                PacketDistributor.sendToServer(chatAppOpenGet);

                break;
            case screenType.Recorder:
                this.screenID = screenType.Recorder;

                this.chatApp.visible = false;
                this.recorderApp.visible = false;
                this.vscodeApp.visible = false;

                this.messageInputBox.visible = false;
                this.chatPlayerList.visible = false;
                this.chatHistoryList.visible = false;
                this.sendMessageButton.visible = false;

                this.recorderButton.visible = true;

                this.timeLabelX = 80;
                this.home.setPosition(this.leftPos + (this.phoneWidth / 2) - 12, this.topPos + 160);
                break;
        }
    }

    public void setChatList() {
        for (String phoneName : playerList) {
            if (Objects.equals(phoneName, this.phoneName)) {
                continue;
            }
            String playerName = playerMap.get(phoneName);
            String lastMessage = lastMessageMap.get(phoneName);
            //System.out.println("添加玩家：" + playerName);
            this.chatPlayerList.addPlayerEntry(avatarMap.get(phoneName), playerName, lastMessage, () -> this.onPlayerSelected(this.chatPlayerList.getSelectedEntry().orElse(null)));
        }
    }

    // 以下是屏幕渲染相关
    @Override
    public void renderTransparentBackground(GuiGraphics guiGraphics) {
        // 绘制透明的背景
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x00000000, 0x00000000);
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        g.blit(phoneBackground, this.leftPos, this.topPos,
                0, 0, this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        // 计算居中和左侧位置
        centerPhoneX = this.leftPos + this.imageWidth / 2 - this.phoneWidth / 2;
        leftPhoneX = this.leftPos;
        int rightPanelX = this.leftPos + this.phoneWidth + 3;

        if (transitioning && targetScreenID != -1) {
            float progress = (float) transitionTicks / (float) transitionDuration;
            if (progress > 1f) progress = 1f;

            int phoneX;
            if (slideToApp) {
                phoneX = (int) (centerPhoneX + (leftPhoneX - centerPhoneX) * progress);
            } else {
                phoneX = (int) (leftPhoneX + (centerPhoneX - leftPhoneX) * progress);
            }

            // 动画过程中只绘制手机，不绘制右侧面板
            g.blit(phoneScreenMain, phoneX, this.topPos, 0, 0,
                    this.phoneWidth, this.phoneHeight,
                    this.phoneWidth, this.phoneHeight);

            transitionTicks++;
            if (transitionTicks >= transitionDuration) {
                transitioning = false;
                currentScreenID = targetScreenID;
                targetScreenID = -1;
                pendingApplyScreen = true; // 动画完成后再应用控件可见性
                this.home.visible = true;
            }

        } else {
            // 非过渡状态
            if (currentScreenID == screenType.HOME) {
                g.blit(phoneScreenMain, centerPhoneX, this.topPos, 0, 0,
                        this.phoneWidth, this.phoneHeight,
                        this.phoneWidth, this.phoneHeight);
            } else {
                g.blit(phoneScreenMain, leftPhoneX, this.topPos, 0, 0,
                        this.phoneWidth, this.phoneHeight,
                        this.phoneWidth, this.phoneHeight);
                g.blit(phoneScreenFrame, rightPanelX, this.topPos, 0, 0,
                        this.imageWidth - this.phoneWidth - 3, this.phoneHeight,
                        this.imageWidth - this.phoneWidth - 3, this.phoneHeight);
            }
        }

        RenderSystem.disableBlend();

        // 动画完成后再应用控件可见性
        if (pendingApplyScreen) {
            pendingApplyScreen = false;
            this.screenID = currentScreenID;
            this.screenComponentManager(this.screenID);
        }
    }


    // 渲染标签
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        long dayTime = world.getDayTime();
        int hour = (int) (dayTime % 24000 / 1000) + 6;
        if (hour >= 24) {
            hour %= 24;
        }
        int minute = (int) (dayTime % 1000 / 20);
        String time = String.format("%02d:%02d", hour, minute);

        guiGraphics.drawString(this.font, Component.literal(time), this.timeLabelX, 15, -12829636, false);
        if (currentScreenID == screenType.CHAT && Objects.equals(chatTarget, "default")) {
            guiGraphics.drawString(
                    this.font,
                    Component.literal("请选择一个聊天对象"),
                    190, // 注意：坐标也需要相应调整
                    90,
                    0xFFFFFFFF
            );
        }

    }

    //每一帧都会调用该渲染方法
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        messageInputBox.render(guiGraphics, mouseX, mouseY, partialTicks);
        chatPlayerList.render(guiGraphics, mouseX, mouseY, partialTicks);
        chatHistoryList.render(guiGraphics, mouseX, mouseY, partialTicks);
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
        if (key == 257 && screenID == screenType.CHAT && !Objects.equals(this.chatTarget, "default")) {
            this.sendMessageButton.onPress();
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

    // 启动滑动过渡
    private void startTransition(int target) {

        // 当前屏幕和目标屏幕相同时，不启动过渡(防止无意义动画)
        if (currentScreenID == screenType.HOME && target == screenType.HOME) {
            return;
        }

        slideToApp = (currentScreenID == screenType.HOME);

        this.targetScreenID = target;
        this.transitioning = true;
        this.transitionTicks = 0;
        this.pendingApplyScreen = false;

        // 动画过程中隐藏所有控件，避免提前出现
        this.chatPlayerList.visible = false;
        this.messageInputBox.visible = false;
        this.sendMessageButton.visible = false;
        this.recorderButton.visible = false;
        this.chatApp.visible = false;
        this.recorderApp.visible = false;
        this.vscodeApp.visible = false;
        this.home.visible = false;
    }

}

