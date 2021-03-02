package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private int paperType;

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
        invalidate();
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
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
        textPaint.setTextSize(Utils.sp2px(context, 13f));
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#99000000"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        data.outerWidth = this.width;
        data.outerHeight = this.width;
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
        drawBitmap(canvas);
        drawLine(canvas);
        drawPaper(canvas);
    }

    float scanPaperNoticeWidth;
    float scanPaperNoticeHeight;
    float scanPaperNoticeX;
    float scanPaperNoticeY;

    private void drawPaper(Canvas canvas) {
        Bitmap source = null;
        //画试纸参考条
        source = BitmapFactory.decodeResource(getResources(), R.drawable.confirm_sample_pic_lh);
        int destBitmapHeight = source.getHeight();
        int padding = Utils.dp2px(context, 10);
        float left = padding;
        float top = width / 3;
        float right = width - padding * 2 + left;
        float bottom = destBitmapHeight + top;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawBitmap(source, null, rectF, null);

        //画扫描提醒
        textPaint.setTextSize(Utils.sp2px(context, 13f));
        String scanNotice = getResources().getString(R.string.paper_scan_notice);
        float scanNoticeWidth = textPaint.measureText(scanNotice);
        float scanNoticeX = (width - scanNoticeWidth) / 2;
        float scanNoticeY = top - Utils.dp2px(context, 50);
        canvas.drawText(scanNotice, scanNoticeX, scanNoticeY, textPaint);

        //卡/笔形试纸？
        String scanPaperNotice = getContext().getString(R.string.scan_paper_notice);
        textPaint.setTextSize(Utils.sp2px(context, 11f));
        scanPaperNoticeHeight = textPaint.getTextSize();
        scanPaperNoticeWidth = textPaint.measureText(scanPaperNotice);
        scanPaperNoticeX = width - padding - scanPaperNoticeWidth;
        scanPaperNoticeY = top - Utils.dp2px(context, 13);
        canvas.drawText(scanPaperNotice, scanPaperNoticeX, scanPaperNoticeY, textPaint);
        int lineY = (int) (scanPaperNoticeY + Utils.dp2px(context, 3f));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(Utils.dp2px(context, 1));
        canvas.drawLine(scanPaperNoticeX, lineY, width - padding, lineY, textPaint);
        textPaint.setStyle(Paint.Style.FILL);
        //画T C
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Utils.sp2px(context, 16f));
        final String T = "T";
        final String C = "C";
        float tcY = top - Utils.dp2px(context, 8);
        float tWidth = textPaint.measureText(T);
        float cWidth = textPaint.measureText(C);
        float tX = padding + (width - padding) * 578 / 1388 - tWidth / 2;
        float cX = padding + (width - padding) * 661 / 1388 - cWidth / 2;
        canvas.drawText(T, tX, tcY, textPaint);
        canvas.drawText(C, cX, tcY, textPaint);
    }

    private void drawLine(Canvas canvas) {
        //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));

        int padding = Utils.dp2px(context, 10);
        int paperHeight = Utils.dp2px(context, 26);
        float startX = padding;
        float stopX = width - startX;
        float top = width / 3 + Utils.dp2px(context, 40);
        float startY = top;
        float stopY = top;
        //横向第一条线
        canvas.drawRect(0, 0, width, stopY, linePaint);
        data.innerLeft = (int) startX;
        data.innerTop = (int) startY;
        //横向第二条线
        startY += paperHeight;
        stopY = startY;
        canvas.drawRect(0, startY, width, width, linePaint);

        data.innerRight = (int) stopX;
        data.innerBottom = (int) stopY;
        data.innerWidth = data.innerRight - data.innerLeft;
        data.innerHeight = data.innerBottom - data.innerTop;

        //左边缘线
        float leftMarginStartX = padding;
        float leftMarginStartY = startY;
        float leftMarginStopX = 0;
        float leftMarginStopY = leftMarginStartY - paperHeight;
        canvas.drawRect(leftMarginStartX, leftMarginStartY, leftMarginStopX, leftMarginStopY, linePaint);

        //右边缘线
        float rightMarginStartX = width;
        float rightMarginStartY = startY;
        float rightMarginStopX = width - padding;
        float rightMarginStopY = rightMarginStartY - paperHeight;
        canvas.drawRect(rightMarginStartX, rightMarginStartY, rightMarginStopX, rightMarginStopY, linePaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (x >= scanPaperNoticeX && y > scanPaperNoticeY-scanPaperNoticeHeight && x <= scanPaperNoticeX + scanPaperNoticeWidth && y <= scanPaperNoticeY + scanPaperNoticeHeight+20) {
                    if (this.viewClick != null) {
                        this.viewClick.onClick();
                        return true;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private ViewClick viewClick;

    public void setViewClick(ViewClick viewClick) {
        this.viewClick = viewClick;
    }

    //自定义点击事件接口
    public interface ViewClick {
        void onClick();
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
