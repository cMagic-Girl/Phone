package baios.magicgirl.phone.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class ChatPlayerList extends ContainerObjectSelectionList<ChatPlayerEntry> {
    public ChatPlayerList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.addEntry(new ChatPlayerEntry());
        this.addEntry(new ChatPlayerEntry());
        this.setScrollAmount(2);
    }

    public void addEntrys() {
        super.addEntry(new ChatPlayerEntry());
    }
}
