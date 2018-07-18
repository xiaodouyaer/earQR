package com.liuchaoya.jartest.earmarktest.camera.utilclass;

import android.content.Context;

public class DisplayUtil
{
  public static float density;
  public static int densityDPI;
  public static float screenHightDip;
  public static float screenWidthDip;
  public static int screenWidthPx;
  public static int screenhightPx;
  
  public static int dip2px(Context paramContext, float paramFloat)
  {
    return (int)(paramFloat * paramContext.getResources().getDisplayMetrics().density + 0.5F);
  }
  
  public static int px2dip(Context paramContext, float paramFloat)
  {
    return (int)(paramFloat / paramContext.getResources().getDisplayMetrics().density + 0.5F);
  }
}