package baios.magicgirl.phone;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;


public class PhoneGui extends Screen {
    EditBox editBox;
    // button是按钮
    Button button;
    // 我们的GUI界面的背景图片的位置
    ResourceLocation FIRST_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MagicGirlPhone.MODID, "textures/gui/first_gui.png");
    // 这里的content是我们用于渲染文字的内容
    Component content = Component.translatable("gui." + MagicGirlPhone.MODID + ".first_gui_title");
    // 滑条，参考调节音量那个
    ExtendedSlider sliderBar;
    public PhoneGui(Component pTitle) {
        super(pTitle);
    }

    @Override
    protected void init() {

        // 创建一个输入框，并设置其位置、大小以及默认文本
        // x,y,width,height,component
        this.editBox = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, Component.translatable("gui." + MagicGirlPhone.MODID + ".first_gui"));
        this.addWidget(this.editBox);
        // button的应该通过builder获得，其中的起一个参数是按钮的名称，第二个参数是按钮按下之后会有什么操作的回调函数。
        // pos是设置按钮的位置
        // size是按钮的大小
        this.button = new Button.Builder(Component.translatable("gui." + MagicGirlPhone.MODID + ".first_gui.save"), pButton -> {
        }).pos(this.width / 2 - 40, 96).size(80, 20).build();
        this.addWidget(this.button);
        // 滑条，位置x，y，宽高w，h，滑条名称前缀，后缀，滑条的最小值，最大值，初始值，是否渲染文字
        this.sliderBar = new ExtendedSlider(this.width / 2 - 100, 120, 200, 10, Component.translatable("gui." + MagicGirlPhone.MODID + ".first_gui.slider"), Component.empty(), 0, 100, 0, true);
        this.addWidget(this.sliderBar);
        // 别忘记的调用super
        super.init();
    }

}

