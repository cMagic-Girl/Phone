package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.ChatAppOpenData;
import baios.magicgirl.phone.data.ChatHistoryData;
import baios.magicgirl.phone.data.ChatMessageCallBack;
import baios.magicgirl.phone.data.ChatMessageData;
import baios.magicgirl.phone.item.ModItems;
import baios.magicgirl.phone.screen.PhoneScreen;

import baios.magicgirl.phone.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    // 方法签名必须匹配：MyData + IPayloadContext，静态方法，void返回值
    public static void handleData(ChatMessageData data, IPayloadContext context) {
        String chatOrigin = data.chatOrigin();
        String chatName = PhoneScreen.playerMap.get(chatOrigin);
        chatName= I18n.get("gui.magic_girl_phone." + chatName);
        String chatMsg = data.message();

        String chatTarget = data.chatTarget();
        String chatTargetName = PhoneScreen.playerMap.get(chatTarget);
        chatTargetName= I18n.get("gui.magic_girl_phone." + chatTargetName);

        // 客户端处理逻辑

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level =minecraft.level;
        LocalPlayer localPlayer = minecraft.player;
        boolean isSpectator = localPlayer.isSpectator();
        if (isSpectator) {
            localPlayer.displayClientMessage(Component.literal("「小手机私聊」"+chatName+" -> "+chatTargetName+" : "+chatMsg), false);
            return;
        }
        Screen currentScreen = minecraft.screen;
        if (localPlayer != null) {
            // 通过物品ID获取Item实例
            Item originItem = ModItems.phoneItemMap.get(chatOrigin).get();

            Item targetItem = ModItems.phoneItemMap.get(chatTarget).get();

            boolean isTarget = false;
            boolean isOrigin = false;
            // 3. 遍历本地玩家背包检查
            Inventory inventory = localPlayer.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && stack.is(targetItem)) {
                    isTarget = true;
                }
            }
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && stack.is(originItem)) {
                    isOrigin = true ;
                }
            }
            if (isOrigin && isTarget) {
                BlockPos playerPos = localPlayer.blockPosition();
                level.playSound(
                        localPlayer,
                        playerPos,
                        ModSounds.PHONE_BEEP_SOUND.get(),
                        SoundSource.PLAYERS,
                        1.0f,
                        1.0f

                );
                localPlayer.displayClientMessage(Component.literal("你收到了一条"+chatName+"发的消息"), false);

            } else if (!isOrigin && isTarget) {
                if ((currentScreen instanceof PhoneScreen phoneScreen)) {
                    phoneScreen.chatHistoryUpdate();
                }else {
                    BlockPos playerPos = localPlayer.blockPosition();
                    level.playSound(
                            localPlayer,
                            playerPos,
                            ModSounds.PHONE_BEEP_SOUND.get(),
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f

                    );
                    localPlayer.displayClientMessage(Component.literal("你收到了一条"+chatName+"发的消息"), false);
                }
            }


        }
    }

    public static void handleChatAppOpenData(ChatAppOpenData data, IPayloadContext iPayloadContext) {
        CompoundTag Msg = data.chatMsg_1();

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        Screen currentScreen = minecraft.screen;
        if (currentScreen instanceof PhoneScreen phoneScreen) {
            for (String key : Msg.getAllKeys()) {
                phoneScreen.lastMessageMap.put(key, Msg.getString(key));
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
            for (i = history.size(); i >=1 ; i--) {
                String key = String.valueOf(i);
                if (history.contains(key, Tag.TAG_COMPOUND)) {
                    CompoundTag messageData = history.getCompound(key);
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
