#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace cv;

extern "C" {
JNIEXPORT void JNICALL
Java_com_studioOgg_philip_chalna_MainActivity_ConvertRGBtoGray(JNIEnv *env,
                                                             jobject Instance,
                                                             jlong matAddrInput,
                                                             jlong matAddrResult) {
    //TODO
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;
    Mat gray;

    cvtColor(matInput, gray, CV_RGBA2GRAY);

    Mat sobel;
    Mat sobelX;
    Mat sobelY;

    Sobel(gray, sobelX, CV_8U, 1, 0);
    Sobel(gray, sobelY, CV_8U, 0, 1);
    sobel = (abs(sobelX) + abs(sobelY));
    sobel.copyTo(matResult);
}
}extern "C"
JNIEXPORT void JNICALL
Java_com_studioOgg_philip_chalna_Camera_CameraModel_sobel_1filter(JNIEnv *env, jobject instance,
                                                                  jlong matAddrInput,
                                                                  jlong matAddrResult) {
    //TODO
    Mat &matInput = *(Mat *) matAddrInput;
    Mat &matResult = *(Mat *) matAddrResult;
    Mat gray;

    cvtColor(matInput, gray, CV_RGBA2GRAY);

    Mat sobel;
    Mat sobelX;
    Mat sobelY;

    Sobel(gray, sobelX, CV_8U, 1, 0);
    Sobel(gray, sobelY, CV_8U, 0, 1);
    sobel = (abs(sobelX) + abs(sobelY));
    sobel.copyTo(matResult);
}