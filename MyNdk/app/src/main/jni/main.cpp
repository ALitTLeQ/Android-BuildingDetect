#include <string.h>
#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <opencv2/nonfree/nonfree.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <android/log.h>
#include <vector>
extern "C" {
      using namespace std;
      using namespace cv;

      #define  LOG_TAG    "HelloOpenCV"
      #define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

      JNIEXPORT jstring JNICALL Java_com_asdc_yzu_myndk_MainActivity_getStringFromNative
      (JNIEnv *env, jobject obj)
      {
            vector<KeyPoint> v;
            string b= "n";
            char a[5] = "abc";
            return env->NewStringUTF(a);
      }
      JNIEXPORT jint JNICALL Java_com_asdc_yzu_myndk_MainActivity_FindFeatures
      (JNIEnv *, jobject, jlong addrGray, jlong addrRgba, jlong addrTemp)
      {
            LOGI("RUUUUUUUUNNNNNNNNNN");
            Mat& mGr  = *(Mat*)addrGray;
            Mat& mRgb = *(Mat*)addrRgba;
            Mat& mTemp = *(Mat*)addrTemp;

            vector<KeyPoint> keypoints;
            Mat descriptors;
            SurfFeatureDetector surfDetector(30);
            FastFeatureDetector fastDetector(50);
            SurfDescriptorExtractor surfExtractor;
            surfDetector.detect(mGr,keypoints);
            //fastDetector.detect(mRgb,keypoints);
/*
            detector.detect(mRgb, keypoints);

            detector.compute(mRgb,keypoints, descriptors);
            */
            return keypoints.size();
      }
}

