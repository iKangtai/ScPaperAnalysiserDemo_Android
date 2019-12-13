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

/**
 * desc
 *
 * @author xiongyl 2019/12/11 16:40
 */
public class PaperScanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int SCAN_COUNT = 190;

    protected SurfaceHolder sh;
    private Paint mGreenPen;
    private final Bitmap scanningBmp;
    private boolean scanning = true;
    private boolean isDrawMatch = true;
    private boolean stopThread = true;
    private Thread thread;

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    // XML文件解析需要调用View的构造函数View(Context , AttributeSet)
    // 因此自定义SurfaceView中也需要该构造函数
    public PaperScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT); // 设置为透明
        setZOrderOnTop(true);// 设置为顶端

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

    private void drawScanning(int i) {
        int width = getWidth();
        Canvas canvas = sh.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


        // 绘制扫描条
        float rate = (float) i % SCAN_COUNT / 100;
        int srcW = rate < 1 ? (int) (rate * width) : width;
        Rect findingSrc = new Rect(0, 0, srcW, scanningBmp.getHeight());

        Rect findingDes = new Rect();
        if (rate >= 1) {
            findingDes.left = (int) (width - srcW * rate);
        } else {
            findingDes.left = width - srcW;
        }
        findingDes.top = scanningBmp.getHeight() / 4;
        if (rate >= 1) {
            findingDes.right = (int) (width - srcW * (rate - 1));
        } else {
            findingDes.right = width;

        }
        findingDes.bottom = getHeight() * 3 / 4;

        canvas.drawBitmap(scanningBmp, findingSrc, findingDes, mGreenPen);
        try {
            if (sh != null) {
                sh.unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

