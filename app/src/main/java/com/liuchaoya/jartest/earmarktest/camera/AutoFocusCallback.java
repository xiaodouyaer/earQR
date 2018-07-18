package com.liuchaoya.jartest.earmarktest.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final long AUTOFOCUS_INTERVAL_MS = 1000L;
    private static final String TAG = "AutoFocusCallback";
    private Handler autoFocusHandler;
    private int autoFocusMessage;

    public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
        if (this.autoFocusHandler != null) {
            Message message = this.autoFocusHandler.obtainMessage(this.autoFocusMessage, paramBoolean);
            this.autoFocusHandler.sendMessageDelayed(message, 1000L);
            this.autoFocusHandler = null;
            return;
        }
        Log.d(TAG, "Got auto-focus callback, but no handler for it");
    }

    public void setHandler(Handler paramHandler, int paramInt) {
        this.autoFocusHandler = paramHandler;
        this.autoFocusMessage = paramInt;
    }
}