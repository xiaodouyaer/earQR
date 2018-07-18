//package com.liuchaoya.jartest;
//
//import android.Manifest;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Toast;
//
//import com.tbruyelle.rxpermissions2.Permission;
//import com.tbruyelle.rxpermissions2.RxPermissions;
//
//import io.reactivex.disposables.Disposable;
//import io.reactivex.functions.Consumer;
//import qrcode.QrCodeActivity;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    public void invoking(View view) {
//        RxPermissions rxPermissions = new RxPermissions(this);
//        Disposable subscribe = rxPermissions.requestEach(Manifest.permission.CAMERA)
//                .subscribe(new Consumer<Permission>() {
//                    @Override
//                    public void accept(Permission permission) throws Exception {
//                        if (permission.granted) {
//                            // 用户已经同意该权限
//                            startActivity(new Intent(MainActivity.this, QrCodeActivity.class));
//                        } else if (permission.shouldShowRequestPermissionRationale) {
//                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                            Toast.makeText(MainActivity.this, "您拒绝了存储权限,无法更新app", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 用户拒绝了该权限，并且选中『不再询问』
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            }).setMessage("去授予权限？").create().show();
//                        }
//                    }
//                });
//    }
//}
