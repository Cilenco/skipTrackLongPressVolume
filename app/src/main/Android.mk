LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT:=false
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay

LOCAL_PACKAGE_NAME := skipTrack
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
