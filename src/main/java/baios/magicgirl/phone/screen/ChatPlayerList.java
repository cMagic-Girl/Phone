package baios.magicgirl.phone.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ChatPlayerList extends ContainerObjectSelectionList<ChatPlayerEntry> {

    private ChatPlayerEntry selectedEntry;

    public ChatPlayerList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.centerListVertically = false;
    }

    @Override
    protected int getRowTop(int index) {
        // 移除+4，使顶部贴顶
        return this.getY() - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    public void setSelectedEntry(ChatPlayerEntry entry) {
        // 1. 取消原有选中项的状态
        if (this.selectedEntry != null) {
            this.selectedEntry.setSelected(false);
        }
        // 2. 设置新选中项的状态
        this.selectedEntry = entry;
        if (entry != null) {
            entry.setSelected(true);
            this.ensureVisible(entry); // 确保选中项在可视区域内
        }
    }

    public Optional<ChatPlayerEntry> getSelectedEntry() {
        return Optional.ofNullable(this.selectedEntry);
    }


    // 强制隐藏滚动条
    @Override
    protected boolean scrollbarVisible() {
        return false;
    }


    public void addPlayerEntry(ResourceLocation avatar, String playerName, Runnable onSelect) {
        ChatPlayerEntry entry = new ChatPlayerEntry(avatar, playerName, clickedEntry -> {
            // 1. 列表内同步选中态
            this.setSelectedEntry(clickedEntry);
            // 2. 触发Screen的业务逻辑
            onSelect.run();
        });
        this.addEntry(entry);
    }

    @Override
    protected boolean isSelectedItem(int index) {
        // 列表内部判定：当前索引的Entry是否为选中项
        ChatPlayerEntry entry = this.getEntry(index);
        return entry != null && entry.equals(this.selectedEntry);
    }
}
