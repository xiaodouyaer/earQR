package com.liuchaoya.jartest.earmarktest.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import cn.ac.ict.earmark.LuminanceSource;

public final class PlanarYUVLuminanceSource
        extends LuminanceSource {
    private final int dataHeight;
    private final int dataWidth;
    private final int left;
    private final int top;
    private final byte[] yuvData;

    public PlanarYUVLuminanceSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        super(paramInt5, paramInt6);
        if ((paramInt5 + paramInt3 <= paramInt1) && (paramInt6 + paramInt4 <= paramInt2)) {
            this.yuvData = paramArrayOfByte;
            this.dataWidth = paramInt1;
            this.dataHeight = paramInt2;
            this.left = paramInt3;
            this.top = paramInt4;
            return;
        }
        throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
    }

    public int getDataHeight() {
        return this.dataHeight;
    }

    public int getDataWidth() {
        return this.dataWidth;
    }

    public byte[] getMatrix() {
        int k = getWidth();
        int m = getHeight();
        if ((k == this.dataWidth) && (m == this.dataHeight)) {
            return this.yuvData;
        }
        int n = k * m;
        byte[] arrayOfByte1 = new byte[n];
        int j = this.top * this.dataWidth + this.left;
        int i1 = this.dataWidth;
        int i = 0;
        if (k == i1) {
            System.arraycopy(this.yuvData, j, arrayOfByte1, 0, n);
            return arrayOfByte1;
        }
        byte[] arrayOfByte2 = this.yuvData;
        while (i < m) {
            System.arraycopy(arrayOfByte2, j, arrayOfByte1, i * k, k);
            j += this.dataWidth;
            i += 1;
        }
        return arrayOfByte1;
    }

    public byte[] getRow(int paramInt, byte[] paramArrayOfByte) {
        if ((paramInt >= 0) && (paramInt < getHeight())) {
            int i = getWidth();
            byte[] arrayOfByte;
            if (paramArrayOfByte != null) {
                arrayOfByte = paramArrayOfByte;
                if (paramArrayOfByte.length >= i) {
                }
            } else {
                arrayOfByte = new byte[i];
            }
            int j = this.top;
            int k = this.dataWidth;
            int m = this.left;
            System.arraycopy(this.yuvData, (paramInt + j) * k + m, arrayOfByte, 0, i);
            return arrayOfByte;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Requested row is outside the image: ");
        builder.append(paramInt);
        throw new IllegalArgumentException(builder.toString());
    }

    public boolean isCropSupported() {
        return true;
    }

    public Bitmap renderCroppedGreyscaleBitmap() {
        int m = getWidth();
        int n = getHeight();
        int[] arrayOfInt = new int[m * n];
        byte[] localObject = this.yuvData;
        int j = this.top * this.dataWidth + this.left;
        int i = 0;
        while (i < n) {
            int k = 0;
            while (k < m) {
                arrayOfInt[(i * m + k)] = ((localObject[(j + k)] & 0xFF) * 65793 | 0xFF000000);
                k += 1;
            }
            j += this.dataWidth;
            i += 1;
        }
        Bitmap bitmap = Bitmap.createBitmap(m, n, Config.ARGB_8888);
        bitmap.setPixels(arrayOfInt, 0, m, 0, 0, m, n);
        return bitmap;
    }
}


/* Location:              G:\Android_逆向\liuchaoya\动检通湖北\classes-dex2jar.jar!\cn\ac\ict\earmarktest\camera\PlanarYUVLuminanceSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */