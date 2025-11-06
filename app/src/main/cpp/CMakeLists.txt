# 最低 CMake 版本要与 Android Studio 自带的一致
cmake_minimum_required(VERSION 3.18.1)

# 工程名随意
project("theengs-jni")

# ------------------------------------------------------------------
# 1. 强制 C++17（Theengs Decoder 需要）
# ------------------------------------------------------------------
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ------------------------------------------------------------------
# 2. 把 Theengs Decoder 整个加进来
#    目录结构：
#      app/src/cpp/
#        ├── CMakeLists.txt   （本文件）
#        ├── native-lib.cpp
#        └── decoder/ （git submodule 拉下来的原仓）
# ------------------------------------------------------------------
add_subdirectory(decoder)

# ------------------------------------------------------------------
# 3. 编译我们自己的 JNI 封装
# ------------------------------------------------------------------
add_library(native-lib SHARED native-lib.cpp)

# ------------------------------------------------------------------
# 4. 链接 Theengs Decoder 的静态库 + log 库
#    目标名固定为 theengs-decoder（由子目录 CMake 导出）
# ------------------------------------------------------------------
target_link_libraries(native-lib
        -Wl,--whole-archive
        decoder        # 来自 add_subdirectory
        -Wl,--whole-archive
        log                    # Android 系统 log 库
)

# ------------------------------------------------------------------
# 5. 让 Android Studio 把头文件路径自动补全出来（可选）
# ------------------------------------------------------------------
target_include_directories(native-lib PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/decoder/src
        ${CMAKE_CURRENT_SOURCE_DIR}/decoder/src/arduino_json/src
)
