package baios.magicgirl.phone.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// 自定义数据载体（以MyData为例，包含name和age）
public record MyData(String name, int age) implements CustomPacketPayload {
    // 1. 定义Payload的唯一标识（命名空间+路径，避免冲突）
    public static final CustomPacketPayload.Type<MyData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath( "magic_girl_phone", "my_data"));

    // 2. 定义编解码器：指定如何将数据写入/读取ByteBuf
    public static final StreamCodec<RegistryFriendlyByteBuf, MyData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,  // name的编码方式（UTF8字符串）
                    MyData::name,               // 取name字段用于编码
                    ByteBufCodecs.VAR_INT,      // age的编码方式（可变长度整数）
                    MyData::age,                // 取age字段用于编码
                    MyData::new                 // 解码时通过name+age构造MyData实例
            );

    // 3. 实现type()方法：返回唯一标识
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}