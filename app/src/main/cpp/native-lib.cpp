#include <jni.h>
#include <string>
#include <vector>

/* 1. 引入官方头文件 */
#include "theengs-decoder/src/decoder.h"

/* 2. 工具：hex 字符串 → byte 数组 */
static std::vector<uint8_t> hexStringToBytes(const std::string &hex) {
    std::vector<uint8_t> bytes;
    size_t len = hex.length();
    if (len & 1) return {};                       // 长度必须偶数
    bytes.reserve(len / 2);
    for (size_t i = 0; i < len; i += 2) {
        uint8_t byte = static_cast<uint8_t>(
                           std::stoul(hex.substr(i, 2), nullptr, 16));
        bytes.push_back(byte);
    }
    return bytes;
}

/* 3. JNI 入口 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_theengsbg_TheengsJni_decode(
        JNIEnv *env,
        jclass /*clazz*/,
        jstring jHexData) {
    if (jHexData == nullptr)
        return env->NewStringUTF(R"({"error":"null_input"})");

    const char *cHex = env->GetStringUTFChars(jHexData, nullptr);
    if (cHex == nullptr)
        return env->NewStringUTF(R"({"error":"oom"})");

    std::string hexStr(cHex);
    env->ReleaseStringUTFChars(jHexData, cHex);

    std::vector<uint8_t> payload = hexStringToBytes(hexStr);
    if (payload.empty())
        return env->NewStringUTF(R"({"error":"bad_hex"})");

    /* 4. 官方解码器实例 */
    TheengsDecoder decoder;

    /* 5. 构造 ArduinoJson 文档 */
    StaticJsonDocument<512> doc;   // 512 字节足够常规广播
    JsonObject root = doc.to<JsonObject>();

    /* 6. 解码 */
    int ret = decoder.decodeBLEJson(root);

    /* 7. 序列化 JSON 并返回 */
    std::string jsonOut;
    if (ret == 1 && serializeJson(doc, jsonOut) > 0) {
        return env->NewStringUTF(jsonOut.c_str());
    } else {
        return env->NewStringUTF(R"({"error":"decode_fail"})");
    }
}
