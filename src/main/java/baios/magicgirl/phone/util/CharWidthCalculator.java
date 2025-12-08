package baios.magicgirl.phone.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 字符宽度计算工具类（基于UI显示的宽度适配）
 * 核心规则（可独立配置）：
 * - 汉字/全角符号 = 1.0 单位宽度
 * - 英文字母 = 0.5 单位宽度
 * - 数字 = 0.5 单位宽度（可单独调整）
 * - 半角符号 = 0.5 单位宽度（可单独调整）
 */
public class CharWidthCalculator {

    // 常见半角符号
    private static final String HALF_WIDTH_SYMBOLS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ \t\n\r\\|\\/\\=\\-\\+\\*\\&\\^\\%\\$\\#\\@\\!\\(\\)\\[\\]\\{\\}\\|\\;\\:\\'\\\"\\,\\.\\/\\<\\>\\?`~";
    // 常见全角符号
    private static final String FULL_WIDTH_SYMBOLS = "！”＃￥％＆’（）＊＋，－．／：；＜＝＞？＠［＼］＾＿｀｛｜｝～　";

    // 宽度配置（可单独调整）
    private static final float CHINESE_FULL_WIDTH_WIDTH = 1.0f;  // 汉字/全角符号
    private static final float LETTER_WIDTH = 0.65f;              // 英文字母
    private static final float NUMBER_WIDTH = 0.6f;              // 数字
    private static final float HALF_WIDTH_SYMBOL_WIDTH = 0.2f;   // 半角符号
    private static final float DEFAULT_WIDTH = 0.5f;             // 兜底

    /**
     * 计算字符串的「汉字等效宽度」（返回float，保留1位小数）
     * @param str 要计算的字符串（支持汉字、英文、数字、符号）
     * @return 基于汉字宽度的等效长度（如："张三123" = 2*1 + 3*0.5 = 3.5）
     */
    public static float calculateChineseEquivalentWidth(String str) {
        if (str == null || str.isEmpty()) {
            return 0.0f;
        }

        float totalWidth = 0.0f;
        for (char c : str.toCharArray()) {
            totalWidth += getSingleCharWidth(c);
        }
        // 保留1位小数，避免精度问题
        return Math.round(totalWidth * 10) / 10.0f;
    }

    /**
     * 计算字符串的「汉字等效宽度」（返回整数，向上取整，适合坐标/行数计算）
     * @param str 要计算的字符串
     * @return 向上取整后的等效长度（如3.1→4，3.5→4，3.0→3）
     */
    public static int calculateCeilWidth(String str) {
        float width = calculateChineseEquivalentWidth(str);
        return (int) Math.ceil(width);
    }

    /**
     * 计算字符串的「汉字等效宽度」（返回整数，四舍五入，适合截断判断）
     * @param str 要计算的字符串
     * @return 四舍五入后的等效长度（如3.1→3，3.5→4，3.0→3）
     */
    public static int calculateRoundWidth(String str) {
        float width = calculateChineseEquivalentWidth(str);
        return (int) Math.round(width);
    }

    /**
     * 按指定宽度截断字符串（适配单行显示）
     * @param str 原始字符串
     * @param maxWidth 最大显示宽度（汉字等效宽度）
     * @return 截断后的字符串（末尾可加省略号）
     */
    public static String truncateByWidth(String str, float maxWidth) {
        if (str == null || str.isEmpty() || maxWidth <= 0) {
            return "";
        }

        float currentWidth = 0.0f;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            float charWidth = getSingleCharWidth(c);
            // 超过最大宽度则截断
            if (currentWidth + charWidth > maxWidth) {
                sb.append("..."); // 可选：添加省略号
                break;
            }
            sb.append(c);
            currentWidth += charWidth;
        }
        return sb.toString();
    }

    /**
     * 按指定宽度拆分字符串为多行（核心方法：返回每行内容+宽度）
     * @param str 原始字符串
     * @param lineMaxWidth 每行最大宽度（默认20单位，即20个汉字宽度）
     * @return Map<行号, LineData> 行号从0开始，LineData包含每行内容和宽度
     */
    public static Map<Integer, LineData> splitByWidthWithLineWidth(String str, float lineMaxWidth) {
        Map<Integer, LineData> lineDataMap = new LinkedHashMap<>(); // 保证行号有序
        if (str == null || str.isEmpty() || lineMaxWidth <= 0) {
            lineDataMap.put(0, new LineData("", 0.0f));
            return lineDataMap;
        }

        float currentLineWidth = 0.0f;
        StringBuilder currentLine = new StringBuilder();
        int lineNum = 0;

        for (char c : str.toCharArray()) {
            float charWidth = getSingleCharWidth(c);
            // 当前字符加入后超过行宽限制 → 换行
            if (currentLineWidth + charWidth > lineMaxWidth) {
                // 存储当前行（内容+宽度）
                lineDataMap.put(lineNum++, new LineData(currentLine.toString(), currentLineWidth));
                currentLine.setLength(0); // 清空当前行
                currentLineWidth = 0.0f;
            }
            // 处理换行符：直接换行
            if (c == '\n') {
                lineDataMap.put(lineNum++, new LineData(currentLine.toString(), currentLineWidth));
                currentLine.setLength(0);
                currentLineWidth = 0.0f;
                continue;
            }
            currentLine.append(c);
            currentLineWidth += charWidth;
        }
        // 加入最后一行
        if (currentLine.length() > 0) {
            lineDataMap.put(lineNum, new LineData(currentLine.toString(), currentLineWidth));
        }
        return lineDataMap;
    }

    /**
     * 重载：默认按20单位宽度拆分（20个汉字宽度）
     */
    public static Map<Integer, LineData> splitByWidthWithLineWidth(String str) {
        return splitByWidthWithLineWidth(str, 20.0f);
    }

    // ------------------------ 私有工具方法 ------------------------
    /**
     * 判断单个字符是否为汉字
     */
    private static boolean isChineseChar(char c) {
        return Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN;
    }

    /**
     * 判断单个字符是否为全角符号
     */
    private static boolean isFullWidthSymbol(char c) {
        return FULL_WIDTH_SYMBOLS.indexOf(c) != -1;
    }

    /**
     * 判断单个字符是否为英文字母
     */
    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * 判断单个字符是否为数字
     */
    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 判断单个字符是否为半角符号
     */
    private static boolean isHalfWidthSymbol(char c) {
        return HALF_WIDTH_SYMBOLS.indexOf(c) != -1;
    }

    /**
     * 获取单个字符的宽度（细分类型）
     */
    private static float getSingleCharWidth(char c) {
        if (isChineseChar(c) || isFullWidthSymbol(c)) {
            return CHINESE_FULL_WIDTH_WIDTH;
        } else if (isLetter(c)) {
            return LETTER_WIDTH;
        } else if (isNumber(c)) {
            return NUMBER_WIDTH;
        } else if (isHalfWidthSymbol(c)) {
            return HALF_WIDTH_SYMBOL_WIDTH;
        } else {
            return DEFAULT_WIDTH;
        }
    }

    /**
     * 每行消息的封装类（存储内容+宽度）
     */
    public static class LineData {
        private String content;    // 该行内容
        private float width;       // 该行的汉字等效宽度

        public LineData(String content, float width) {
            this.content = content;
            this.width = width;
        }

        // Getter & Setter
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }
    }
}