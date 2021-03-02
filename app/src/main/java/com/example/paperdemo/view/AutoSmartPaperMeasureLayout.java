package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.example.paperdemo.R;
import com.example.paperdemo.util.AiCode;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.util.PxDxUtil;
import com.ikangtai.papersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Automatic identification of test paper View
 *
 * @author xiongyl 2019/12/11 16:26
 */
public class AutoSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint textBackgroundPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Context context;
    private int width;
    private int height;
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
        this.height=this.width;
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Utils.sp2px(context, 18f));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#79FA1E"));
        textBackgroundPaint.setAntiAlias(true);
        textBackgroundPaint.setColor(Color.parseColor("#99444444"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resolvedHeight = View.resolveSize(width, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, resolvedHeight);
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

    }

    public void scanPaperCoordinatesData(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap) {
        this.paperCoordinatesData = paperCoordinatesData;
        this.originSquareBitmap = originSquareBitmap;
        invalidate();
    }

    public PaperCoordinatesData getPaperCoordinatesData() {
        return paperCoordinatesData;
    }

    public void setPaperCoordinatesData(PaperCoordinatesData paperCoordinatesData) {
        this.paperCoordinatesData = paperCoordinatesData;
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
            textPaint.setTextSize(Utils.sp2px(context, 14f));
            float scanResultWidth = textPaint.measureText(scanResult);
            float scanResultBackgroundWidth = scanResultWidth * 1.5f;
            float scanResultBackgroundHeight = Utils.dp2px(context, 45);
            float layerLeft = (width - scanResultBackgroundWidth) / 2;
            float layerTop = width / 2 - scanResultBackgroundHeight / 2;
            float layerRight = layerLeft + scanResultBackgroundWidth;
            float layerBottom = layerTop + scanResultBackgroundHeight;
            // Set a new rectangle
            RectF layerOval = new RectF(layerLeft, layerTop, layerRight, layerBottom);
            //The second parameter is the x radius, and the third parameter is the y radius
            canvas.drawRoundRect(layerOval, 10, 10, textBackgroundPaint);
            float x = (width - scanResultWidth) / 2;
            float y = width / 2 + 10;
            canvas.drawText(scanResult, x, y, textPaint);
        }

    }


    private void drawLine(Canvas canvas) {
        linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 5));
        int lineWidth = Utils.dp2px(context, 30);
        int padding = Utils.dp2px(context, 10);
        int paddingTop = Utils.dp2px(context, 70);
        float startX = padding + lineWidth;
        float startY = padding + paddingTop;
        float centerX = padding;
        float centerY = padding + paddingTop;
        float stopX = padding;
        float stopY = padding + lineWidth + paddingTop;
        //First horizontal line
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Second horizontal line
        startX = padding + lineWidth;
        startY = height - padding - paddingTop;
        centerX = padding;
        centerY = height - padding - paddingTop;
        stopX = padding;
        stopY = height - padding - lineWidth - paddingTop;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Third horizontal line
        startX = width - padding - lineWidth;
        startY = height - padding - paddingTop;
        centerX = width - padding;
        centerY = height - padding - paddingTop;
        stopX = width - padding;
        stopY = height - padding - lineWidth - paddingTop;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Fourth horizontal line
        startX = width - padding - lineWidth;
        startY = padding + paddingTop;
        centerX = width - padding;
        centerY = padding + paddingTop;
        stopX = width - padding;
        stopY = padding + lineWidth + paddingTop;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        Path path = handlePath(paperCoordinatesData);
        if (path != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 4));
            canvas.drawPath(path, linePaint);
        }
    }

    private Path handleLinePath(float startX, float startY, float centerX, float centerY, float endX, float endY) {
        Path p = new Path();
        p.moveTo(startX, startY);
        p.lineTo(centerX, centerY);
        p.lineTo(endX, endY);
        return p;
    }

    private void drawHint(Canvas canvas) {
        String hint = getResources().getString(R.string.paper_in_container);
        if (!TextUtils.isEmpty(hint)) {
            textPaint.setTextSize(Utils.sp2px(context, 14f));
            float hintWidth = textPaint.measureText(hint);
            float x = (width - hintWidth) / 2;
            float y = Utils.dp2px(context, 50f);
            StaticLayout layout = new StaticLayout(hint, textPaint, (int) hintWidth - 10, Layout.Alignment.ALIGN_CENTER, 1.2f, 0, false);
            //canvas.drawText(hint, x, y, textPaint);
            canvas.translate(x, y);
            layout.draw(canvas);
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
