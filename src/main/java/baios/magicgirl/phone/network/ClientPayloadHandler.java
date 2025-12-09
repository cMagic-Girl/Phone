package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.ChatAppOpenData;
import baios.magicgirl.phone.data.ChatHistoryData;
import baios.magicgirl.phone.data.ChatMessageCallBack;
import baios.magicgirl.phone.data.ChatMessageData;
import baios.magicgirl.phone.item.ModItems;
import baios.magicgirl.phone.screen.PhoneScreen;
import baios.magicgirl.phone.util.NbtStringManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    // 方法签名必须匹配：MyData + IPayloadContext，静态方法，void返回值
    public static void handleData(ChatMessageData data, IPayloadContext context) {
        String chatTarget = data.chatTarget();
        // 客户端处理逻辑
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Screen currentScreen = minecraft.screen;
        if (localPlayer != null) {
            // 通过物品ID获取Item实例
            Item targetItem = ModItems.phoneItemMap.get(chatTarget).get();

            // 3. 遍历本地玩家背包检查
            Inventory inventory = localPlayer.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && stack.is(targetItem)) {
                    if (currentScreen instanceof PhoneScreen phoneScreen) {
                        phoneScreen.chatHistoryUpdate();
                    }else {
                        localPlayer.displayClientMessage(Component.literal("你的手机震动了一下"), false);
                    }
                }
            }

        }
    }

    public static void handleChatAppOpenData(ChatAppOpenData data, IPayloadContext iPayloadContext) {
        String Msg = data.chatMsg_1();
        System.out.println("Client Data:" + Msg);
        CompoundTag compound = NbtStringManager.structuredStringToCompound(Msg, "\\|", ":");

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Screen currentScreen = minecraft.screen;
        if (currentScreen instanceof PhoneScreen phoneScreen) {
            for (String key : compound.getAllKeys()) {
                System.out.println("Key Name:" + key + "Value:" + compound.getString(key));
                phoneScreen.lastMessageMap.put(key, compound.getString(key));
            }
            phoneScreen.setChatList();
        }

    }

    public static void handleChatHistoryData(ChatHistoryData data, IPayloadContext iPayloadContext) {
        CompoundTag history = data.chatMsg_1();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Screen currentScreen = minecraft.screen;
        if (currentScreen instanceof PhoneScreen phoneScreen) {
            int i;
            for (i = 1; i <= history.size(); i++) {
                String key = String.valueOf(i);
                if (history.contains(key, Tag.TAG_COMPOUND)) {
                    CompoundTag messageData = history.getCompound(key);
                    System.out.println("messageData:" + messageData);
                    String message = messageData.getString("message");
                    String chatOrigin = messageData.getString("chatOrigin");
                    String chatTarget = messageData.getString("chatTarget");
                    phoneScreen.chatHistoryAdd(chatOrigin, chatTarget, message);
                }
            }
        }

        System.out.println("Client Data:" + history);
    }

    public static void handleChatMessageCallBack(ChatMessageCallBack chatMessageCallBack, IPayloadContext iPayloadContext) {
        if (chatMessageCallBack.isSuccess()) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localPlayer = minecraft.player;
            Screen currentScreen = minecraft.screen;
            if (currentScreen instanceof PhoneScreen phoneScreen) {
                phoneScreen.chatHistoryUpdate();
            }
        }
    }
}
