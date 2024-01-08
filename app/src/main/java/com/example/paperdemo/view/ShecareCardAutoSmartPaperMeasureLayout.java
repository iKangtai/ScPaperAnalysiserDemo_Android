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
public class ShecareCardAutoSmartPaperMeasureLayout extends FrameLayout {
    private TextPaint textPaint = new TextPaint();
    private TextPaint textBackgroundPaint = new TextPaint();
    private TextPaint linePaint = new TextPaint();
    private Paint cardOverlayPaint = new Paint();
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
    private PaperCoordinatesData paperCoordinatesData;
    private int state;

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap) {
        this.originSquareBitmap = originSquareBitmap;
        this.paperCoordinatesData = null;
        if (originSquareBitmap != null) {
            if (state != 2) {
                //sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_pen_paper_green);
            }
            state = 2;
        } else {
            if (state != 0) {
                //sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_pen_paper_red);
            }
            this.state = 0;
        }
        invalidate();
    }

    public void scanPaperCoordinatesData(Bitmap originSquareBitmap, PaperCoordinatesData paperCoordinatesData) {
        this.originSquareBitmap = originSquareBitmap;
        this.paperCoordinatesData = paperCoordinatesData;
        if (paperCoordinatesData != null && paperCoordinatesData.getCode() == 0) {
            if (state != 1) {
                //sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_pen_paper_yellow);
            }
            this.state = 1;
        } else {
            if (state != 0) {
                //sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_pen_paper_red);
            }
            this.state = 0;
        }
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

    public ShecareCardAutoSmartPaperMeasureLayout(@NonNull Context context) {
        super(context);
        this.initData(context);
    }

    public ShecareCardAutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initData(context);
    }

    public ShecareCardAutoSmartPaperMeasureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        cardOverlayPaint.setStyle(Paint.Style.FILL);
        cardOverlayPaint.setAntiAlias(true);
        cardOverlayPaint.setColor(Color.parseColor("#2965ff01"));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundColor(Color.TRANSPARENT);
        data.outerWidth = this.width;
        data.outerHeight = this.width;
        sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_pen_paper_yc);
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
    private Path handleCardOverlayPath(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getColorBarPoints() != null && data.getColorBarPoints().length > 0) {
                int fixPadding = 0;
                Path p = new Path();
                p.moveTo(data.getColorBarPoints()[0] - fixPadding, data.getColorBarPoints()[1]);
                p.lineTo(data.getColorBarPoints()[10] - fixPadding, data.getColorBarPoints()[11]);
                p.lineTo(data.getColorBarPoints()[28] - fixPadding, data.getColorBarPoints()[29]);
                p.lineTo(data.getColorBarPoints()[22] - fixPadding, data.getColorBarPoints()[23]);
                p.close();
                return p;
            }
        }

        return null;
    }
    private Path handleCardOverlayPath1(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getColorBarPoints() != null && data.getColorBarPoints().length > 0) {
                int fixPadding = 0;
                Path p = new Path();
                p.moveTo(data.getColorBarPoints()[0] - fixPadding, data.getColorBarPoints()[1]);
                p.lineTo(data.getColorBarPoints()[2] - fixPadding, data.getColorBarPoints()[3]);
                p.lineTo(data.getColorBarPoints()[4] - fixPadding, data.getColorBarPoints()[5]);
                p.lineTo(data.getColorBarPoints()[6] - fixPadding, data.getColorBarPoints()[7]);
                p.close();
                return p;
            }
        }

        return null;
    }
    private Path handleCardOverlayPath2(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getColorBarPoints() != null && data.getColorBarPoints().length > 0) {
                int fixPadding = 0;
                Path p = new Path();
                p.moveTo(data.getColorBarPoints()[8] - fixPadding, data.getColorBarPoints()[9]);
                p.lineTo(data.getColorBarPoints()[10] - fixPadding, data.getColorBarPoints()[11]);
                p.lineTo(data.getColorBarPoints()[12] - fixPadding, data.getColorBarPoints()[13]);
                p.lineTo(data.getColorBarPoints()[14] - fixPadding, data.getColorBarPoints()[15]);
                p.close();
                return p;
            }
        }

        return null;
    }
    private Path handleCardOverlayPath3(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getColorBarPoints() != null && data.getColorBarPoints().length > 0) {
                int fixPadding = 0;
                Path p = new Path();
                p.moveTo(data.getColorBarPoints()[16] - fixPadding, data.getColorBarPoints()[17]);
                p.lineTo(data.getColorBarPoints()[18] - fixPadding, data.getColorBarPoints()[19]);
                p.lineTo(data.getColorBarPoints()[20] - fixPadding, data.getColorBarPoints()[21]);
                p.lineTo(data.getColorBarPoints()[22] - fixPadding, data.getColorBarPoints()[23]);
                p.close();
                return p;
            }
        }

        return null;
    }
    private Path handleCardOverlayPath4(PaperCoordinatesData data) {
        if (data != null) {
            if (data.getColorBarPoints() != null && data.getColorBarPoints().length > 0) {
                int fixPadding = 0;
                Path p = new Path();
                p.moveTo(data.getColorBarPoints()[24] - fixPadding, data.getColorBarPoints()[25]);
                p.lineTo(data.getColorBarPoints()[26] - fixPadding, data.getColorBarPoints()[27]);
                p.lineTo(data.getColorBarPoints()[28] - fixPadding, data.getColorBarPoints()[29]);
                p.lineTo(data.getColorBarPoints()[30] - fixPadding, data.getColorBarPoints()[31]);
                p.close();
                return p;
            }
        }

        return null;
    }
    private void drawLine(Canvas canvas) {
        int padding = Utils.dp2px(context, 10);
        linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 5));
        int lineWidth = Utils.dp2px(context, 30);
        int paddingTop = Utils.dp2px(context, 70);
        float paperCenterX = width / 2;
        float paperCenterY = height / 2;
        //canvas.drawRect(0, paperTop + paperHeight, width, width, bgPaint);
        //Reference strip for drawing test paper
        destBitmapWidth = width - padding * 2;
        destBitmapHeight = destBitmapWidth * 104 / 355;
        float smallPaperFrameWidth = destBitmapWidth * 44f / 355f;
        float smallPaperFrameHeight = destBitmapHeight * 44f / 104f;
        float bigPaperFrameWidth = destBitmapWidth * 57.5f / 355f;
        float bigPaperFrameHeight = destBitmapHeight * 44f / 104f;

        float left = padding;
        float top = paddingTop;
        float right = width - padding;
        float bottom = height - paddingTop;

        //canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        //Draw black mask
        //canvas.drawRect(0, 0, width, height, bgPaint);

        float smallPaperCenterX = width / 2 + destBitmapWidth * 117.7f / 355f;
        float smallPaperCenterY = top + destBitmapHeight / 2;

        float smallPaperLeft = smallPaperCenterX - smallPaperFrameWidth / 2;
        float smallPaperTop = smallPaperCenterY - smallPaperFrameHeight / 2;
        float smallPaperRight = smallPaperCenterX + smallPaperFrameWidth / 2;
        float smallPaperBottom = smallPaperCenterY + smallPaperFrameHeight / 2;
        //canvas.drawRect(smallPaperLeft, smallPaperTop, smallPaperRight, smallPaperBottom, eraser);
        //Hollow cover for viewing frame
        float bigPaperCenterX = width / 2;
        float bigPaperCenterY = top + destBitmapHeight / 2;

        float paperLeft = bigPaperCenterX - bigPaperFrameWidth / 2;
        float paperTop = bigPaperCenterY - bigPaperFrameHeight / 2;
        float paperRight = bigPaperCenterX + bigPaperFrameWidth / 2;
        float paperBottom = bigPaperCenterY + bigPaperFrameHeight / 2;
        //canvas.drawRect(paperLeft, paperTop, paperRight, paperBottom, eraser);
        //canvas.drawCircle(paperLeft, paperTop + bigPaperFrameHeight / 2, bigPaperFrameHeight / 2, eraser);
        //canvas.drawCircle(paperRight, paperTop + bigPaperFrameHeight / 2, bigPaperFrameHeight / 2, eraser);
        //canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        int sampleWidth = destBitmapWidth;
        int sampleHeight = sampleBitmap.getHeight() * sampleWidth / sampleBitmap.getWidth();
        RectF rectF = new RectF(left, top - sampleHeight, right, top);
        //canvas.drawBitmap(sampleBitmap, null, rectF, null);
        //First corner
        float centerX = left;
        float centerY = top;
        float startX = centerX + lineWidth;
        float startY = centerY;
        float stopX = centerX;
        float stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Second corner
        centerX = left;
        centerY = bottom;
        startX = centerX;
        startY = centerY - lineWidth;
        stopX = centerX + lineWidth;
        stopY = centerY;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Third corner
        centerX = right;
        centerY = bottom;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY - lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

        //Fourth corner
        centerX = right;
        centerY = top;
        startX = centerX - lineWidth;
        startY = centerY;
        stopX = centerX;
        stopY = centerY + lineWidth;
        canvas.drawPath(handleLinePath(startX, startY, centerX, centerY, stopX, stopY), linePaint);

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

        data.innerLeft = (int) smallPaperLeft;
        data.innerTop = (int) smallPaperTop;
        data.innerRight = (int) smallPaperRight;
        data.innerBottom = (int) smallPaperBottom;
        data.innerWidth = data.innerRight - data.innerLeft;
        data.innerHeight = data.innerBottom - data.innerTop;

        data.outerLeft = (int) left;
        data.outerTop = (int) top;
        data.outerWidth = (int) (right - left);
        data.outerHeight = (int) (bottom - top);
        /*int offsetX = (int) left;
        int offsetY = (int) top;
        Path path = handlePath(paperCoordinatesData, offsetX, offsetY);
        if (path != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setColor(Color.GREEN);
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(path, linePaint);
        }

        Path pathSmall = handleSmallPath(paperCoordinatesData, offsetX, offsetY);
        if (pathSmall != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setColor(Color.GREEN);
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(pathSmall, linePaint);
        }

        Path pathQr= handleQrPath(paperCoordinatesData, offsetX, offsetY);
        if (pathQr != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setColor(Color.GREEN);
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(pathQr, linePaint);
        }*/

        int offsetX = -(height - width) / 2;
        int offsetY = 0;
        Path path1 = handleCardOverlayPath1(paperCoordinatesData);
        if (path1 != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(path1, linePaint);
        }
        Path path2 = handleCardOverlayPath2(paperCoordinatesData);
        if (path2 != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(path2, linePaint);
        }
        Path path3 = handleCardOverlayPath3(paperCoordinatesData);
        if (path3 != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(path3, linePaint);
        }
        Path path4 = handleCardOverlayPath4(paperCoordinatesData);
        if (path4 != null) {
            //linePaint.setPathEffect(new DashPathEffect(new float[]{12, 12}, 0));
            linePaint.setStrokeWidth(PxDxUtil.dip2px(getContext(), 2));
            canvas.drawPath(path4, linePaint);
        }
//        Path pathCardOverlay = handleCardOverlayPath(paperCoordinatesData);
//        if (pathCardOverlay != null) {
//            canvas.drawPath(pathCardOverlay, cardOverlayPaint);
//        }

        Path path = handlePaperPath(paperCoordinatesData, 0, 0);
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

    private PaperCoordinatesData lastPaperCoordinatesData;
    private long errorTime;

    private void drawScanResult(Canvas canvas) {
        if (paperCoordinatesData != null && lastPaperCoordinatesData != null) {
            if (paperCoordinatesData.getCode() == AiCode.CODE_1 && errorTime > 0 && System.currentTimeMillis() - errorTime <= 2000) {
                paperCoordinatesData = lastPaperCoordinatesData;
            } else {
                lastPaperCoordinatesData = paperCoordinatesData;
                errorTime = System.currentTimeMillis();
            }
        } else {
            lastPaperCoordinatesData = paperCoordinatesData;
            errorTime = System.currentTimeMillis();
        }
        String scanResult = handleData(paperCoordinatesData);
        if (!TextUtils.isEmpty(scanResult)) {
            textPaint.setTextSize(Utils.sp2px(context, 14f));
            float scanResultWidth = textPaint.measureText(scanResult);
            float scanResultBackgroundWidth = scanResultWidth * 1.5f;
            float scanResultBackgroundHeight = Utils.dp2px(context, 45);
            float layerLeft = (width - scanResultBackgroundWidth) / 2;
            float layerTop = width / 2 - scanResultBackgroundHeight / 2 - destBitmapHeight / 2;
            float layerRight = layerLeft + scanResultBackgroundWidth;
            float layerBottom = layerTop + scanResultBackgroundHeight;
            //Set a new rectangle
            RectF layerOval = new RectF(layerLeft, layerTop, layerRight, layerBottom);
            //The second parameter is the x radius, and the third parameter is the y radius
            canvas.drawRoundRect(layerOval, 10, 10, textBackgroundPaint);
            float x = (width - scanResultWidth) / 2;
            float y = width / 2 + 10 - destBitmapHeight / 2;
            canvas.drawText(scanResult, x, y, textPaint);
        }

    }

    private void drawHint(Canvas canvas) {
        String hint = "请将笔型试纸放入取景框内";
        if (!TextUtils.isEmpty(hint)) {
            textPaint.setTextSize(Utils.sp2px(context, 12f));
            float hintWidth = textPaint.measureText(hint);
            float x = (width - hintWidth) / 2;
            float y = (height / 2 - destBitmapHeight / 2 - destBitmapHeight) / 2;
            StaticLayout layout = new StaticLayout(hint, textPaint, (int) hintWidth, Layout.Alignment.ALIGN_CENTER, 1.2f, 0, false);
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
            if (data.getPoint1() != null && data.getArcRectLeft() != null) {
                int padding = 0;
                boolean isChange = false;
                Path p = new Path();
                p.moveTo(data.getPoint1().x + offsetX, data.getPoint1().y + offsetY);
                RectF rectF = new RectF(data.getArcRectLeft().x + offsetX - padding, data.getArcRectLeft().y + offsetY - padding, data.getArcRectLeft().x + offsetX + data.getRadius() * 2 + padding, data.getArcRectLeft().y + offsetY + data.getRadius() * 2 + padding);
                p.arcTo(rectF, -90 - data.getDegree() - 180, 180, false);
                p.moveTo(data.getPoint2().x + offsetX, data.getPoint2().y + offsetY);
                p.lineTo(data.getPoint3().x + offsetX, data.getPoint3().y + offsetY);
                rectF = new RectF(data.getArcRectRight().x + offsetX - padding, data.getArcRectRight().y + offsetY - padding, data.getArcRectRight().x + offsetX + data.getRadius() * 2 + padding, data.getArcRectRight().y + offsetY + data.getRadius() * 2 + padding);
                p.arcTo(rectF, -90 - data.getDegree(), 180, false);
                p.moveTo(data.getPoint4().x + offsetX, data.getPoint4().y + offsetY);
                p.lineTo(data.getPoint1().x + offsetX, data.getPoint1().y + offsetY);
                p.close();

//                Path p = new Path();
//                p.moveTo(data.getPoint1().x + offsetX, data.getPoint1().y + offsetY);
//                p.lineTo(data.getPoint2().x + offsetX, data.getPoint2().y + offsetY);
//                p.lineTo(data.getPoint3().x + offsetX, data.getPoint3().y + offsetY);
//                p.lineTo(data.getPoint4().x + offsetX, data.getPoint4().y + offsetY);
//                p.close();
                return p;
            }
        }

        return null;
    }

    private Path handlePaperPath(PaperCoordinatesData data, int offsetX, int offsetY) {
        if (data != null) {
            if (data.getPoint1() != null && data.getPoint3()!= null) {
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

    private Path handleQrPath(PaperCoordinatesData data, int offsetX, int offsetY) {
        if (data != null) {
            if (data.getPointQr1() != null && data.getPointQr2()!= null) {
                Path p = new Path();
                p.moveTo(data.getPointQr1().x + offsetX, data.getPointQr1().y + offsetY);
                p.lineTo(data.getPointQr2().x + offsetX, data.getPointQr2().y + offsetY);
                p.lineTo(data.getPointQr3().x + offsetX, data.getPointQr3().y + offsetY);
                p.lineTo(data.getPointQr4().x + offsetX, data.getPointQr4().y + offsetY);
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
        public int outerLeft;
        public int outerTop;
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

