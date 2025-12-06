package baios.magicgirl.phone.network;


import baios.magicgirl.phone.MagicGirlPhone;
import baios.magicgirl.phone.data.ChatMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MagicGirlPhone.MODID)
public class NetworkRegistry {
    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        // 1. 创建PayloadRegistrar，指定网络协议版本（如"1"）
        PayloadRegistrar registrar = event.registrar("1");

        // 若需在网络线程处理（避免阻塞主线程），需显式设置：
        registrar = registrar.executesOn(HandlerThread.NETWORK);

        // 3. 注册Play阶段的双向Payload（客户端↔服务端）
        registrar.playBidirectional(
                ChatMessage.TYPE,                // Payload唯一标识
                ChatMessage.STREAM_CODEC,        // 编解码器
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,  // 客户端接收时的处理器
                        ServerPayloadHandler::handleData   // 服务端接收时的处理器
                )
        );

    }
}