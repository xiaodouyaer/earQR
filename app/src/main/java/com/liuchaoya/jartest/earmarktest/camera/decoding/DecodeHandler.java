package com.liuchaoya.jartest.earmarktest.camera.decoding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.activity.CaptureFragment;
import com.synqe.Barcode.ResultObject.DecodeImageData_Result;

import org.json.JSONObject;

import java.io.IOException;
import java.util.EnumMap;

import cn.ac.ict.earmark.CodeUtils;
import cn.ac.ict.earmarktest.camera.CameraManager;
import cn.ac.ict.earmarktest.camera.PlanarYUVLuminanceSource;


final class DecodeHandler extends Handler {
    private static final String TAG = "DecodeHandler";
    private final CaptureFragment fragment;
    private EnumMap<DecodeHintType, Object> hints;
    private int decodeType;
    private MultiFormatReader multiFormatReader;


    DecodeHandler(CaptureFragment paramCaptureFragment, int decodeType) {
        this.fragment = paramCaptureFragment;
        this.decodeType = decodeType;
        if (this.decodeType == 100){
            this.multiFormatReader = new MultiFormatReader();
            hints = new EnumMap<>(DecodeHintType.class);

        }
    }

    private void decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        if (decodeType == 100) {
            decodeQR(paramArrayOfByte, paramInt1, paramInt2);
        } else {
            decodeEAR(paramArrayOfByte, paramInt1, paramInt2);
        }
    }

    private void decodeQR(byte[] data, int paramInt1, int paramInt2) {
        byte[] localObject1 = new byte[data.length];
        int i = 0;
        while (i < paramInt2) {
            int j = 0;
            while (j < paramInt1) {
                localObject1[(j * paramInt2 + paramInt2 - i - 1)] = data[(i * paramInt1 + j)];
                j += 1;
            }
            i += 1;
        }

//        // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
//        byte[] rotatedData = new byte[data.length];
//        for (int y = 0; y < size.height; y++) {
//            for (int x = 0; x < size.width; x++)
//                rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
//        }
//
//        // 宽高也要调整
//        int tmp = size.width;
//        size.width = size.height;
//        size.height = tmp;

        Result rawResult = null;
        com.google.zxing.PlanarYUVLuminanceSource source = buildLuminanceSource(data, paramInt2, paramInt1);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }
        if (rawResult == null){
            System.out.println("11111Result is null");
        }else {
            System.out.println("22222" + rawResult.getText());
        }
    }

    private com.google.zxing.PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        // Go ahead and assume it's YUV rather than die.
        return new com.google.zxing.PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
    }

    private void decodeEAR(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
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