LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE :=opencv_java
LOCAL_SRC_FILES :=libopencv_java.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=SES_FaceRec
LOCAL_SRC_FILES :=libSES_FaceRec.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
OpenCV_CAMERA_MODULES:=off
OpenCV_INSTALL_MODULES:=on
OpenCV_LIB_TYPE:=SHARED
include ../../OpenCV-2.4.11-android-sdk/native/jni/OpenCV.mk
LOCAL_MODULE    := Temp
LOCAL_SRC_FILES := Android_Face_Verification.cpp
include $(BUILD_SHARED_LIBRARY)


#Close the Eclipse project (e.g. by quitting Eclipse).
#Open the .project file in a text or xml editor. There will be at least 2 <buildCommand> nodes that need to be removed. 
#Remove the <buildCommand> node with name org.eclipse.cdt.managedbuilder.core.genmakebuilder and all its children, 
#and the <buildCommand> node with name org.eclipse.cdt.managedbuilder.core.ScannerConfigBuilder and its children. 
#Finally, remove the lines:
#<nature>org.eclipse.cdt.core.cnature</nature> 
#<nature>org.eclipse.cdt.core.ccnature</nature> 
#<nature>org.eclipse.cdt.managedbuilder.core.managedBuildNature</nature>
# <nature>org.eclipse.cdt.managedbuilder.core.ScannerConfigNature</nature>
#Completely remove the .cproject file.
