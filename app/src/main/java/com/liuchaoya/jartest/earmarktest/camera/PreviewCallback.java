package com.liuchaoya.jartest.earmarktest.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

public final class PreviewCallback implements Camera.PreviewCallback {
    private static final String TAG = "PreviewCallback";
    private final CameraConfigurationManager configManager;
    private Handler previewHandler;
    private int previewMessage;
    private final boolean useOneShotPreviewCallback;

    PreviewCallback(CameraConfigurationManager paramCameraConfigurationManager, boolean paramBoolean) {
        this.configManager = paramCameraConfigurationManager;
        this.useOneShotPreviewCallback = paramBoolean;
    }

    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        Point localPoint = this.configManager.getCameraResolution();
        if (!this.useOneShotPreviewCallback) {
            paramCamera.setPreviewCallback(null);
        }
        if (this.previewHandler != null) {
            float[] arrayOfFloat = new float[3];
            paramCamera.getParameters().getFocusDistances(arrayOfFloat);
            this.previewHandler.obtainMessage(this.previewMessage, localPoint.x, localPoint.y, paramArrayOfByte).sendToTarget();
            this.previewHandler = null;
            return;
        }
        Log.d(TAG, "Got preview callback, but no handler for it");
    }

    public void setHandler(Handler paramHandler, int paramInt) {
        this.previewHandler = paramHandler;
        this.previewMessage = paramInt;
    }
}