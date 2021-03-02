package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * 手动拍照测量坐标线
 *
 * @author
 */
public class CardAutoSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint textBackgroundPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Paint bgPaint = new Paint();
    private Paint eraser = new Paint();
    private Context context;
    private int width;
    private int height;
    private Data data = new Data();
    private Bitmap originSquareBitmap;
    private Bitmap sampleBitmap;
    private int destBitmapHeight;
    private int destBitmapWidth;
    private boolean isFixSquareImage = true;
    private PaperCoordinatesData paperCoordinatesData;

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
        this.paperCoordinatesData = null;
        invalidate();
    }

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap, PaperCoordinatesData paperCoordinatesData) {
        this.originSquareBitmap = originSquareBitmap;
        this.paperCoordinatesData = paperCoordinatesData;
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

    public CardAutoSmartPaperMeasureLayout(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public CardAutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public CardAutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initData(context);
    }

    private void initData(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.width = dm.widthPixels;
        this.height = this.width;
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(Utils.sp2px(context, 13f));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.parseColor("#90000000"));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#79FA1E"));
        linePaint.setStrokeWidth(Utils.dp2px(context, 4));
        eraser.setStyle(Paint.Style.FILL);
        eraser.setColor(Color.TRANSPARENT);
        eraser.setAlpha(1);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        textBackgroundPaint.setAntiAlias(true);
        textBackgroundPaint.setColor(Color.parseColor("#99444444"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        data.outerWidth = this.width;
        data.outerHeight = this.width;
        sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.confirm_sample_pic_card);
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
        drawScanResult(canvas);
        drawHint(canvas);
    }

    private void drawLine(Canvas canvas) {
        int padding = Utils.dp2px(context, 10);
        int paperHeight = width * 2 / 7;
        float paperTop = width / 2 + Utils.dp2px(context, 10);

        //canvas.drawRect(0, paperTop + paperHeight, width, width, bgPaint);
        //画试纸参考条
        destBitmapWidth = width - padding * 2;
        destBitmapHeight = destBitmapWidth * 177 / 1053;
        float left = padding;
        float top = width / 2 - destBitmapHeight;
        float right = destBitmapWidth + left;
        float bottom = width / 2;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawBitmap(sampleBitmap, null, rectF, null);
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //绘制黑色蒙板
        canvas.drawRect(0, 0, width, height, bgPaint);
        int lineWidth = Utils.dp2px(context, 10);
        canvas.drawRect(padding + destBitmapWidth * 142 / 351f, top + destBitmapHeight * 12 / 60f, padding + destBitmapWidth * 216 / 351f, top + destBitmapHeight * 43 / 60f, eraser);
        float fixY = (paperHeight - destBitmapHeight) / 2;
        //取景框镂空遮盖
        canvas.drawRect(padding + destBitmapWidth * 124 / 351f, paperTop + fixY + destBitmapHeight * 10 / 60f, padding + destBitmapWidth * 232 / 351f, paperTop + fixY + destBitmapHeight * 55 / 60f, eraser);
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //画T C
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Utils.sp2px(context, 16f));
        final String T = "T";
        final String C = "C";
        float tcMargin = Utils.dp2px(context, 10);
        float tcY = top + destBitmapHeight * 22 / 60f + tcMargin;
        float tWidth = textPaint.measureText(T);
        float cWidth = textPaint.measureText(C);
        float tX = padding + destBitmapWidth * 142 / 351f - tWidth / 2 - tcMargin;
        float cX = padding + destBitmapWidth * 216 / 351f - cWidth / 2 + tcMargin;
        canvas.drawText(T, tX, tcY, textPaint);
        canvas.drawText(C, cX, tcY, textPaint);
        textPaint.setFakeBoldText(false);
        //第一角标
        float centerX = padding + destBitmapWidth * 142 / 351f;
        float centerY = top + destBitmapHeight * 12 / 60f;
        float startX = centerX + lineWidth;
        float startY = centerY;
        float stopX = centerX;
        float stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第二角标
        centerX = padding + destBitmapWidth * 142 / 351f;
        centerY = top + destBitmapHeight * 43 / 60f;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第三角标
        centerX = padding + destBitmapWidth * 216 / 351f;
        centerY = top + destBitmapHeight * 43 / 60f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第四角标
        centerX = padding + destBitmapWidth * 216 / 351f;
        centerY = top + destBitmapHeight * 12 / 60f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        lineWidth = Utils.dp2px(context, 15);


        //第5角标
        centerX = padding + destBitmapWidth * 124 / 351f;
        centerY = paperTop + fixY + destBitmapHeight * 10 / 60f;
        startX = centerX + lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第6角标
        centerX = padding + destBitmapWidth * 124 / 351f;
        centerY = paperTop + fixY + destBitmapHeight * 55 / 60f;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第7角标
        centerX = padding + destBitmapWidth * 232 / 351f;
        centerY = paperTop + fixY + destBitmapHeight * 55 / 60f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //第8角标
        centerX = padding + destBitmapWidth * 232 / 351f;
        centerY = paperTop + fixY + destBitmapHeight * 10 / 60f;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        data.innerLeft = (int) (destBitmapWidth * 124 / 351f) + padding;
        data.innerTop = (int) (paperTop + fixY + destBitmapHeight * 10 / 60f);
        data.innerRight = (int) (destBitmapWidth * 232 / 351f) + padding;
        data.innerBottom = (int) (paperTop + fixY + destBitmapHeight * 55 / 60f);
        data.innerWidth = data.innerRight - data.innerLeft;
        data.innerHeight = data.innerBottom - data.innerTop;
        if (isFixSquareImage) {
            int fixPix = (data.innerWidth - data.innerHeight) / 2;
            data.innerTop -= fixPix;
            data.innerBottom += fixPix;
            data.innerHeight = data.innerWidth;
        }
        data.outerWidth = destBitmapWidth;
        data.outerHeight = destBitmapHeight;
    }

    private Path handleLinePath(float startX, float startY, float centerX, float centerY, float endX, float endY) {
        Path p = new Path();
        p.moveTo(startX, startY);
        p.lineTo(centerX, centerY);
        p.lineTo(endX, endY);
        return p;
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
            // 设置个新的长方形
            RectF layerOval = new RectF(layerLeft, layerTop, layerRight, layerBottom);
            //第二个参数是x半径，第三个参数是y半径
            canvas.drawRoundRect(layerOval, 10, 10, textBackgroundPaint);
            float x = (width - scanResultWidth) / 2;
            float y = width / 2 + 10;
            canvas.drawText(scanResult, x, y, textPaint);
        }

    }

    private void drawHint(Canvas canvas) {
        String hint = getContext().getString(R.string.card_paper_in_container);
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

        public float tagLineLoc;
        /**
         * 裁剪框左上角left坐标
         */
        public int enlargeLeft;
        /**
         * 裁剪框左上角top坐标
         */
        public int enlargeTop;
        /**
         * 裁剪框右下角right坐标
         */
        public int enlargeRight;
        /**
         * 裁剪框右下角bottom坐标
         */
        public int enlargeBottom;
        /**
         * 裁剪框宽度
         */
        public int enlargeWidth;
        /**
         * 裁剪框高度
         */
        public int enlargeHeight;

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

