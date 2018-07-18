package com.liuchaoya.jartest.earmarktest.camera.decoding;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

public final class FinishListener
  implements OnClickListener, OnCancelListener, Runnable
{
  private final Activity activityToFinish;
  
  public FinishListener(Activity paramActivity)
  {
    this.activityToFinish = paramActivity;
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    run();
  }
  
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    run();
  }
  
  public void run()
  {
    this.activityToFinish.finish();
  }
}


/* Location:              G:\Android_逆向\liuchaoya\动检通湖北\classes-dex2jar.jar!\cn\ac\ict\earmarktest\camera\decoding\FinishListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */