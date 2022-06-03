package com.suman.trucksharing.speechrec;

/**
 * Created by Maxwell on 14-Jan-18.
 */

public interface OnSpeechRecognitionPermissionListener {

    void onPermissionGranted();

    void onPermissionDenied();
}
