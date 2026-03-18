#include <jni.h>
#include <android/log.h>

#define LOG_TAG "AstrumNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jint JNICALL
Java_com_novusforge_astrum_android_MainActivity_nativeOnCreate(JNIEnv *env, jobject thiz) {
    LOGI("Project Astrum: Native library loaded");
    return 0;
}

}
