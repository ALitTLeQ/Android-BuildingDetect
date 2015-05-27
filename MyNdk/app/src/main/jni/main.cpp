#include <string.h>
#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <vector>
extern "C" {
      using namespace std;
      using namespace cv;

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
            Mat& mGr  = *(Mat*)addrGray;
            Mat& mRgb = *(Mat*)addrRgba;
            Mat& mTemp = *(Mat*)addrTemp;


            vector<KeyPoint> v1, v2;
            //Detector
            FastFeatureDetector detector(50);
            detector.detect(mTemp, v1);
            detector.detect(mTemp, v2);

            //Extractor
            BriefDescriptorExtractor extractor(16);
            Mat desc_1, desc_2;
            extractor.compute(mGr, v1, desc_1);
            extractor.compute(mTemp, v2, desc_2);

            //cv::Ptr<cv::DescriptorMatcher> matcher = new cv::BFMatcher(cv::NORM_HAMMING,true);	// ØS©∫§«∞t°A?∫‚Hamming∂Z÷√
            BFMatcher matcher(NORM_HAMMING, true);
            vector<DMatch> matches;
            matcher.match(desc_1, desc_2, matches);



          vector<Point2f> pts1;
          vector<Point2f> pts2;
          for (int i = 0; i<matches.size(); ++i)
          {
              int i1 = matches[i].queryIdx;
              int i2 = matches[i].trainIdx;
              pts1.push_back(v1[i1].pt);
              pts2.push_back(v2[i2].pt);
          }

          Mat inlierMat;
          Mat F = cv::findFundamentalMat(pts1, pts2, FM_RANSAC, 1, 0.99, inlierMat);

          vector<DMatch> matchInlier;
          for (int i = 0; i<pts1.size(); ++i)
          {
              if (inlierMat.at<unsigned char>(i, 0) != 0)
              {
                  matchInlier.push_back(matches[i]);
              }
          }

            for( unsigned int i = 0; i < v1.size(); i++ )
            {
               const KeyPoint& kp = v1[i];
               //circle(mRgb, Point(kp.pt.x, kp.pt.y), 10, Scalar(0,0,255,255));
            }

            return matchInlier.size();
      }
}

