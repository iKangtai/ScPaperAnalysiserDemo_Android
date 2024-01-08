package com.example.paperdemo;

import android.app.Activity;
import android.app.Dialog;
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
import com.example.paperdemo.view.ProgressDialog;
import com.example.paperdemo.view.TopBar;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.PaperResultDialog;
import com.ikangtai.papersdk.event.IBaseAnalysisEvent;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.PxDxUtil;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.File;

/**
 * Test paper cutout
 *
 * @author
 */
public class PaperClipActivity extends Activity implements View.OnTouchListener {
    private ImageView srcPic;
    private ManualSmartPaperMeasureLayout measureLayout;
    private TopBar topBar;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    float oldRotation = 0;

    /**
     * Action flag: None
     */
    private static final int NONE = 0;
    /**
     * Action sign: drag
     */
    private static final int DRAG = 1;
    /**
     * Action flag: zoom
     */
    private static final int ZOOM = 2;
    /**
     * Init action flag
     */
    private int mode = NONE;

    /**
     * Record start coordinates
     */
    private PointF start = new PointF();
    /**
     * Record the coordinates of the middle point of the two fingers when zooming
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
        Config.setNetTimeOut(30);
        //Test paper to identify sdk related configuration
        Config config = new Config.Builder().build();
        //init sdk
        paperAnalysiserClient = new PaperAnalysiserClient(this, AppConstant.appId, AppConstant.appSecret, "xyl1@qq.com", config);

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
                        LogUtils.d("Show Loading Dialog");
                        PaperClipActivity.this.showProgressDialog(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                paperAnalysiserClient.stopShowProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void dismissProgressDialog() {
                        LogUtils.d("Hide Loading Dialog");
                        PaperClipActivity.this.dismissProgressDialog();
                    }

                    @Override
                    public void cancel() {
                        LogUtils.d("Cancel test strip result confirmation");
                    }

                    @Override
                    public void save(PaperResult paperResult) {
                        LogUtils.d("Save test paper analysis results：\n" + paperResult.toString());
                        if (paperResult.getErrNo() != 0) {
                            ToastUtils.show(PaperClipActivity.this, AiCode.getMessage(paperResult.getErrNo()));
                        }
                        FileUtil.saveBitmap(paperResult.getPaperBitmap(), paperResult.getPaperId());
                        paperResult.setNoMarginBitmap(null);
                        paperResult.setPaperBitmap(null);
                        Intent intent = new Intent(PaperClipActivity.this, PaperDetailActivity.class);
                        intent.putExtra("bean", paperResult);
                        startActivityForResult(intent, 2001);
                    }

                    @Override
                    public void saasAnalysisError(String errorResult, int code) {
                        LogUtils.d("Test strip analysis error code：" + code + " errorResult:" + errorResult);
                    }

                    @Override
                    public void paperResultDialogShow(PaperResultDialog paperResultDialog) {

                    }
                });
            }
        });
    }

    private Dialog progressDialog;

    public void showProgressDialog(View.OnClickListener onClickListener) {
        progressDialog = ProgressDialog.createLoadingDialog(this, onClickListener);
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing()) {
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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
     * Initialize the screenshot area, and zoom the source image according to the crop box ratio
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
                // Seek zoom ratio by crop box
                float scale = (clipWidth * 1.0f) / imageWidth;
                // Starting center point
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = imageHeight * scale / 2;
                srcPic.setScaleType(ImageView.ScaleType.MATRIX);

                // zoom
                matrix.postScale(scale, scale);
                // translate
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
                // Set the starting point position
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
                        // zoom
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        // rotate
                        matrix.postRotate(rotation, mid.x, mid.y);
                    }
                }
                break;
        }
        mImageView.setImageMatrix(matrix);
        return true;
    }

    /**
     * For multi-touch, calculate the distance between the first two fingers dropped
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
     * When multi-touch, calculate the center coordinates of the first two fingers dropped
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
     * Get a screenshot in the crop box
     *
     * @return
     */
    private Bitmap getBitmap() {

        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        return bitmap;
    }

    /**
     * Take the rotation angle
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
        // release
        mImageView.destroyDrawingCache();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001) {
            if (resultCode == Activity.RESULT_OK) {
                int paperValue = data.getIntExtra("paperValue", 0);
                //Manually modify lhValue
                paperAnalysiserClient.updatePaperValue(paperValue);
            }
            setResult(RESULT_OK);
            finish();
        }
    }
}
