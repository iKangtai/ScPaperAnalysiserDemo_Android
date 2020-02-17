package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.util.Utils;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 16:26
 */
public class ManualSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Context context;
    private int width;
    private Data data = new Data();
    private Bitmap originSquareBitmap;

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
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

    public ManualSmartPaperMeasureLayout(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public ManualSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public ManualSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        data.outerWidth = this.width;
        data.outerHeight = this.width;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
        drawPaper(canvas);
        drawLine(canvas);
    }

    private void drawPaper(Canvas canvas) {
        //画试纸参考条
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.paper_camera_ovulation_refrence);
        int destBitmapHeight = source.getHeight();
        int padding = Utils.dp2px(context, 10);
        float left = padding;
        float top = width / 3;
        float right = width - padding * 2 + left;
        float bottom = destBitmapHeight + top;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawBitmap(source, null, rectF, null);

        //画扫描提醒
        String scanNotice = getResources().getString(R.string.paper_scan_notice);
        float scanNoticeWidth = textPaint.measureText(scanNotice);
        float scanNoticeX = (width - scanNoticeWidth) / 2;
        float scanNoticeY = top - Utils.dp2px(context, 30);
        canvas.drawText(scanNotice, scanNoticeX, scanNoticeY, textPaint);

        //画T C
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Utils.sp2px(context, 16f));
        final String T = "T";
        final String C = "C";
        float tcY = top - Utils.dp2px(context, 5);
        float tWidth = textPaint.measureText(T);
        float cWidth = textPaint.measureText(C);
        float tX = width / 2 - tWidth;
        float cX = width / 2 + cWidth;
        canvas.drawText(T, tX, tcY, textPaint);
        canvas.drawText(C, cX, tcY, textPaint);
    }

    private void drawLine(Canvas canvas) {

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));

        int padding = Utils.dp2px(context, 10);
        float startX = padding;
        float stopX = width - startX;
        float top = width / 3 + Utils.dp2px(context, 60);
        float startY = top;
        float stopY = top;
        //横向第一条线
        canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        data.innerLeft = (int) startX;
        data.innerTop = (int) startY;
        //横向第二条线
        startY += Utils.dp2px(context, 20);
        stopY = startY;
        canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        data.innerRight = (int) stopX;
        data.innerBottom = (int) stopY;
        data.innerWidth = data.innerRight - data.innerLeft;
        data.innerHeight = data.innerBottom - data.innerTop;


        //左边缘
        String leftMarginContent = getResources().getString(R.string.left_margin);
        //右边缘
        String rightMarginContent = getResources().getString(R.string.right_margin);

        //边缘线长度
        int marginLength = Utils.dp2px(context, 130);

        //左边缘线
        float leftMarginStartX = startX;
        float leftMarginStartY = startY + Utils.dp2px(context, 20);
        float leftMarginStopX = startX;
        float leftMarginStopY = leftMarginStartY - marginLength;
        canvas.drawLine(leftMarginStartX, leftMarginStartY, leftMarginStopX, leftMarginStopY, linePaint);

        //左边缘描述
        int diff = Utils.dp2px(context, 20);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(Utils.sp2px(context, 14f));
        canvas.drawText(leftMarginContent, leftMarginStartX, leftMarginStartY + diff, textPaint);


        //右边缘线
        float rightMarginStartX = stopX;
        float rightMarginStartY = startY + Utils.dp2px(context, 20);
        float rightMarginStopX = stopX;
        float rightMarginStopY = rightMarginStartY - marginLength;
        canvas.drawLine(rightMarginStartX, rightMarginStartY, rightMarginStopX, rightMarginStopY, linePaint);

        //右边缘描述
        float rightMarginContentWidth = textPaint.measureText(rightMarginContent);
        canvas.drawText(rightMarginContent, rightMarginStartX - rightMarginContentWidth, rightMarginStartY + diff, textPaint);
    }

    public Data getData() {
        return data;
    }

    public static final class Data {
        /**
         * 最外层宽度
         */
        public int outerWidth;
        /**
         * 最外层高度
         */
        public int outerHeight;
        /**
         * 裁剪框宽度
         */
        public int innerWidth;
        /**
         * 裁剪框高度
         */
        public int innerHeight;
        /**
         * 裁剪框左上角left坐标
         */
        public int innerLeft;
        /**
         * 裁剪框左上角top坐标
         */
        public int innerTop;
        /**
         * 裁剪框右下角right坐标
         */
        public int innerRight;
        /**
         * 裁剪框右下角bottom坐标
         */
        public int innerBottom;


        public String getPointPath() {
            String flag = "_";
            StringBuilder pointPathBuilder = new StringBuilder();
            pointPathBuilder.append(innerLeft);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerTop);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerRight);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerTop);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerRight);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerBottom);

            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerLeft);
            pointPathBuilder.append(flag);
            pointPathBuilder.append(innerBottom);

            return pointPathBuilder.toString();
        }

        @Override
        public String toString() {
            return "Data{" +
                    "outerWidth=" + outerWidth +
                    ", outerHeight=" + outerHeight +
                    ", innerWidth=" + innerWidth +
                    ", innerHeight=" + innerHeight +
                    ", innerLeft=" + innerLeft +
                    ", innerTop=" + innerTop +
                    ", innerRight=" + innerRight +
                    ", innerBottom=" + innerBottom +
                    '}';
        }
    }
}
