cmake_minimum_required(VERSION 3.22.1)

project("astrum-android")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(astrum-native SHARED
    astrum-native.cpp
)

find_library(log-lib log)

target_link_libraries(astrum-native
    ${log-lib}
)

target_include_directories(astrum-native PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}
)
