package com.liuchaoya.jartest.earmarktest.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

final class CameraConfigurationManager {
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final int DESIRED_SHARPNESS = 30;
    private static final String TAG = "CameraConfiguration";
    private static final int TEN_DESIRED_ZOOM = 300;
    private Point cameraResolution;
    private final Context context;
    private int previewFormat;
    private String previewFormatString;
    private Point screenResolution;
    private Camera.Size mCameraResolution;

    CameraConfigurationManager(Context paramContext) {
        this.context = paramContext;
    }

    private static int findBestMotZoomValue(CharSequence paramCharSequence, int paramInt) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(paramCharSequence)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return paramInt;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(paramInt - value) < Math.abs(paramInt - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    private static Point findBestPreviewSizeValue(CharSequence paramCharSequence, Point paramPoint) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(paramCharSequence)) {

            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.e(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;
            try {
                newY = Integer.parseInt(previewSize.substring(0, dimPosition));
                newX = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs(newX - paramPoint.x) + Math.abs(newY - paramPoint.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private static Point getCameraResolution(Parameters parameters, Point paramPoint) {
        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            Log.e(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, paramPoint);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point((paramPoint.x >> 3) << 3, (paramPoint.y >> 3) << 3);
        }
        parameters.getSupportedPreviewSizes();
        return cameraResolution;
    }

    public static int getDesiredSharpness() {
        return 30;
    }

    private void setFlash(Parameters paramParameters) {
        if ((Build.MODEL.contains("Behold II")) && (CameraManager.SDK_INT == 3)) {
            paramParameters.set("flash-value", 1);
        } else {
            paramParameters.set("flash-value", 2);
        }
        paramParameters.set("flash-mode", "off");
    }

    private void setZoom(Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
                Log.e(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        // if (maxZoomString != null || motZoomValuesString != null) {
        // parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        // }
        if (parameters.isZoomSupported()) {
            Log.e(TAG, "max-zoom:" + parameters.getMaxZoom());
            parameters.setZoom(parameters.getMaxZoom() / 10);
        } else {
            Log.e(TAG, "Unsupported zoom.");
        }
    }

    Point getCameraResolution() {
        return this.cameraResolution;
    }
    Camera.Size getCameraResolutionSize() {
        return mCameraResolution;
    }

    int getPreviewFormat() {
        return this.previewFormat;
    }

    String getPreviewFormatString() {
        return this.previewFormatString;
    }

    Point getScreenResolution() {
        return this.screenResolution;
    }

    void initFromCameraParameters(Camera paramCamera) {
        Parameters parameters = paramCamera.getParameters();
        this.previewFormat = parameters.getPreviewFormat();
        this.previewFormatString = parameters.get("preview-format");
        Object localObject = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Default preview format: ");
        localStringBuilder.append(this.previewFormat);
        localStringBuilder.append('/');
        localStringBuilder.append(this.previewFormatString);
        Log.d((String) localObject, localStringBuilder.toString());
        localObject = ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.screenResolution = new Point(((Display) localObject).getWidth(), ((Display) localObject).getHeight());
        localObject = TAG;
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Screen resolution: ");
        localStringBuilder.append(this.screenResolution);
        Log.d((String) localObject, localStringBuilder.toString());
        localObject = new Point();
        ((Point) localObject).x = this.screenResolution.x;
        ((Point) localObject).y = this.screenResolution.y;
        if (this.screenResolution.x < this.screenResolution.y) {
            ((Point) localObject).x = this.screenResolution.y;
            ((Point) localObject).y = this.screenResolution.x;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("screenX:");
        localStringBuilder.append(((Point) localObject).x);
        localStringBuilder.append("   screenY:");
        localStringBuilder.append(((Point) localObject).y);
        Log.i("#########", localStringBuilder.toString());
        this.cameraResolution = getCameraResolution(parameters, (Point) localObject);
        this.mCameraResolution = findCloselySize(cameraResolution.x, cameraResolution.y,
                parameters.getSupportedPreviewSizes());
        Log.d(TAG, ("Camera resolution: "+screenResolution));
    }

    protected Camera.Size findCloselySize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        Collections.sort(preSizeList, new CameraConfigurationManager.SizeComparator(surfaceWidth, surfaceHeight));
        return preSizeList.get(0);
    }

    void setDesiredCameraParameters(Camera paramCamera) {
        Parameters localParameters = paramCamera.getParameters();
        String str = TAG;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Setting preview size: ");
        localStringBuilder.append(this.cameraResolution);
        Log.d(str, localStringBuilder.toString());
//        localParameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
        localParameters.setPreviewSize(this.mCameraResolution.width, this.mCameraResolution.height);
        setFlash(localParameters);
        setZoom(localParameters);
        paramCamera.setDisplayOrientation(90);
        paramCamera.setParameters(localParameters);
    }

    /**
     * 预览尺寸与给定的宽高尺寸比较器。首先比较宽高的比例，在宽高比相同的情况下，根据宽和高的最小差进行比较。
     */
    private static class SizeComparator implements Comparator<Camera.Size> {

        private final int width;
        private final int height;
        private final float ratio;

        SizeComparator(int width, int height) {
            if (width < height) {
                this.width = height;
                this.height = width;
            } else {
                this.width = width;
                this.height = height;
            }
            this.ratio = (float) this.height / this.width;
        }

        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            int width1 = size1.width;
            int height1 = size1.height;
            int width2 = size2.width;
            int height2 = size2.height;

            float ratio1 = Math.abs((float) height1 / width1 - ratio);
            float ratio2 = Math.abs((float) height2 / width2 - ratio);
            int result = Float.compare(ratio1, ratio2);
            if (result != 0) {
                return result;
            } else {
                int minGap1 = Math.abs(width - width1) + Math.abs(height - height1);
                int minGap2 = Math.abs(width - width2) + Math.abs(height - height2);
                return minGap1 - minGap2;
            }
        }
    }
}
