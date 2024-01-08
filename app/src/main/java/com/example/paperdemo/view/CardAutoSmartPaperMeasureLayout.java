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
import com.example.paperdemo.util.AiCode;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.util.PxDxUtil;
import com.ikangtai.papersdk.util.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Card Automatic identification of test paper View
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
        //float paperTop = width / 2 + Utils.dp2px(context, 10);
        float paperCenterX = width / 2;
        float paperCenterY = height / 2;
        //canvas.drawRect(0, paperTop + paperHeight, width, width, bgPaint);
        //Reference strip for drawing test paper
        destBitmapWidth = width - padding - Utils.dp2px(context, 14);
        destBitmapHeight = destBitmapWidth * 177 / 1053;
        float smallPaperFrameWidth = destBitmapWidth * 151 / 780f;
        float smallPaperFrameHeight = destBitmapHeight * 62 / 134f;
        float bigPaperFrameWidth = destBitmapWidth * 104 / 350.7f;
        float bigPaperFrameHeight = destBitmapHeight * 43 / 60.2f;

        float left = padding;
        float top = paperCenterY - (height / 2 - destBitmapHeight) / 3 - destBitmapHeight;
        float right = destBitmapWidth + left;
        float bottom = top + destBitmapHeight;
        RectF rectF = new RectF(left, top, right, bottom);
        canvas.drawBitmap(sampleBitmap, null, rectF, null);
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //Draw black mask
        canvas.drawRect(0, 0, width, height, bgPaint);
        int lineWidth = Utils.dp2px(context, 10);

        float smallPaperCenterX = width / 2;
        float smallPaperCenterY = top + destBitmapHeight * 32 / 134f + destBitmapHeight * 30 / 134f;

        float smallPaperLeft = smallPaperCenterX - smallPaperFrameWidth / 2;
        float smallPaperTop = smallPaperCenterY - smallPaperFrameHeight / 2;
        float smallPaperRight = smallPaperCenterX + smallPaperFrameWidth / 2;
        float smallPaperBottom = smallPaperCenterY + smallPaperFrameHeight / 2;
        canvas.drawRect(smallPaperLeft, smallPaperTop, smallPaperRight, smallPaperBottom, eraser);
        float fixY = (paperHeight - destBitmapHeight) / 2;
        //Hollow cover for viewing frame
        float paperLeft = paperCenterX - bigPaperFrameWidth / 2;
        float paperTop = paperCenterY;
        float paperRight = paperCenterX + bigPaperFrameWidth / 2;
        float paperBottom = paperCenterY + bigPaperFrameHeight;
        canvas.drawRect(paperLeft, paperTop, paperRight, paperBottom, eraser);
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //draw T C
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Utils.sp2px(context, 16f));
        final String T = "T";
        final String C = "C";
        float tcMargin = Utils.dp2px(context, 10);
        float tcY = top + destBitmapHeight * 22 / 60f + tcMargin;
        float tWidth = textPaint.measureText(T);
        float cWidth = textPaint.measureText(C);
        float tX = smallPaperLeft - tWidth / 2 - tcMargin;
        float cX = smallPaperRight - cWidth / 2 + tcMargin;
        canvas.drawText(T, tX, tcY, textPaint);
        canvas.drawText(C, cX, tcY, textPaint);
        textPaint.setFakeBoldText(false);
        //First corner
        float centerX = paperLeft;
        float centerY = paperTop;
        float startX = centerX + lineWidth;
        float startY = centerY;
        float stopX = centerX;
        float stopY = centerY + lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Second corner
        centerX = paperLeft;
        centerY = paperBottom;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Third corner
        centerX = paperRight;
        centerY = paperBottom;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Fourth corner
        centerX = paperRight;
        centerY = paperTop;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        lineWidth = Utils.dp2px(context, 15);


        //No.5 corner mark
        centerX = paperLeft;
        centerY = paperTop;
        startX = centerX + lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //No.6 corner mark
        centerX = paperLeft;
        centerY = paperBottom;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //No.7 corner mark
        centerX = paperRight;
        centerY = paperBottom;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //No.8 corner mark
        centerX = paperRight;
        centerY = paperTop;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        //canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        data.innerLeft = (int) paperLeft;
        data.innerTop = (int) paperTop;
        data.innerRight = (int) paperRight;
        data.innerBottom = (int) paperBottom;
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
        int offsetX = 0;
        int offsetY = 0;
        Path path = handlePath(paperCoordinatesData, offsetX, offsetY);
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

    private void drawScanResult(Canvas canvas) {
        String scanResult = handleData(paperCoordinatesData);
        if (!TextUtils.isEmpty(scanResult)) {
            textPaint.setTextSize(Utils.sp2px(context, 14f));
            float scanResultWidth = textPaint.measureText(scanResult);
            float scanResultBackgroundWidth = scanResultWidth * 1.5f;
            float scanResultBackgroundHeight = Utils.dp2px(context, 45);
            float layerLeft = (width - scanResultBackgroundWidth) / 2;
            float layerTop = width / 2 - scanResultBackgroundHeight;
            float layerRight = layerLeft + scanResultBackgroundWidth;
            float layerBottom = layerTop + scanResultBackgroundHeight;
            //Set a new rectangle
            RectF layerOval = new RectF(layerLeft, layerTop, layerRight, layerBottom);
            //The second parameter is the x radius, and the third parameter is the y radius
            canvas.drawRoundRect(layerOval, 10, 10, textBackgroundPaint);
            float x = (width - scanResultWidth) / 2;
            float y = width / 2 + 10 - scanResultBackgroundHeight / 2;
            canvas.drawText(scanResult, x, y, textPaint);
        }

    }

    private void drawHint(Canvas canvas) {
        String hint = getContext().getString(R.string.card_paper_in_container);
        if (!TextUtils.isEmpty(hint)) {
            textPaint.setTextSize(Utils.sp2px(context, 12f));
            float hintWidth = textPaint.measureText(hint);
            float x = (width - hintWidth) / 2;
            float y = ((height - destBitmapHeight) / 3 - Utils.dp2px(context, 12f)) / 2;
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

    private Path handlePath(PaperCoordinatesData data, int offsetX, int offsetY) {
        if (data != null) {
            if (data.getCode() == AiCode.CODE_0) {
                Path p = new Path();
                p.moveTo(data.getPoint1().x + offsetX, data.getPoint1().y + offsetY);
                p.lineTo(data.getPoint2().x + offsetX, data.getPoint2().y + offsetY);
                p.lineTo(data.getPoint3().x + offsetX, data.getPoint3().y + offsetY);
                p.lineTo(data.getPoint4().x + offsetX, data.getPoint4().y + offsetY);
                p.close();
                return p;
            }
        }

        return null;
    }

    public Data getData() {
        return data;
    }

    public static final class Data {
        /**
         * Outermost width
         */
        public int outerWidth;
        /**
         * Outermost height
         */
        public int outerHeight;
        /**
         * Crop frame width
         */
        public int innerWidth;
        /**
         * Crop frame height
         */
        public int innerHeight;
        /**
         * The left coordinate of the upper left corner of the cropping box
         */
        public int innerLeft;
        /**
         * The top coordinate of the upper left corner of the cropping box
         */
        public int innerTop;
        /**
         * The right coordinate of the upper left corner of the cropping box
         */
        public int innerRight;
        /**
         * The bottom coordinate of the upper left corner of the cropping box
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

