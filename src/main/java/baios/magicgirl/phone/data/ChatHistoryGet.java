package baios.magicgirl.phone.data;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatHistoryGet () implements CustomPacketPayload {
    // 定义Payload的唯一标识（命名空间+路径，避免冲突）
    public static final CustomPacketPayload.Type<ChatHistoryGet> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_history_get"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
