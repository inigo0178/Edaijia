
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := edaijia
LOCAL_SRC_FILES := edlib.c

include $(BUILD_SHARED_LIBRARY)
