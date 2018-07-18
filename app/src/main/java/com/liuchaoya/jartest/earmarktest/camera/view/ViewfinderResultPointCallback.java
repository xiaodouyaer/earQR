package com.liuchaoya.jartest.earmarktest.camera.view;


import com.liuchaoya.jartest.earmarktest.camera.utilclass.ResultPoint;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.ResultPointCallback;

public final class ViewfinderResultPointCallback
  implements ResultPointCallback
{
  private final ViewfinderView viewfinderView;
  
  public ViewfinderResultPointCallback(ViewfinderView paramViewfinderView)
  {
    this.viewfinderView = paramViewfinderView;
  }
  
  public void foundPossibleResultPoint(ResultPoint paramResultPoint)
  {
    this.viewfinderView.addPossibleResultPoint(paramResultPoint);
  }
}
