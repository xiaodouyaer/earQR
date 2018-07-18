package com.liuchaoya.jartest.earmarktest.camera.decoding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.activity.CaptureFragment;
import com.synqe.Barcode.ResultObject.DecodeImageData_Result;

import org.json.JSONObject;

import java.io.IOException;

import cn.ac.ict.earmark.CodeUtils;
import cn.ac.ict.earmarktest.camera.CameraManager;
import cn.ac.ict.earmarktest.camera.PlanarYUVLuminanceSource;


final class DecodeHandler extends Handler {
    private static final String TAG = "DecodeHandler";
    private final CaptureFragment fragment;

    DecodeHandler(CaptureFragment paramCaptureFragment) {
        this.fragment = paramCaptureFragment;
    }

    private void decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        byte[] localObject1 = new byte[paramArrayOfByte.length];
        int i = 0;
        while (i < paramInt2) {
            int j = 0;
            while (j < paramInt1) {
                localObject1[(j * paramInt2 + paramInt2 - i - 1)] = paramArrayOfByte[(i * paramInt1 + j)];
                j += 1;
            }
            i += 1;
        }
        PlanarYUVLuminanceSource localPlanarYUVLuminanceSource = CameraManager.get().buildLuminanceSource(localObject1, paramInt2, paramInt1);
        Bitmap bitmap = localPlanarYUVLuminanceSource.renderCroppedGreyscaleBitmap();
        System.out.println(localPlanarYUVLuminanceSource);
        paramArrayOfByte = null;
        try {
            long l1 = System.currentTimeMillis();
            DecodeImageData_Result result = CodeUtils.analyzeBitmap(bitmap, true);
            long l2 = System.currentTimeMillis();
            Object localObject2 = new StringBuilder();
            ((StringBuilder) localObject2).append("Decode barcode (");
            ((StringBuilder) localObject2).append(l2 - l1);
            ((StringBuilder) localObject2).append(" ms):\n");
            Log.w(TAG, ((StringBuilder) localObject2).toString());
            if (result.Result == 0) {
                String str1 = String.valueOf(result.EM.Version);
                String str2 = String.valueOf(result.EM.AnimalType);
                String str3 = String.valueOf(result.EM.RegionSerial);
                String str4 = String.valueOf(result.EM.RegionCode);
                String str5 = String.valueOf(result.EM.EarMarkNumber);
                String str6 = String.valueOf(result.EM.ProducerCode);
                StringBuilder builder = new StringBuilder();
                builder.append("barcode is null Version: ");
                builder.append(str1);
                Log.e("barcode", builder.toString());
                builder = new StringBuilder();
                builder.append("barcode is null AnimalType: ");
                builder.append(str2);
                Log.e("barcode", builder.toString());
                builder = new StringBuilder();
                (builder).append("barcode is null RegionSerial: ");
                (builder).append(str3);
                Log.e("barcode", (builder).toString());
                builder = new StringBuilder();
                (builder).append("barcode is null RegionCode: ");
                (builder).append(str4);
                Log.e("barcode", (builder).toString());
                builder = new StringBuilder();
                (builder).append("barcode is null EarMarkNumber: ");
                (builder).append(str5);
                Log.e("barcode", (builder).toString());
                builder = new StringBuilder();
                (builder).append("barcode is null ProducerCode: ");
                (builder).append(str6);
                Log.e("barcode", (builder).toString());
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Version", str1);
                    jsonObject.put("AnimalType", str2);
                    jsonObject.put("RegionSerial", str3);
                    jsonObject.put("RegionCode", str4);
                    jsonObject.put("EarMarkNumber", str5);
                    jsonObject.put("ProducerCode", str6);
                    paramArrayOfByte = jsonObject.toString().getBytes();
                } catch (Exception localException) {
                    localException.printStackTrace();
                    paramArrayOfByte = "Result is null ".getBytes();
                }
            } else {
                Log.e("barcode", "Result is null ");
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        if (paramArrayOfByte != null) {
            Message obtain = Message.obtain(this.fragment.getHandler(), R.id.decode_succeeded, paramArrayOfByte);
            Bundle localBundle = new Bundle();
            localBundle.putParcelable("barcode_bitmap", localPlanarYUVLuminanceSource.renderCroppedGreyscaleBitmap());
            obtain.setData(localBundle);
            obtain.sendToTarget();
            return;
        }
        Message.obtain(this.fragment.getHandler(), R.id.decode_failed).sendToTarget();
        return;
    }

    public void handleMessage(Message paramMessage) {
        if (paramMessage.what == R.id.decode) {
            decode((byte[]) paramMessage.obj, paramMessage.arg1, paramMessage.arg2);
            Log.w(TAG, "............decoding............");
            return;
        }
        if (paramMessage.what == R.id.skip) {
            Message.obtain(this.fragment.getHandler(), R.id.decode_failed).sendToTarget();
            return;
        }
        if (paramMessage.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }
}