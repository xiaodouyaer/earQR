package com.liuchaoya.jartest.earmarktest.camera;

import android.graphics.Bitmap;
import cn.ac.ict.earmark.LuminanceSource;

public class BitmapLuminanceSource
  extends LuminanceSource
{
  private byte[] bitmapPixels;
  
  public BitmapLuminanceSource(Bitmap paramBitmap)
  {
    super(paramBitmap.getWidth(), paramBitmap.getHeight());
    int[] arrayOfInt = new int[paramBitmap.getWidth() * paramBitmap.getHeight()];
    this.bitmapPixels = new byte[paramBitmap.getWidth() * paramBitmap.getHeight()];
    paramBitmap.getPixels(arrayOfInt, 0, getWidth(), 0, 0, getWidth(), getHeight());
    int i = 0;
    while (i < arrayOfInt.length)
    {
      this.bitmapPixels[i] = ((byte)arrayOfInt[i]);
      i += 1;
    }
  }
  
  public byte[] getMatrix()
  {
    return this.bitmapPixels;
  }
  
  public byte[] getRow(int paramInt, byte[] paramArrayOfByte)
  {
    System.arraycopy(this.bitmapPixels, paramInt * getWidth(), paramArrayOfByte, 0, getWidth());
    return paramArrayOfByte;
  }
}


/* Location:              G:\Android_逆向\liuchaoya\动检通湖北\classes-dex2jar.jar!\cn\ac\ict\earmarktest\camera\BitmapLuminanceSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */