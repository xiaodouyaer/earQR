package com.liuchaoya.jartest.earmarktest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.activity.CaptureActivity;
import com.synqe.Barcode.ResultObject.DecodeImageData_Result;

import java.util.List;

import cn.ac.ict.earmark.CodeUtils;
import cn.ac.ict.earmarktest.ImageUtil;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;

public class MainActivity
        extends AppCompatActivity
        implements PermissionCallbacks {
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final int REQUEST_CAMERA_PERM = 101;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_IMAGE = 112;
    public Button button = null;
    public Button button2 = null;

    private void initView() {
        this.button = ((Button) findViewById(R.id.button));
        this.button2 = ((Button) findViewById(R.id.button2));
        this.button.setOnClickListener(new ButtonOnClickListener(this.button.getId()));
        this.button2.setOnClickListener(new ButtonOnClickListener(this.button2.getId()));
        verifyStoragePermissions(this);
    }

    private void onClick() {
        startActivityForResult(new Intent(getApplication(), CaptureActivity.class),200);
    }

    public static void verifyStoragePermissions(Activity paramActivity) {
        if (ActivityCompat.checkSelfPermission(paramActivity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(paramActivity, PERMISSIONS_STORAGE, 1);
        }
    }

    @AfterPermissionGranted(101)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, new String[]{"android.permission.CAMERA"})) {
            onClick();
            return;
        }
        EasyPermissions.requestPermissions(this, "需要请求camera权限", 101, new String[]{"android.permission.CAMERA"});
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent paramIntent) {
        if (requestCode == 200 && resultCode == -1 && paramIntent != null){
            String string = paramIntent.getStringExtra("Content");
            Toast.makeText(this,string,Toast.LENGTH_LONG).show();
            return;
        }

        if ((requestCode == 112) && (paramIntent != null)) {
            Uri data = paramIntent.getData();
            try {
                DecodeImageData_Result result = CodeUtils.analyzePicture(ImageUtil.getImageAbsolutePath(this, data));
                Bundle localBundle;
                if (result.Result == 0) {
                    paramIntent = new Intent();
                    paramIntent.setClass(this, ResultActivity.class);
                    localBundle = new Bundle();
                    localBundle.putString("Tips", "扫描解析成功！");
                    String str1 = String.valueOf(result.EM.Version);
                    String str2 = String.valueOf(result.EM.AnimalType);
                    String str3 = String.valueOf(result.EM.RegionSerial);
                    String str4 = String.valueOf(result.EM.RegionCode);
                    String str5 = String.valueOf(result.EM.EarMarkNumber);
                    String str6 = String.valueOf(result.EM.ProducerCode);
                    StringBuilder localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null Version: ");
                    localStringBuilder.append(str1);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null AnimalType: ");
                    localStringBuilder.append(str2);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null RegionSerial: ");
                    localStringBuilder.append(str3);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null RegionCode: ");
                    localStringBuilder.append(str4);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null EarMarkNumber: ");
                    localStringBuilder.append(str5);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("barcode is null ProducerCode: ");
                    localStringBuilder.append(str6);
                    Log.e("barcode", localStringBuilder.toString());
                    localStringBuilder = new StringBuilder();
                    localStringBuilder.append("Version: ");
                    localStringBuilder.append(str1);
                    localStringBuilder.append("\nAnimalType: ");
                    localStringBuilder.append(str2);
                    localStringBuilder.append("\nRegionSerial: ");
                    localStringBuilder.append(str3);
                    localStringBuilder.append("\nRegionCode: ");
                    localStringBuilder.append(str4);
                    localStringBuilder.append("\nEarMarkNumber: ");
                    localStringBuilder.append(str5);
                    localStringBuilder.append("\nProducerCode: ");
                    localStringBuilder.append(str6);
                    localBundle.putString("Content", localStringBuilder.toString());
                    paramIntent.putExtras(localBundle);
                    startActivity(paramIntent);
                    return;
                }
                if (result.Result == -1) {
                    paramIntent = new Intent();
                    paramIntent.setClass(this, ResultActivity.class);
                    localBundle = new Bundle();
                    localBundle.putString("Tips", "读取图片错误，请确保权限及文件格式正确！");
                    paramIntent.putExtras(localBundle);
                    startActivity(paramIntent);
                    return;
                }
                if (result.Result == -2) {
                    paramIntent = new Intent();
                    paramIntent.setClass(this, ResultActivity.class);
                    localBundle = new Bundle();
                    localBundle.putString("Tips", "解析二维码失败！");
                    paramIntent.putExtras(localBundle);
                    startActivity(paramIntent);
                    return;
                }
                paramIntent = new Intent();
                paramIntent.setClass(this, ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Tips", "未知错误！");
                paramIntent.putExtras(bundle);
                startActivity(paramIntent);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onPermissionsDenied(int paramInt, @NonNull List<String> paramList) {
        Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, paramList)) {
            // 用户拒绝了该权限，并且选中『不再询问』
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setMessage("去授予权限？").create().show();
        }
    }

    @Override
    public void onPermissionsGranted(int paramInt, @NonNull List<String> paramList) {
        Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
        EasyPermissions.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt, this);
    }

    class ButtonOnClickListener implements OnClickListener {
        private int buttonId;

        public ButtonOnClickListener(int paramInt) {
            this.buttonId = paramInt;
        }

        public void onClick(View paramView) {
            if (paramView.getId() == R.id.button) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.addCategory("android.intent.category.OPENABLE");
                intent.setType("image/*");
                MainActivity.this.startActivityForResult(intent, 112);
                return;
            }
            MainActivity.this.cameraTask();
        }
    }
}