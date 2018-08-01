package com.liuchaoya.jartest.earmarktest.camera.decoding;// INTERNAL ERROR //

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.activity.CaptureFragment;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.BarcodeFormat;
import com.liuchaoya.jartest.earmarktest.camera.view.ViewfinderResultPointCallback;
import com.liuchaoya.jartest.earmarktest.camera.view.ViewfinderView;

import java.util.Vector;

import cn.ac.ict.earmarktest.camera.CameraManager;


public final class CaptureActivityHandler
        extends Handler {
    private static final String TAG = "CaptureActivityHandler";
    private final DecodeThread decodeThread;
    private final CaptureFragment fragment;
    private CaptureActivityHandler.State state;

    public CaptureActivityHandler(CaptureFragment paramCaptureFragment,
                                  Vector<BarcodeFormat> paramVector,
                                  String paramString,
                                  ViewfinderView paramViewfinderView,
                                  int decodeType) {
        this.fragment = paramCaptureFragment;
        this.decodeThread = new DecodeThread(paramCaptureFragment,
                paramVector,
                paramString,
                new ViewfinderResultPointCallback(paramViewfinderView),
                decodeType);
        this.decodeThread.start();
        this.state = CaptureActivityHandler.State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    private void restartPreviewAndDecode() {
        if (this.state == CaptureActivityHandler.State.SUCCESS) {
            this.state = CaptureActivityHandler.State.PREVIEW;
            CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            this.fragment.drawViewfinder();
        }
    }

    public void handleMessage(Message paramMessage) {
        if (paramMessage.what == R.id.auto_focus) {
            if (this.state == CaptureActivityHandler.State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else {
            if (paramMessage.what == R.id.restart_preview) {
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                return;
            }
            if (paramMessage.what == R.id.decode_succeeded) {
                Log.d(TAG, "Got decode succeeded message");
                this.state = CaptureActivityHandler.State.SUCCESS;
                Object localObject = paramMessage.getData();
                if (localObject == null) {
                    localObject = null;
                } else {
                    localObject = ((Bundle) localObject).getParcelable("barcode_bitmap");
                }
                this.fragment.handleDecode(new String((byte[]) paramMessage.obj), (Bitmap) localObject);
                return;
            }
            if (paramMessage.what == R.id.decode_failed) {
                this.state = CaptureActivityHandler.State.PREVIEW;
                CameraManager.get().requestPreviewFrame(this.decodeThread.getHandler(), R.id.decode);
                return;
            }
            if (paramMessage.what == R.id.return_scan_result) {
                Log.d(TAG, "Got return scan result message");
                this.fragment.getActivity().setResult(-1, (Intent) paramMessage.obj);
                this.fragment.getActivity().finish();
                return;
            }
            if (paramMessage.what == R.id.launch_product_query) {
                Log.d(TAG, "Got product query message");
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse((String) paramMessage.obj));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                this.fragment.getActivity().startActivity(intent);
            }
        }
    }

    public void quitSynchronously() {
        this.state = CaptureActivityHandler.State.DONE;
        CameraManager.get().stopPreview();
        Message.obtain(this.decodeThread.getHandler(), R.id.quit).sendToTarget();
        try {
            this.decodeThread.join();
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
            return;
        } catch (InterruptedException localInterruptedException) {
            for (; ; ) {
            }
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }
}