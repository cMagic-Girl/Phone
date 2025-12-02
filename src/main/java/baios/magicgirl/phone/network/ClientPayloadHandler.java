package baios.magicgirl.phone.network;

import baios.magicgirl.phone.data.MyData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    // 方法签名必须匹配：MyData + IPayloadContext，静态方法，void返回值
    public static void handleData(MyData data, IPayloadContext context) {
        // 客户端处理逻辑
    }
}
