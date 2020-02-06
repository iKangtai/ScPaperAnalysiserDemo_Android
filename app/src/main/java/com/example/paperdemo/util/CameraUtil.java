package com.example.paperdemo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 17:02
 */
public class CameraUtil {
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean openFlashLight;
    private boolean isPreviewing;
    private Activity activity;
    private SurfaceView surfaceView;
    private Camera.PreviewCallback outsidePreviewCallback;
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isPreviewing = true;
            outsidePreviewCallback.onPreviewFrame(data, camera);
        }
    };

    public void holdFocus(Rect focusRect) {
        if (mCamera != null) {
            mCamera.cancelAutoFocus();
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            if (focusRect != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                List<Camera.Area> focus = new ArrayList<>();
                focus.add(new Camera.Area(focusRect, 1000));
                params.setFocusAreas(focus);

                if (params.getMaxNumMeteringAreas() > 0) {
                    List<Camera.Area> metering = new ArrayList<>();
                    metering.add(new Camera.Area(focusRect, 1000));
                    params.setMeteringAreas(metering);
                }
            }else {
                params.setFocusAreas(null);
                params.setMeteringAreas(null);
            }
            mCamera.setParameters(params);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    mParams = mCamera.getParameters();
                    //1连续对焦
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    List<String> focusModes = mParams.getSupportedFocusModes();
                    if (focusModes.contains("continuous-video")) {
                        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }
                    mCamera.setParameters(mParams);
                    // 2如果要实现连续的自动对焦
                    mCamera.cancelAutoFocus();
                }
            });
        }
    }

    public void focusOnTouch() {
        holdFocus(null);
    }

    public void initCamera(Activity context, final SurfaceView surfaceView, final Camera.PreviewCallback previewCallback) {
        this.activity = context;
        this.surfaceView = surfaceView;
        this.outsidePreviewCallback = previewCallback;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(context, "打开相机失败");
            return;
        }
        mParams = mCamera.getParameters();
        mParams.setPictureFormat(PixelFormat.JPEG);
        mParams.setPreviewFormat(ImageFormat.NV21);
        chooseFixedPreviewFps(mParams, 30);
        Camera.Size psize = parameters(mCamera);
        final int pWidth = psize.width;
        final int pHeight = psize.height;
        mParams.setPictureSize(pWidth, pHeight);
        mParams.setPreviewSize(pWidth, pHeight);

        //1连续对焦
        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        //设置图片角度
        mCamera.setDisplayOrientation(90);
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains("continuous-video")) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        mCamera.setParameters(mParams);
        // 2如果要实现连续的自动对焦
        mCamera.cancelAutoFocus();
        mCamera.setPreviewCallback(mPreviewCallback);
        try {
            mCamera.setPreviewDisplay(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mParams = mCamera.getParameters();
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                layoutParams.height = (int) (pWidth * 1.0 * surfaceView.getWidth() / pHeight);
                surfaceView.setLayoutParams(layoutParams);

            }
        });
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    /**
     * 拍照 获取原图
     * @param event
     */
    public void takePicture(final ICameraTakeEvent event) {
        if (isPreviewing && (mCamera != null)) {
            mCamera.cancelAutoFocus();
            mCamera.takePicture(mShutterCallback, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //照片尺寸与用户所见的Camera成像尺寸存在拉伸情况时需进行处理
                    Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length);
                    int degree = TensorFlowTools.getDegree(activity);
                    source = ImageUtil.rotateBitmapByDegree(source, degree);
                    int sourceWidth = source.getWidth();
                    int sourceHeight = source.getHeight();
                    int surfaceViewWidth = surfaceView.getWidth();
                    int surfaceViewHeight = surfaceView.getHeight();
                    float scaleWidth = ((float) surfaceViewWidth) / sourceWidth;
                    float scaleHeight = ((float) surfaceViewHeight) / sourceHeight;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    source = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
                    //裁剪为正方形
                    source = Bitmap.createBitmap(source, 0, 0, surfaceViewWidth, surfaceViewWidth);
                    mCamera.cancelAutoFocus();
                    mCamera.setPreviewCallback(mPreviewCallback);
                    try {
                        mCamera.setPreviewDisplay(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                    if (event != null) {
                        event.takeBitmap(source);
                    }
                }
            });
        }
    }

    public boolean isOpenFlashLight() {
        return openFlashLight;
    }

    public void setOpenFlashLight(boolean openFlashLight) {
        this.openFlashLight = openFlashLight;
    }

    public void openFlashLight() {
        try {
            setOpenFlashLight(true);
            Camera.Parameters mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParameters);
        } catch (Exception ex) {
            LogUtils.e("openFlashLight>>" + ex.getMessage());
        }

    }

    public void closeFlashLight() {
        try {
            setOpenFlashLight(false);
            Camera.Parameters mParameters = mCamera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParameters);
        } catch (Exception ex) {
            LogUtils.e("closeFlashLight>>" + ex.getMessage());
        }

    }

    public static int chooseFixedPreviewFps(Camera.Parameters parameters, int expectedThoudandFps) {
        List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        for (int[] entry : supportedFps) {
            if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                parameters.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }
        int[] temp = new int[2];
        int guess;
        parameters.getPreviewFpsRange(temp);
        if (temp[0] == temp[1]) {
            guess = temp[0];
        } else {
            guess = temp[1] / 2;
        }
        return guess;
    }

    public Camera.Size parameters(Camera camera) {
        List<Camera.Size> pictureSizes = camera.getParameters().getSupportedPictureSizes();
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();

        /*
        降序排序
         */
        Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width < rhs.width) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width < rhs.width) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        Camera.Size psizePicture;
        Camera.Size psizePreview;
        for (int i = 0; i < pictureSizes.size(); i++) {
            psizePicture = pictureSizes.get(i);
            for (int j = 0; j < previewSizes.size(); j++) {
                psizePreview = previewSizes.get(j);
                //当两分辨率相同时，以这时的数据来对相机进行设置
                if (psizePicture.width == psizePreview.width
                        && psizePicture.height == psizePreview.height) {
                    return psizePicture;
                }
            }
        }
        return null;
    }

    public void stopCamera() {
        if (isOpenFlashLight()) {
            closeFlashLight();
        }
        if (null != mCamera) {
            isPreviewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public interface ICameraPreviewEvent {
        /**
         * 预览原图 正方形
         *
         * @param mBitmap
         */
        void previewBitmap(Bitmap mBitmap);

    }


    public interface ICameraTakeEvent {
        /**
         * 照原图 正方形
         *
         * @param bitmap
         */
        void takeBitmap(Bitmap bitmap);
    }
}
