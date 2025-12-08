package baios.magicgirl.phone.data;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatHistoryData(String chatMsg_1) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChatHistoryData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_history_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf,ChatHistoryData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ChatHistoryData::chatMsg_1,
                    ChatHistoryData::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}