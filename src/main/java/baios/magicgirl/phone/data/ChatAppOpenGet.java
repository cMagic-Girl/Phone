package baios.magicgirl.phone.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ChatAppOpenGet(String phoneName) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChatAppOpenGet> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "chat_app_open_get"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChatAppOpenGet> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    ChatAppOpenGet::phoneName,
                    ChatAppOpenGet::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
