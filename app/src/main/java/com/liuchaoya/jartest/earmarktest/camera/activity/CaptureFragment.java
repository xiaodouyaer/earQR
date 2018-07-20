package com.liuchaoya.jartest.earmarktest.camera.activity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.decoding.CaptureActivityHandler;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.BarcodeFormat;
import com.liuchaoya.jartest.earmarktest.camera.view.ViewfinderView;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import cn.ac.ict.earmarktest.camera.CameraManager;
import cn.ac.ict.earmarktest.camera.decoding.InactivityTimer;

import static android.content.Context.VIBRATOR_SERVICE;

public class CaptureFragment extends Fragment implements Callback {
    private static final float BEEP_VOLUME = 0.1F;
    private static final long VIBRATE_DURATION = 200L;
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer paramAnonymousMediaPlayer) {
            paramAnonymousMediaPlayer.seekTo(0);
        }
    };
    private Camera camera;
    private String characterSet;
    private Vector<BarcodeFormat> decodeFormats;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private boolean vibrate;
    private ViewfinderView viewfinderView;

    private void initBeepSound() {
        try {
            AssetFileDescriptor localAssetFileDescriptor;
            if ((this.playBeep) && (this.mediaPlayer == null)) {
                getActivity().setVolumeControlStream(3);
                this.mediaPlayer = new MediaPlayer();
                this.mediaPlayer.setAudioStreamType(3);
                this.mediaPlayer.setOnCompletionListener(this.beepListener);
                localAssetFileDescriptor = getResources().openRawResourceFd(R.raw.beep);
                this.mediaPlayer.setDataSource(localAssetFileDescriptor.getFileDescriptor(), localAssetFileDescriptor.getStartOffset(), localAssetFileDescriptor.getLength());
                localAssetFileDescriptor.close();
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            }
            return;
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        }
        this.mediaPlayer = null;
    }

    private void initCamera(SurfaceHolder paramSurfaceHolder) {
        try {
            CameraManager.get().openDriver(paramSurfaceHolder);
            this.camera = CameraManager.get().getCamera();
            if (this.handler == null) {
                this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.characterSet, this.viewfinderView);
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playBeepSoundAndVibrate() {
        if ((this.playBeep) && (this.mediaPlayer != null)) {
            this.mediaPlayer.start();
        }
        if (this.vibrate) {
            FragmentActivity localFragmentActivity = getActivity();
            getActivity();
            ((Vibrator) Objects.requireNonNull(Objects.requireNonNull(localFragmentActivity).getSystemService(VIBRATOR_SERVICE))).vibrate(200L);
        }
    }

    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void handleDecode(String paramString, Bitmap paramBitmap) {
        this.inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        if (paramString != null) {
            if (TextUtils.isEmpty(paramString)) {
                return;
            }
            ((CaptureActivity) getActivity()).decode_success(paramString);
        }
    }

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        CameraManager.init(getActivity().getApplication());
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(getActivity());
    }

    @Nullable
    public View onCreateView(@NonNull final LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        View inflate = paramLayoutInflater.inflate(R.layout.mark_fragment_capture, null);
        this.viewfinderView = (inflate.findViewById(R.id.viewfinder_view_mark));
        this.surfaceView = ( inflate.findViewById(R.id.preview_view));
        this.surfaceHolder = this.surfaceView.getHolder();
        (inflate.findViewById(R.id.ll_sdsr)).setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                ((CaptureActivity) CaptureFragment.this.getActivity()).decode_success(null);
            }
        });
        final ImageView imageView = inflate.findViewById(R.id.sdt_iv);
        final TextView textView = inflate.findViewById(R.id.sdt_tv);
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View paramAnonymousView) {
                CameraManager.get().flashHandler(imageView, textView);
            }
        });
        return inflate;
    }

    public void onDestroy() {
        super.onDestroy();
        this.inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void onPause() {
        super.onPause();
        if (this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void onResume() {
        super.onResume();
        if (this.hasSurface) {
            initCamera(this.surfaceHolder);
        } else {
            this.surfaceHolder.addCallback(this);
            this.surfaceHolder.setType(3);
        }
        this.decodeFormats = null;
        this.characterSet = null;
        this.playBeep = true;
        FragmentActivity localFragmentActivity = getActivity();
        getActivity();
        if (((AudioManager) Objects.requireNonNull(Objects.requireNonNull(localFragmentActivity).getSystemService(Context.AUDIO_SERVICE))).getRingerMode() != 2) {
            this.playBeep = false;
        }
        initBeepSound();
        this.vibrate = true;
    }

    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
    }

    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        if (!this.hasSurface) {
            this.hasSurface = true;
            initCamera(paramSurfaceHolder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        this.hasSurface = false;
        if ((this.camera != null) && (this.camera != null) && (CameraManager.get().isPreviewing())) {
            if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                this.camera.setPreviewCallback(null);
            }
            this.camera.stopPreview();
            CameraManager.get().getPreviewCallback().setHandler(null, 0);
            CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
            CameraManager.get().setPreviewing(false);
        }
    }
}


/* Location:              G:\Android_逆向\liuchaoya\动检通湖北\classes-dex2jar.jar!\cn\ac\ict\earmarktest\camera\activity\CaptureFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */