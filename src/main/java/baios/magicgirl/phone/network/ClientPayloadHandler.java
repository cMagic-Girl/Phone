package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.ChatMessageData;
import baios.magicgirl.phone.item.ModItems;
import baios.magicgirl.phone.screen.PhoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    // 方法签名必须匹配：MyData + IPayloadContext，静态方法，void返回值
    public static void handleData(ChatMessageData data, IPayloadContext context) {
        // 客户端处理逻辑
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Screen currentScreen = minecraft.screen;
        if (currentScreen instanceof PhoneScreen phoneScreen) {
            if (localPlayer != null) {

                // 2. 通过物品ID获取Item实例
                Item targetItem = ModItems.HIRO_PHONE.get();

                // 3. 遍历本地玩家背包检查
                Inventory inventory = localPlayer.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack stack = inventory.getItem(i);
                    if (!stack.isEmpty() && stack.is(targetItem)) {
                        localPlayer.displayClientMessage(Component.literal("你已收到一条消息：" + data.message()),false);
                    }
                }
            }
            phoneScreen.test="服务器收到消息" +data.message();
        }
    }
}
