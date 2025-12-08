package baios.magicgirl.phone.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatAppOpenData(String chatMsg_1
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChatAppOpenData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_app_open_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChatAppOpenData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ChatAppOpenData::chatMsg_1,
                    ChatAppOpenData::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
