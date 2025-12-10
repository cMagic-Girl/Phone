package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.*;
import baios.magicgirl.phone.util.ChatHistorySql;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    // 服务端接收Payload时的处理方法
    public static void handleData(final ChatMessageData data, final IPayloadContext context) {
        // 1. 网络线程中执行轻量级计算（避免阻塞主线程）
        String chatOrigin = data.chatOrigin();
        String chatTarget = data.chatTarget();
        String message = data.message();
        int dayTimes = data.dayTimes();

        CompoundTag messageTag = new CompoundTag();
        messageTag.putString("chatOrigin", chatOrigin);
        messageTag.putString("chatTarget", chatTarget);
        messageTag.putString("message", message);
        messageTag.putInt("dayTimes", dayTimes);

        ChatHistorySql.writeChatHistory(messageTag);

        ServerPlayer player = (ServerPlayer) context.player();
        ChatMessageCallBack chatMessageCallBack = new ChatMessageCallBack(true);
        PacketDistributor.sendToPlayer(player, chatMessageCallBack);
        // 2. 若需操作游戏逻辑（如修改实体、世界），需切换到主线程
        context.enqueueWork(() -> {
                    // 主线程中执行的逻辑（如给玩家发送消息、修改玩家数据）
                    // player.sendSystemMessage(Component.literal("给" + chatTarget + "发送："+ message));
                    ChatMessageData payload = new ChatMessageData(chatOrigin,chatTarget,  message, dayTimes);
                    PacketDistributor.sendToAllPlayers(payload);
                })
                .exceptionally(e -> {  // 处理主线程任务的异常
                    // 异常时断开连接（可选）
                    context.disconnect(Component.translatable("mymod.network.error", e.getMessage()));
                    return null;
                });
    }


    public static void handleChatAppOpenGet(ChatAppOpenGet data, IPayloadContext iPayloadContext) {
        String phoneName = data.phoneName();
        CompoundTag lastMessageList = ChatHistorySql.readChatListLatestMessage(phoneName);
        ChatAppOpenData chatMessageData = new ChatAppOpenData(lastMessageList);
        PacketDistributor.sendToAllPlayers(chatMessageData);
    }

    public static void handleChatHistoryGet(ChatHistoryGet data, IPayloadContext iPayloadContext) {
        String chatTarget = data.chatTarget();
        String chatOrigin = data.chatOrigin();

        CompoundTag historyList = ChatHistorySql.readChatHistory(chatTarget, chatOrigin);

        ChatHistoryData chatHistoryData = new ChatHistoryData(historyList);
        ServerPlayer player = (ServerPlayer) iPayloadContext.player();
        PacketDistributor.sendToPlayer(player ,chatHistoryData);
    }
}