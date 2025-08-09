cmake_minimum_required(VERSION 3.4.1)

add_library(
    ggwave_jni
    SHARED
        ggwave_kotlin.cpp
        ggwave-waver-v1.5.2/src/ggwave.cpp
)

target_include_directories(
    ggwave_jni
    PRIVATE
        ggwave-waver-v1.5.2/include
        ggwave-waver-v1.5.2/include/ggwave
        ggwave-waver-v1.5.2/src
        ggwave-waver-v1.5.2/src/reed-solomon
)

target_link_libraries(
    ggwave_jni
    # log
)
