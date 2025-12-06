package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.ChatMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    // 服务端接收Payload时的处理方法
    public static void handleData(final ChatMessage data, final IPayloadContext context) {
        // 1. 网络线程中执行轻量级计算（避免阻塞主线程）
        String chatTarget = data.chatTarget();
        String chatOrigin = data.chatOrigin();
        String message = data.message();
        int dayTimes = data.dayTimes();
        System.out.println("服务端接收数据：" + chatOrigin+"向" + chatTarget + "发送了消息：" + message);

        // 2. 若需操作游戏逻辑（如修改实体、世界），需切换到主线程
        context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    // 主线程中执行的逻辑（如给玩家发送消息、修改玩家数据）
                    // player.sendSystemMessage(Component.literal("给" + chatTarget + "发送："+ message));

                    ChatMessage payload = new ChatMessage("服务器"+chatTarget, chatOrigin, message,dayTimes);

                    PacketDistributor.sendToAllPlayers(payload);

                })
                .exceptionally(e -> {  // 处理主线程任务的异常
                    // 异常时断开连接（可选）
                    context.disconnect(Component.translatable("mymod.network.error", e.getMessage()));
                    return null;
                });
    }
}