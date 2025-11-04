package com.example.theengsbg;

/**
 * Theengs Decoder 的极简 JNI 桥
 * 调用 native-lib.so 中的 C++ 解码器
 */
public final class TheengsJni {

    /* 加载 CMake 构建的 native-lib.so */
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 解码 BLE 原始广播包（十六进制字符串）
     *
     * @param hexData 原始广播完整 16 进制字符串，不区分大小写，允许空格（内部会去掉）
     * @return JSON 字符串，成功例：
     *         {"brand":"Xiaomi","model":"LYWSD03MMC","temp":23.4,"hum":55,"batt":87}
     *         失败例：
     *         {"error":"decode_fail"}
     */
    public static native String decode(String hexData);

    /* 工具方法：去掉所有空格，转大写，方便统一处理 */
    public static String sanitize(String hex) {
        if (hex == null) return "";
        return hex.replaceAll("\\s+", "").toUpperCase();
    }

    /* 私有构造，禁止实例化 */
    private TheengsJni() {}
}
