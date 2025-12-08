package baios.magicgirl.phone.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatHistoryGet(String chatTarget, String chatOrigin) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChatHistoryGet> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_history_get"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChatHistoryGet> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ChatHistoryGet::chatTarget,
                    ByteBufCodecs.STRING_UTF8, ChatHistoryGet::chatOrigin,
                    ChatHistoryGet::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}