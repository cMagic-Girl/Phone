package baios.magicgirl.phone.util;

import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * NeoForge 1.21.1 NBT 工具类（核心支持 String ↔ NBT 互转）
 * 1. 基础：String ↔ TAG_String
 * 2. 进阶：结构化字符串 ↔ TAG_Compound/TAG_List
 * 3. 扩展：NBT 二进制 ↔ Base64 字符串（文本传输/存储）
 */
public class NbtStringManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("NbtStringManager");
    private static final String MOD_ID = "magic_girl_phone"; // 你的模组ID

    // ==================== 基础场景：String ↔ TAG_String ====================

    /**
     * 普通字符串 → NBT 字符串标签（TAG_String）
     */
    public static StringTag stringToNbtTag(String content) {
        return StringTag.valueOf(content == null ? "" : content);
    }

    /**
     * NBT 字符串标签（TAG_String）→ 普通字符串
     */
    public static String nbtTagToString(StringTag tag) {
        return tag == null ? "" : tag.getAsString();
    }

    // ==================== 进阶场景：结构化字符串 ↔ TAG_Compound ====================

    /**
     * 结构化字符串（如 "玩家名:Steve|消息:Hello NBT|时间:2025-12-08"）→ TAG_Compound
     * 自定义分隔符：行分隔符 | ，键值分隔符 :
     */
    public static CompoundTag structuredStringToCompound(String structuredStr, String lineSep, String kvSep) {
        CompoundTag compound = new CompoundTag();
        if (structuredStr == null || structuredStr.isEmpty()) {
            return compound;
        }

        // 拆分行
        String[] lines = structuredStr.split(lineSep);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // 拆分键值
            String[] kv = line.split(kvSep, 2); // 仅拆分第一个分隔符
            if (kv.length < 2) continue;

            String key = kv[0].trim();
            String value = kv[1].trim();

            // 自动识别值类型（支持字符串/数字/布尔）
            putValueToCompound(compound, key, value);
        }
        return compound;
    }

    /**
     * TAG_Compound → 结构化字符串（如 "玩家名:Steve|消息:Hello NBT|时间:2025-12-08"）
     */
    public static String compoundToStructuredString(CompoundTag compound, String lineSep, String kvSep) {
        StringBuilder sb = new StringBuilder();
        for (String key : compound.getAllKeys()) {
            Tag tag = compound.get(key);
            if (tag == null) continue;

            // 转换 NBT 标签为字符串值
            String valueStr = switch (tag.getId()) {
                case Tag.TAG_STRING -> ((StringTag) tag).getAsString();
                case Tag.TAG_INT -> String.valueOf(((IntTag) tag).getAsInt());
                case Tag.TAG_LONG -> String.valueOf(((LongTag) tag).getAsLong());
                case Tag.TAG_FLOAT -> String.valueOf(((FloatTag) tag).getAsFloat());
                case Tag.TAG_DOUBLE -> String.valueOf(((DoubleTag) tag).getAsDouble());
                case Tag.TAG_BYTE -> String.valueOf(((ByteTag) tag).getAsByte() != 0); // 布尔转字符串
                default -> tag.getAsString(); // 其他类型默认转为字符串
            };

            // 拼接键值对
            if (sb.length() > 0) {
                sb.append(lineSep);
            }
            sb.append(key).append(kvSep).append(valueStr);
        }
        return sb.toString();
    }

    // ==================== 扩展场景：NBT 二进制 ↔ Base64 字符串（文本传输/存储） ====================

    /**
     * NBT 标签 → Base64 字符串（二进制转文本，便于传输/存储为文本文件）
     */
    public static String nbtToBase64String(Tag tag) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed((CompoundTag) tag, baos); // 压缩 NBT 二进制
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes); // 转为 Base64 字符串
        } catch (IOException e) {
            LOGGER.error("NBT 转 Base64 字符串失败", e);
            return "";
        }
    }


    // ==================== 辅助方法：自动识别值类型并写入 Compound ====================
    private static void putValueToCompound(CompoundTag compound, String key, String value) {
        // 尝试解析布尔值
        if ("true".equalsIgnoreCase(value)) {
            compound.putByte(key, (byte) 1);
            return;
        }
        if ("false".equalsIgnoreCase(value)) {
            compound.putByte(key, (byte) 0);
            return;
        }

        // 尝试解析整数
        try {
            int intValue = Integer.parseInt(value);
            compound.putInt(key, intValue);
            return;
        } catch (NumberFormatException ignored) {
        }

        // 尝试解析长整数
        try {
            long longValue = Long.parseLong(value);
            compound.putLong(key, longValue);
            return;
        } catch (NumberFormatException ignored) {
        }

        // 尝试解析浮点数
        try {
            float floatValue = Float.parseFloat(value);
            compound.putFloat(key, floatValue);
            return;
        } catch (NumberFormatException ignored) {
        }

        // 尝试解析双精度浮点数
        try {
            double doubleValue = Double.parseDouble(value);
            compound.putDouble(key, doubleValue);
            return;
        } catch (NumberFormatException ignored) {
        }

        // 默认作为字符串存储
        compound.putString(key, value);
    }

}