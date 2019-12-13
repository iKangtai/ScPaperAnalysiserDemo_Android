package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 16:26
 */
public class AutoSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint textBackgroundPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Context context;
    private int width;
    private PaperCoordinatesData paperCoordinatesData;
    private Bitmap originSquareBitmap;

    public AutoSmartPaperMeasureLayout(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public AutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public AutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initData(context);
    }

    private void initData(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.width = dm.widthPixels;
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Utils.sp2px(context, 14f));
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#F9F900"));
        textBackgroundPaint.setAntiAlias(true);
        textBackgroundPaint.setColor(Color.parseColor("#99444444"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long t1 = System.currentTimeMillis();
        drawBitmap(canvas);
        drawScanResult(canvas);
        drawLine(canvas);
        drawHint(canvas);
        long t2 = System.currentTimeMillis();
        Log.i(LogUtils.LOG_TAG, "画布绘制消耗:" + (t2 - t1));

    }

    public void scanPaperCoordinatesData(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap) {
        this.paperCoordinatesData = paperCoordinatesData;
        this.originSquareBitmap = originSquareBitmap;
        invalidate();
    }


    public void scanPaperCoordinatesData(PaperCoordinatesData paperCoordinatesData) {
        this.paperCoordinatesData = paperCoordinatesData;
        this.originSquareBitmap = null;
        invalidate();
    }


    private void drawBitmap(Canvas canvas) {

        if (originSquareBitmap != null) {
            int left = 0;
            int top = 0;
            int right = getWidth();
            int bottom = getHeight();
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(originSquareBitmap, null, rect, null);
        }

    }

    private void drawScanResult(Canvas canvas) {
        String scanResult = handleData(paperCoordinatesData);
        if (!TextUtils.isEmpty(scanResult)) {
            float scanResultWidth = textPaint.measureText(scanResult);
            float scanResultBackgroundWidth = scanResultWidth * 1.5f;
            float scanResultBackgroundHeight = Utils.dp2px(context, 45);
            float layerLeft = (width - scanResultBackgroundWidth) / 2;
            float layerTop = width / 2 - scanResultBackgroundHeight / 2;
            float layerRight = layerLeft + scanResultBackgroundWidth;
            float layerBottom = layerTop + scanResultBackgroundHeight;
            // 设置个新的长方形
            RectF layerOval = new RectF(layerLeft, layerTop, layerRight, layerBottom);
            //第二个参数是x半径，第三个参数是y半径
            canvas.drawRoundRect(layerOval, 10, 10, textBackgroundPaint);
            float x = (width - scanResultWidth) / 2;
            float y = width / 2 + 10;
            canvas.drawText(scanResult, x, y, textPaint);
        }

    }


    private void drawLine(Canvas canvas) {
        Path path = handlePath(paperCoordinatesData);
        if (path != null) {
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(5);
            linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            canvas.drawPath(path, linePaint);
        }
    }

    private void drawHint(Canvas canvas) {
        String hint = getResources().getString(R.string.paper_in_container);
        if (!TextUtils.isEmpty(hint)) {
            float hintWidth = textPaint.measureText(hint);
            float x = (width - hintWidth) / 2;
            float y = Utils.dp2px(context, 78f);
            canvas.drawText(hint, x, y, textPaint);
        }
    }

    private String handleData(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getCode() != AiCode.CODE_0) {
                return AiCode.getMessage(data.getCode());
            }
        }
        return null;
    }


    private Path handlePath(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getCode() == AiCode.CODE_0) {
                Path p = new Path();
                p.moveTo(data.getPoint1().x, data.getPoint1().y);
                p.lineTo(data.getPoint2().x, data.getPoint2().y);
                p.lineTo(data.getPoint3().x, data.getPoint3().y);
                p.lineTo(data.getPoint4().x, data.getPoint4().y);
                p.close();
                return p;
            }
        }

        return null;
    }

}
