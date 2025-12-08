package baios.magicgirl.phone.data;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatMessageCallBack(boolean isSuccess) implements CustomPacketPayload {
    // 1. 定义Payload的唯一标识（命名空间+路径，避免冲突）
    public static final CustomPacketPayload.Type<ChatMessageCallBack> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_message_call_back"));

    // 2. 定义编解码器：指定如何将数据写入/读取ByteBuf
    public static final StreamCodec<RegistryFriendlyByteBuf, ChatMessageCallBack> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ChatMessageCallBack::isSuccess,
                    ChatMessageCallBack::new
            );

    // 3. 实现type()方法：返回唯一标识
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}