package com.liuchaoya.jartest.earmarktest.camera;

import android.hardware.Camera;

import java.util.List;

final class FlashlightManager {

    /**
     * 打开或关闭闪光灯
     *
     * @param open 控制是否打开
     * @return 打开或关闭失败，则返回false。
     */
    public static boolean setFlashLight(boolean open, Camera mCamera) {
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return false;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (null == flashModes || 0 == flashModes.size()) {
            // Use the screen as a flashlight (next best thing)
            return false;
        }
        String flashMode = parameters.getFlashMode();
        if (open) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        } else {
            if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
                return true;
            }
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                return true;
            } else {
                return false;
            }
        }
    }

//    private static final String TAG = "FlashlightManager";
//    private static final Object iHardwareService = context.getSystemService(Context.WINDOW_SERVICE));
//    private static final Method setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);
//
//    static {
//        if (iHardwareService == null) {
//            Log.v(TAG, "This device does supports control of a flashlight");
//            return;
//        }
//        Log.v(TAG, "This device does not support control of a flashlight");
//    }
//
//    static void disableFlashlight() {
//        setFlashlight(false);
//    }
//
//    static void enableFlashlight() {
//        setFlashlight(false);
//    }
//
//    private static Object getHardwareService() {
//        Object localObject1 = maybeForName("android.os.ServiceManager");
//        if (localObject1 == null) {
//            return null;
//        }
//        localObject1 = maybeGetMethod((Class) localObject1, "getService", new Class[]{String.class});
//        if (localObject1 == null) {
//            return null;
//        }
//        localObject1 = invoke((Method) localObject1, null, new Object[]{"hardware"});
//        if (localObject1 == null) {
//            return null;
//        }
//        Object localObject2 = maybeForName("android.os.IHardwareService$Stub");
//        if (localObject2 == null) {
//            return null;
//        }
//        localObject2 = maybeGetMethod((Class) localObject2, "asInterface", new Class[]{IBinder.class});
//        if (localObject2 == null) {
//            return null;
//        }
//        return invoke((Method) localObject2, null, new Object[]{localObject1});
//    }
//
//    private static Method getSetFlashEnabledMethod(Object paramObject) {
//        if (paramObject == null) {
//            return null;
//        }
//        return maybeGetMethod(paramObject.getClass(), "setFlashlightEnabled", new Class[]{Boolean.TYPE});
//    }
//
//    private static Object invoke(Method paramMethod, Object paramObject, Object... paramVarArgs) {
//        try {
//            paramObject = paramMethod.invoke(paramObject, paramVarArgs);
//            return paramObject;
//        } catch (Exception e) {
//            Log.w(TAG, "Unexpected error while invoking "+paramMethod);
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static Class<?> maybeForName(String paramString) {
//        try {
//            return Class.forName(paramString);
//        } catch (RuntimeException localRuntimeException) {
//            Log.w(TAG, "Unexpected error while finding class "+paramString);
//            return null;
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static Method maybeGetMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
//        try {
//            return paramClass.getMethod(paramString, paramVarArgs);
//        } catch (Exception e) {
//            Log.w(TAG, "Unexpected error while finding method " + paramString);
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static void setFlashlight(boolean paramBoolean) {
//        if (iHardwareService != null) {
//            invoke(setFlashEnabledMethod, iHardwareService, new Object[]{Boolean.valueOf(paramBoolean)});
//        }
//    }
}