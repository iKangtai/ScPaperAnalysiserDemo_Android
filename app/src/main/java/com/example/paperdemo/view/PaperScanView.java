package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.util.Utils;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 16:40
 */
public class PaperScanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    protected SurfaceHolder sh;
    public int error;
    private int height;

    private Paint mGreenPen;
    private final Bitmap scanningBmp;
    private boolean scanning = true;
    private boolean isDrawMatch = true;
    private boolean stopThread = true;
    private Thread thread;
    private boolean isCardMode = false;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    public PaperScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);

        mGreenPen = new Paint();
        mGreenPen.setAntiAlias(true);
        mGreenPen.setColor(Color.GREEN);
        mGreenPen.setStyle(Paint.Style.STROKE);
        scanningBmp = BitmapFactory.decodeResource(getResources(), R.drawable.qrcode_default_scan_line);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        thread = new Thread(this);
        stopThread = true;
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        stopThread = false;
    }

    @Override
    public void run() {
        int i = 0;
        while (stopThread) {
            if (scanning) {
                drawScanning(i++);
            } else {
                if (isDrawMatch) {
                    i = 0;
                    isDrawMatch = false;
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void drawScan() {
        stopThread = true;
        scanning = true;
    }

    public void drawStop() {
        stopThread = false;
    }

    public boolean isCardMode() {
        return isCardMode;
    }

    public void setCardMode(boolean cardMode) {
        isCardMode = cardMode;
    }

    private void drawScanning(int i) {
        int width = getWidth();
        Canvas canvas = sh.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        if (isCardMode) {
            int padding = Utils.dp2px(getContext(), 10);
            //Reference strip for drawing test paper
            int destBitmapWidth = width - padding * 2;
            int destBitmapHeight = destBitmapWidth * 177 / 1053;
            int paperHeight = width * 2 / 7;
            float fixY = (paperHeight - destBitmapHeight) / 2;
            float paperTop = width / 2 + Utils.dp2px(getContext(), 10);
            float centerLeft = padding + destBitmapWidth * 124 / 351f;
            float centerTop = paperTop + fixY + destBitmapHeight * 10 / 60f;

            float centerRight = padding + destBitmapWidth * 232 / 351f;
            float centerBottom = paperTop + fixY + destBitmapHeight * 55 / 60f;

            int scanWidth = (int) (centerRight - centerLeft);
            int scanHeight = (int) (centerBottom - centerTop);
            // Draw scan bar
            float rate = (float) i % 150 / 150;
            int srcW = (int) (rate * scanWidth);
            Rect findingSrc = new Rect(0, 0, scanningBmp.getWidth(), scanningBmp.getHeight());

            Rect findingDes = new Rect();
            findingDes.left = (int) (centerLeft + srcW - scanningBmp.getWidth());
            findingDes.top = (int) centerTop;
            findingDes.right = (int) (centerLeft + srcW);
            if (findingDes.left < centerLeft) {
                findingDes.left = (int) centerLeft;
            }
            if (findingDes.right > centerRight) {
                findingDes.right = (int) centerRight;
            }
            findingDes.bottom = (int) centerBottom;
            canvas.drawBitmap(scanningBmp, findingSrc, findingDes, mGreenPen);
        } else {
            // Draw scan bar
            float rate = (float) i % 250 / 250;
            int srcW = rate < 1 ? (int) (rate * width) : width;
            Rect findingSrc = new Rect(0, 0, srcW, scanningBmp.getHeight());

            Rect findingDes = new Rect();
            if (rate >= 1) {
                findingDes.left = (int) (srcW * rate);
            } else {
                findingDes.left = srcW;
            }
            findingDes.top = getHeight() / 4;
            if (rate >= 1) {
                findingDes.right = (int) (srcW * (rate + 1));
            } else {
                findingDes.right = srcW * 2;
            }
            findingDes.bottom = getHeight() * 3 / 4;
            canvas.drawBitmap(scanningBmp, findingSrc, findingDes, mGreenPen);
        }
        try {
            if (sh != null) {
                sh.unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

