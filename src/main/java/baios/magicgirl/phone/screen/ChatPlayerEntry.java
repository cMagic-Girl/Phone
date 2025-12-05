package baios.magicgirl.phone.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ChatPlayerEntry extends ContainerObjectSelectionList.Entry<ChatPlayerEntry> {
    private Button button;
    public ChatPlayerEntry() {
        super();
        this.button = Button.builder(Component.literal("11111"), e -> {

        }).bounds( 0, 0, 50, 20).build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        button.setX(left);
        button.setY(top);
        button.render(guiGraphics, mouseX,mouseY, partialTick);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(this.button);
    }



    @Override
    public List<? extends GuiEventListener> children() {
        return List.of(this.button);
    }
}

