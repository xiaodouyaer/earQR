package com.liuchaoya.jartest.earmarktest.camera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.DisplayUtil;


public class CaptureActivity
        extends AppCompatActivity {
    private void initDisplayOpinion() {
        DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
        DisplayUtil.density = localDisplayMetrics.density;
        DisplayUtil.densityDPI = localDisplayMetrics.densityDpi;
        DisplayUtil.screenWidthPx = localDisplayMetrics.widthPixels;
        DisplayUtil.screenhightPx = localDisplayMetrics.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), localDisplayMetrics.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), localDisplayMetrics.heightPixels);
    }

    protected void decode_success(String paramString) {
        Intent localIntent = new Intent();
        localIntent.putExtra("Content", paramString);
        setResult(-1, localIntent);
        finish();
//        Toast.makeText(this,paramString,Toast.LENGTH_LONG).show();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.camera);
        CaptureFragment fragment = new CaptureFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("decodeType",200);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, fragment).commit();
        initDisplayOpinion();
    }
}