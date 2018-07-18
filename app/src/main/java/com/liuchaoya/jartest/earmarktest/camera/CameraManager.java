package com.liuchaoya.jartest.earmarktest.camera;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuchaoya.jartest.R;

import java.io.IOException;


public final class CameraManager {
    public static int FRAME_HEIGHT = -1;
    public static int FRAME_MARGINTOP = -1;
    public static int FRAME_WIDTH = -1;
    static final int SDK_INT = Integer.parseInt(Build.VERSION.SDK);
    private static final String TAG = "CameraManager";
    private static CameraManager cameraManager;
    private final AutoFocusCallback autoFocusCallback;
    private Camera camera;
    private final CameraConfigurationManager configManager;
    private final Context context;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private final PreviewCallback previewCallback;
    private boolean previewing;
    private final boolean useOneShotPreviewCallback;

    private CameraManager(Context paramContext) {
        this.context = paramContext;
        this.configManager = new CameraConfigurationManager(paramContext);
        boolean bool;
        if (Integer.parseInt(Build.VERSION.SDK) > 3) {
            bool = true;
        } else {
            bool = false;
        }
        this.useOneShotPreviewCallback = bool;
        this.previewCallback = new PreviewCallback(this.configManager, this.useOneShotPreviewCallback);
        this.autoFocusCallback = new AutoFocusCallback();
    }

    public static CameraManager get() {
        return cameraManager;
    }

    public static void init(Context paramContext) {
        if (cameraManager == null) {
            cameraManager = new CameraManager(paramContext);
        }
    }

    private void turnOff(Parameters paramParameters) {
        paramParameters.setFlashMode("off");
        this.camera.setParameters(paramParameters);
    }

    private void turnOn(Parameters paramParameters) {
        paramParameters.setFlashMode("torch");
        this.camera.setParameters(paramParameters);
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        Rect localRect = getFramingRectInPreview();
        int i = this.configManager.getPreviewFormat();
        String str = this.configManager.getPreviewFormatString();
        switch (i) {
            default:
                if ("yuv420p".equals(str)) {
                    return new PlanarYUVLuminanceSource(paramArrayOfByte, paramInt1, paramInt2, localRect.left, localRect.top, localRect.width(), localRect.height());
                }
                break;
            case 16:
            case 17:
                return new PlanarYUVLuminanceSource(paramArrayOfByte, paramInt1, paramInt2, localRect.left, localRect.top, localRect.width(), localRect.height());
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Unsupported picture format: ");
        builder.append(i);
        builder.append('/');
        builder.append(str);
        throw new IllegalArgumentException(builder.toString());
    }

    public void closeDriver() {
        if (this.camera != null) {
            FlashlightManager.setFlashLight(false,camera);
            this.camera.release();
            this.camera = null;
        }
    }

    public void flashHandler(ImageView paramImageView, TextView paramTextView) {
        FeatureInfo[] arrayOfFeatureInfo = this.context.getPackageManager().getSystemAvailableFeatures();
        int i = 0;
        int j = arrayOfFeatureInfo.length;
        while (i < j) {
            if (("android.hardware.camera.flash".equals(arrayOfFeatureInfo[i].name)) && (this.camera != null)) {
                Parameters localParameters = this.camera.getParameters();
                if ("off".equals(localParameters.getFlashMode())) {
                    turnOn(localParameters);
                    paramImageView.setBackgroundResource(R.drawable.open);
                    paramTextView.setText("关闭手电筒");
                } else if ("torch".equals(localParameters.getFlashMode())) {
                    paramImageView.setBackgroundResource(R.drawable.close_sdt);
                    turnOff(localParameters);
                    paramTextView.setText("打开手电筒");
                }
            }
            i += 1;
        }
    }

    public AutoFocusCallback getAutoFocusCallback() {
        return this.autoFocusCallback;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Context getContext() {
        return this.context;
    }

    public Rect getFramingRect() {
        Point localPoint = this.configManager.getScreenResolution();
        if (this.camera == null) {
            return null;
        }
        int j = (localPoint.x - FRAME_WIDTH) / 2;
        int i;
        if (FRAME_MARGINTOP != -1) {
            i = FRAME_MARGINTOP;
        } else {
            i = (int) (0.5F + 150.0F * Resources.getSystem().getDisplayMetrics().density);
        }
        this.framingRect = new Rect(j, i, FRAME_WIDTH + j, FRAME_HEIGHT + i);
        return this.framingRect;
    }

    public Rect getFramingRectInPreview() {
        if (this.framingRectInPreview == null) {
            Rect localRect = new Rect(getFramingRect());
//            Point localPoint1 = this.configManager.getCameraResolution();
//            Point localPoint2 = this.configManager.getScreenResolution();
            Camera.Size localPoint1 = this.configManager.getCameraResolutionSize();
            Point localPoint2 = this.configManager.getScreenResolution();
            localRect.left = (localRect.left * localPoint1.height / localPoint2.x);
            localRect.right = (localRect.right * localPoint1.height / localPoint2.x);
            localRect.top = (localRect.top * localPoint1.width / localPoint2.y);
            localRect.bottom = (localRect.bottom * localPoint1.width / localPoint2.y);
            this.framingRectInPreview = localRect;
        }
        return this.framingRectInPreview;
    }

    public PreviewCallback getPreviewCallback() {
        return this.previewCallback;
    }

    public boolean isPreviewing() {
        return this.previewing;
    }

    public boolean isUseOneShotPreviewCallback() {
        return this.useOneShotPreviewCallback;
    }

    public void openDriver(SurfaceHolder paramSurfaceHolder)
            throws IOException {
        if (this.camera == null) {
            this.camera = Camera.open();
            if (this.camera == null) {
                throw new IOException();
            }
            this.camera.setPreviewDisplay(paramSurfaceHolder);
            if (!this.initialized) {
                this.initialized = true;
                this.configManager.initFromCameraParameters(this.camera);
            }
            this.configManager.setDesiredCameraParameters(this.camera);
            FlashlightManager.setFlashLight(true,camera);
        }
    }

    public void requestAutoFocus(Handler paramHandler, int paramInt) {
        if ((this.camera != null) && (this.previewing)) {
            this.autoFocusCallback.setHandler(paramHandler, paramInt);
            float[] floats = new float[3];
            this.camera.getParameters().getFocusDistances(floats);
            this.camera.autoFocus(this.autoFocusCallback);
        }
    }

    public void requestPreviewFrame(Handler paramHandler, int paramInt) {
        if ((this.camera != null) && (this.previewing)) {
            this.previewCallback.setHandler(paramHandler, paramInt);
            if (this.useOneShotPreviewCallback) {
                this.camera.setOneShotPreviewCallback(this.previewCallback);
                return;
            }
            this.camera.setPreviewCallback(this.previewCallback);
        }
    }

    public void setPreviewing(boolean paramBoolean) {
        this.previewing = paramBoolean;
    }

    public void startPreview() {
        if ((this.camera != null) && (!this.previewing)) {
            this.camera.startPreview();
            this.previewing = true;
        }
    }

    public void stopPreview() {
        if ((this.camera != null) && (this.previewing)) {
            if (!this.useOneShotPreviewCallback) {
                this.camera.setPreviewCallback(null);
            }
            this.camera.stopPreview();
            this.previewCallback.setHandler(null, 0);
            this.autoFocusCallback.setHandler(null, 0);
            this.previewing = false;
        }
    }
}