package baios.magicgirl.phone.screen;

import baios.magicgirl.phone.menu.PhoneMainMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PhoneMainScreen extends AbstractContainerScreen<PhoneMainMenu> implements ModScreens.ScreenAccessor {
    private final Level world;
    private final int x, y, z;
    private final Player entity;
    private boolean menuStateUpdateActive = false;
    private static final ResourceLocation texture = ResourceLocation.parse("magic_girl_phone:textures/gui/phone_screen.png");

    public PhoneMainScreen(PhoneMainMenu container, Inventory inventory, Component text) {
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
        Button button_empty = Button.builder(Component.translatable("gui.magic_girl_phone.phone_screen.send_button"), e -> {
        }).bounds(this.leftPos + 280, this.topPos + 173, 30, 18).build();
        this.addRenderableWidget(button_empty);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // 启用混合模式以支持透明度
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);

        RenderSystem.setShaderColor(1, 1, 1, 1f);

        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        RenderSystem.disableBlend();
    }

    @Override
    public void updateMenuState(int elementType, String name, Object elementState) {

    }
}
