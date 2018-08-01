package com.liuchaoya.jartest.earmarktest.camera.decoding;

import android.os.Handler;
import android.os.Looper;

import com.liuchaoya.jartest.earmarktest.camera.activity.CaptureFragment;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.BarcodeFormat;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.ResultPointCallback;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;


final class DecodeThread extends Thread {
    public static final String BARCODE_BITMAP = "barcode_bitmap";
    private final CaptureFragment fragment;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;
    private int decodeType;

    DecodeThread(CaptureFragment paramCaptureFragment,
                 Vector<BarcodeFormat> paramVector,
                 String paramString,
                 ResultPointCallback paramResultPointCallback,
                 int decodeType) {
        this.fragment = paramCaptureFragment;
        this.handlerInitLatch = new CountDownLatch(1);
        this.decodeType = decodeType;
    }

    Handler getHandler() {
        try {
            this.handlerInitLatch.await();
            return this.handler;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return handler;
    }

    public void run() {
        Looper.prepare();
        this.handler = new DecodeHandler(this.fragment,decodeType);
        this.handlerInitLatch.countDown();
        Looper.loop();
    }
}