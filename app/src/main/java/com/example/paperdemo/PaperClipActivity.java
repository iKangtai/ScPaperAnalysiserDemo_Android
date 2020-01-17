package com.example.paperdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.paperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.paperdemo.view.TopBar;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.event.IBaseAnalysisEvent;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.PxDxUtil;

import java.io.File;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 22:23
 */
public class PaperClipActivity extends Activity implements View.OnTouchListener {
    private ImageView srcPic;
    private ManualSmartPaperMeasureLayout measureLayout;
    private TopBar topBar;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    float oldRotation = 0;

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;
    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;
    /**
     * 动作标志：缩放
     */
    private static final int ZOOM = 2;
    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 记录起始坐标
     */
    private PointF start = new PointF();
    /**
     * 记录缩放时两指中间点坐标
     */
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private Bitmap bitmap;

    private ImageView mImageView;
    private String uriStr;
    private PaperAnalysiserClient paperAnalysiserClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setTestServer(true);
        Config.setNetTimeOut(30);
        //初始化sdk
        paperAnalysiserClient = new PaperAnalysiserClient(this, AppConstant.appId, AppConstant.appSecret,"xyl1@qq.com");
        //试纸识别sdk相关配置
        Config config = new Config.Builder().margin(10).build();
        paperAnalysiserClient.init(config);

        setContentView(R.layout.activity_paper_clip_picture);
        srcPic = this.findViewById(R.id.src_pic);
        mImageView = srcPic;
        measureLayout = findViewById(R.id.paper_clip_measureLayout);
        srcPic.setOnTouchListener(this);
        ViewTreeObserver observer = srcPic.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                srcPic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initClipView();
            }
        });

        initTopBar();
        findViewById(R.id.paper_clip_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageView == null) {
                    return;
                }
                Bitmap clipBitmap = getBitmap();
                ManualSmartPaperMeasureLayout.Data data = measureLayout.getData();
                int x = data.innerLeft;
                int y = data.innerTop + measureLayout.getTop() - PxDxUtil.dip2px(PaperClipActivity.this, 50);
                int width = data.innerWidth;
                int height = data.innerHeight;
                Point upLeftPoint = new Point(x, y);
                Point rightBottomPoint = new Point(x + width, y + height);

                paperAnalysiserClient.analysisClipBitmapFromPhoto(clipBitmap, upLeftPoint, rightBottomPoint, new IBaseAnalysisEvent() {
                    @Override
                    public void showProgressDialog() {

                    }

                    @Override
                    public void dismissProgressDialog() {

                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void save(PaperResult paperResult) {
                        FileUtil.saveBitmap(paperResult.getPaperBitmap(),paperResult.getPaperId());
                        paperResult.setNoMarginBitmap(null);
                        paperResult.setPaperBitmap(null);
                        Intent intent = new Intent(PaperClipActivity.this, PaperDetailActivity.class);
                        intent.putExtra("bean", paperResult);
                        startActivityForResult(intent, 2001);
                    }

                    @Override
                    public void saasAnalysisError(String errorResult, int code) {

                    }
                });
            }
        });
    }

    private void initTopBar() {
        topBar = findViewById(R.id.topBar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void midLeftClick() {

            }

            @Override
            public void midRightClick() {

            }

            @Override
            public void rightClick() {

            }
        });

    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     */
    private void initClipView() {
        Intent intent = getIntent();
        uriStr = intent.getStringExtra("paperUri");
        File file = ImageUtil.getFileFromUril(uriStr);
        bitmap = ImageUtil.getBitmapByFile(file);

        measureLayout.post(new Runnable() {
            @Override
            public void run() {
                int clipHeight = srcPic.getHeight();
                int clipWidth = srcPic.getWidth();

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = imageHeight * scale / 2;
                srcPic.setScaleType(ImageView.ScaleType.MATRIX);

                // 缩放
                matrix.postScale(scale, scale);
                // 平移
                matrix.postTranslate(0, clipHeight / 2 - imageMidY);

                srcPic.setImageMatrix(matrix);
                srcPic.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mImageView = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                // 设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                oldRotation = rotation(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float rotation = rotation(event) - oldRotation;
                        float scale = newDist / oldDist;
                        // 缩放
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        // 旋转
                        matrix.postRotate(rotation, mid.x, mid.y);
                    }
                }
                break;
        }
        mImageView.setImageMatrix(matrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        return bitmap;
    }

    private Bitmap getPaperBitmap() {

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        int x = measureLayout.getLeft();
        int y = measureLayout.getTop() -
                PxDxUtil.dip2px(this, 50);
        int width = measureLayout.getWidth();
        int height = measureLayout.getHeight();
        Bitmap finalBitmap = Bitmap.createBitmap(bitmap,
                x, y, width, height);

        // 释放资源
        mImageView.destroyDrawingCache();
        return finalBitmap;
    }

    /**
     * 取旋转角度
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        mImageView.destroyDrawingCache();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001) {
            if (resultCode == Activity.RESULT_OK) {
                int paperValue = data.getIntExtra("paperValue", 0);
                //手动修改lhValue
                paperAnalysiserClient.updatePaperValue(paperValue);
            }
            setResult(RESULT_OK);
            finish();
        }
    }
}
