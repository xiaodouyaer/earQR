package com.liuchaoya.jartest.earmarktest.camera.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.liuchaoya.jartest.R;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.DisplayUtil;
import com.liuchaoya.jartest.earmarktest.camera.utilclass.ResultPoint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import cn.ac.ict.earmarktest.camera.CameraManager;


public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 255;
    private int SCAN_VELOCITY;
    private int innercornercolor;
    private int innercornerlength;
    private int innercornerwidth;
    private boolean isCircle;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private final int maskColor;
    private final Paint paint = new Paint();
    private Collection<ResultPoint> possibleResultPoints;
    private Bitmap resultBitmap;
    private final int resultColor;
    private final int resultPointColor;
    private Bitmap scanLight;
    private int scanLineTop;

    public ViewfinderView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        Resources localResources = getResources();
        this.maskColor = localResources.getColor(R.color.viewfinder_mask);
        this.resultColor = localResources.getColor(R.color.result_view);
        this.resultPointColor = localResources.getColor(R.color.possible_result_points);
        this.possibleResultPoints = new HashSet(5);
        this.scanLight = BitmapFactory.decodeResource(localResources, R.drawable.scan_light);
        initInnerRect(paramContext, paramAttributeSet);
    }

    public static int dip2px(Context paramContext, float paramFloat) {
        return (int) (paramFloat * paramContext.getResources().getDisplayMetrics().density + 0.5F);
    }

    private void drawFrameBounds(Canvas paramCanvas, Rect paramRect) {
        this.paint.setColor(this.innercornercolor);
        this.paint.setStyle(Style.FILL);
        int i = this.innercornerwidth;
        int j = this.innercornerlength;
        paramCanvas.drawRect(paramRect.left, paramRect.top, paramRect.left + i, paramRect.top + j, this.paint);
        paramCanvas.drawRect(paramRect.left, paramRect.top, paramRect.left + j, paramRect.top + i, this.paint);
        paramCanvas.drawRect(paramRect.right - i, paramRect.top, paramRect.right, paramRect.top + j, this.paint);
        paramCanvas.drawRect(paramRect.right - j, paramRect.top, paramRect.right, paramRect.top + i, this.paint);
        paramCanvas.drawRect(paramRect.left, paramRect.bottom - j, paramRect.left + i, paramRect.bottom, this.paint);
        paramCanvas.drawRect(paramRect.left, paramRect.bottom - i, paramRect.left + j, paramRect.bottom, this.paint);
        paramCanvas.drawRect(paramRect.right - i, paramRect.bottom - j, paramRect.right, paramRect.bottom, this.paint);
        paramCanvas.drawRect(paramRect.right - j, paramRect.bottom - i, paramRect.right, paramRect.bottom, this.paint);
    }

    private void drawScanLight(Canvas paramCanvas, Rect paramRect) {
        if (this.scanLineTop == 0) {
            this.scanLineTop = paramRect.top;
        }
        if (this.scanLineTop >= paramRect.bottom - 30) {
            this.scanLineTop = paramRect.top;
        } else {
            this.scanLineTop += this.SCAN_VELOCITY;
        }
        paramRect = new Rect(paramRect.left, this.scanLineTop, paramRect.right, this.scanLineTop + 30);
        paramCanvas.drawBitmap(this.scanLight, null, paramRect, this.paint);
    }

    private void initInnerRect(Context paramContext, AttributeSet paramAttributeSet) {
        TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.innerrect);
        float f = typedArray.getDimension(R.styleable.innerrect_inner_margintop, -1.0F);
        if (f != -1.0F) {
            CameraManager.FRAME_MARGINTOP = (int) f;
        }
        CameraManager.FRAME_WIDTH = (int) typedArray.getDimension(R.styleable.innerrect_inner_width, DisplayUtil.screenWidthPx / 2);
        CameraManager.FRAME_HEIGHT = (int) typedArray.getDimension(R.styleable.innerrect_inner_height, DisplayUtil.screenWidthPx / 2);
        this.innercornercolor = typedArray.getColor(R.styleable.innerrect_inner_corner_color, Color.parseColor("#45DDDD"));
        this.innercornerlength = ((int) typedArray.getDimension(R.styleable.innerrect_inner_corner_length, 65.0F));
        this.innercornerwidth = ((int) typedArray.getDimension(R.styleable.innerrect_inner_corner_width, 15.0F));
        typedArray.getDrawable(R.styleable.innerrect_inner_scan_bitmap);
        this.scanLight = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.innerrect_inner_scan_bitmap, R.drawable.scan_light));
        this.SCAN_VELOCITY = typedArray.getInt(R.styleable.innerrect_inner_scan_speed, 5);
        this.isCircle = typedArray.getBoolean(R.styleable.innerrect_inner_scan_iscircle, true);
        typedArray.recycle();
    }

    public void addPossibleResultPoint(ResultPoint paramResultPoint) {
        this.possibleResultPoints.add(paramResultPoint);
    }

    public void drawViewfinder() {
        this.resultBitmap = null;
        invalidate();
    }

    public void onDraw(Canvas paramCanvas) {
        Rect localRect = CameraManager.get().getFramingRect();
        if (localRect == null) {
            return;
        }
        int j = paramCanvas.getWidth();
        int k = paramCanvas.getHeight();
        Object localObject1 = this.paint;
        int i;
        if (this.resultBitmap != null) {
            i = this.resultColor;
        } else {
            i = this.maskColor;
        }
        ((Paint) localObject1).setColor(i);
        float f = j;
        paramCanvas.drawRect(0.0F, 0.0F, f, localRect.top, this.paint);
        paramCanvas.drawRect(0.0F, localRect.top, localRect.left, localRect.bottom + 1, this.paint);
        paramCanvas.drawRect(localRect.right + 1, localRect.top, f, localRect.bottom + 1, this.paint);
        paramCanvas.drawRect(0.0F, localRect.bottom + 1, f, k, this.paint);
        if (this.resultBitmap != null) {
            this.paint.setAlpha(255);
            paramCanvas.drawBitmap(this.resultBitmap, localRect.left, localRect.top, this.paint);
            return;
        }
        drawFrameBounds(paramCanvas, localRect);
        drawScanLight(paramCanvas, localRect);
        Object localObject2 = this.possibleResultPoints;
        localObject1 = this.lastPossibleResultPoints;
        if (((Collection) localObject2).isEmpty()) {
            this.lastPossibleResultPoints = null;
        } else {
            this.possibleResultPoints = new HashSet(5);
            this.lastPossibleResultPoints = ((Collection) localObject2);
            this.paint.setAlpha(255);
            this.paint.setColor(this.resultPointColor);
            if (this.isCircle) {
                localObject2 = ((Collection) localObject2).iterator();
                while (((Iterator) localObject2).hasNext()) {
                    ResultPoint localResultPoint = (ResultPoint) ((Iterator) localObject2).next();
                    paramCanvas.drawCircle(localRect.left + localResultPoint.getX(), localRect.top + localResultPoint.getY(), 6.0F, this.paint);
                }
            }
        }
        if (localObject1 != null) {
            this.paint.setAlpha(127);
            this.paint.setColor(this.resultPointColor);
            if (this.isCircle) {
                localObject1 = ((Collection) localObject1).iterator();
                while (((Iterator) localObject1).hasNext()) {
                    localObject2 = (ResultPoint) ((Iterator) localObject1).next();
                    paramCanvas.drawCircle(localRect.left + ((ResultPoint) localObject2).getX(), localRect.top + ((ResultPoint) localObject2).getY(), 3.0F, this.paint);
                }
            }
        }
        postInvalidateDelayed(100L, localRect.left, localRect.top, localRect.right, localRect.bottom);
    }
}