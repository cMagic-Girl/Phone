package baios.magicgirl.phone.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.resources.ResourceLocation;

public class ChatHistoryList extends ContainerObjectSelectionList<ChatMessageEntry> {

    public ChatHistoryList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    public void addMessageEntry(ResourceLocation avatar, String playerName,String message) {
        ChatMessageEntry entry = new ChatMessageEntry(avatar, message,playerName,true);
        this.addEntry(entry);
    }
}
