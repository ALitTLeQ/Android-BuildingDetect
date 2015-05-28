LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= C:/OpenCV-2.4.10-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include C:/OpenCV-2.4.10-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := :=	nonfree_init.cpp			\
                        sift.cpp					\
                        surf.cpp                    \
                        main.cpp
LOCAL_LDLIBS += -llog -ldl
LOCAL_MODULE := MyLib
LOCAL_CFLAGS := -Werror -O3 -ffast-math

include $(BUILD_SHARED_LIBRARY)