package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.MyData;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
    // 服务端接收Payload时的处理方法
    public static void handleData(final MyData data, final IPayloadContext context) {
        // 1. 网络线程中执行轻量级计算（避免阻塞主线程）
        String userName = data.name();
        int userAge = data.age();
        System.out.println("服务端接收数据：" + userName + ", " + userAge);

        // 2. 若需操作游戏逻辑（如修改实体、世界），需切换到主线程
        context.enqueueWork(() -> {
                    // 主线程中执行的逻辑（如给玩家发送消息、修改玩家数据）
                    context.player().sendSystemMessage(Component.literal("你发送的消息时：" + userAge));
                })
                .exceptionally(e -> {  // 处理主线程任务的异常
                    // 异常时断开连接（可选）
                    context.disconnect(Component.translatable("mymod.network.error", e.getMessage()));
                    return null;
                });
    }
}