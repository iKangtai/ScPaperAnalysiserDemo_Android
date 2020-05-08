package com.example.paperdemo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

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
    private static final String TAG = CameraUtil.class.getName();
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean openFlashLight;
    private boolean isPreviewing;
    private Activity activity;
    private SurfaceView surfaceView;
    private Camera.PreviewCallback outsidePreviewCallback;
    private boolean holdFocusFinish = true;
    private long lastHoldFocusTime;
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            isPreviewing = true;
            outsidePreviewCallback.onPreviewFrame(data, camera);
        }
    };

    public void holdFocus() {
        Rect focusRect = null;
        Rect meteringRect = null;
        if (surfaceView != null) {
            int focusX = surfaceView.getWidth() > surfaceView.getHeight() ? surfaceView.getHeight() / 2 : surfaceView.getWidth() / 2;
            int focusY = focusX;
            focusRect = calculateTapArea(focusX, focusY, 1f);
            meteringRect = calculateTapArea(focusX, focusY, 1.5f);
        }
        holdFocus(focusRect, meteringRect);
    }

    public void holdFocus(Rect focusRect, Rect meteringRect) {
        if (mCamera != null) {
            if (!holdFocusFinish || System.currentTimeMillis() - lastHoldFocusTime < 3000) {
                return;
            }
            lastHoldFocusTime = System.currentTimeMillis();
            holdFocusFinish = false;
            try {
                mCamera.cancelAutoFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            if (focusRect != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                List<Camera.Area> focus = new ArrayList<>();
                focus.add(new Camera.Area(focusRect, 1000));
                params.setFocusAreas(focus);

                if (meteringRect != null && params.getMaxNumMeteringAreas() > 0) {
                    List<Camera.Area> metering = new ArrayList<>();
                    metering.add(new Camera.Area(meteringRect, 1000));
                    params.setMeteringAreas(metering);
                }
            } else {
                params.setFocusAreas(null);
                params.setMeteringAreas(null);
            }
            try {
                mCamera.setParameters(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    holdFocusFinish = true;
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

    public void focusOnTouch(MotionEvent event) {
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
        Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);
        holdFocus(focusRect, meteringRect);

    }

    /**
     * Convert touch position x:y to {@link Camera.Area} position -1000:-1000 to 1000:1000.
     * <p/>
     * Rotate, scale and translate touch rectangle using matrix configured in
     * {@link (android.view.SurfaceHolder, int, int, int)}
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerY = 0;
        int centerX = 0;
        centerY = (int) (x / surfaceView.getWidth() * 2000 - 1000);
        centerX = (int) (y / surfaceView.getHeight() * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
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
        mParams.setJpegQuality(100);
        mParams.setPreviewFormat(ImageFormat.NV21);
        //chooseFixedPreviewFps(mParams, 30);
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth  = display.getWidth();
        int screenHeight = display.getHeight();

        Point screenResolution=new Point(screenWidth,screenHeight);
        final Point previewSize = findBestPreviewSizeValue(mCamera.getParameters().getSupportedPreviewSizes(),screenResolution);
        Point pictureSize = findBestPictureSizeValue(mCamera.getParameters().getSupportedPictureSizes(),screenResolution);

        //LogUtils.d("previewSize:"+previewSize.x+" "+previewSize.y+" pictureSize:"+pictureSize.x+" "+pictureSize.y);
        mParams.setPictureSize(pictureSize.x, pictureSize.y);
        mParams.setPreviewSize(previewSize.x, previewSize.y);
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
                layoutParams.height = (int) (previewSize.x * 1.0 * surfaceView.getWidth() / previewSize.y);
                surfaceView.setLayoutParams(layoutParams);
                //holdFocus();
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
     *
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

    private static final int DEFAULT_WIDTH = 1920, DEFAULT_HEIGHT = 1080;

    private static Point findBestPreviewSizeValue(List<Camera.Size> sizeList, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int size = 0;
        for (int i = 0; i < sizeList.size(); i++) {
            // 如果有符合的分辨率，则直接返回
            if (sizeList.get(i).width == DEFAULT_WIDTH && sizeList.get(i).height == DEFAULT_HEIGHT) {
                Log.d(TAG, "get default preview size!!!");
                return new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            }

            int newX = sizeList.get(i).width;
            int newY = sizeList.get(i).height;
            int newSize = Math.abs(newX * newX) + Math.abs(newY * newY);
            float ratio = (float) newY / (float) newX;
            Log.d(TAG, newX + ":" + newY + ":" + ratio);
            if (newSize >= size && ratio != 0.75) {  // 确保图片是16：9的
                bestX = newX;
                bestY = newY;
                size = newSize;
            } else if (newSize < size) {
                continue;
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private static Point findBestPictureSizeValue(List<Camera.Size> sizeList, Point screenResolution) {
        List<Camera.Size> tempList = new ArrayList<>();
        for (int i = 0; i < sizeList.size(); i++) {
            // 如果有符合的分辨率，则直接返回
            if (sizeList.get(i).width == DEFAULT_WIDTH && sizeList.get(i).height == DEFAULT_HEIGHT) {
                Log.d(TAG, "get default picture size!!!");
                return new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            }
            if (sizeList.get(i).width >= screenResolution.x && sizeList.get(i).height >= screenResolution.y) {
                tempList.add(sizeList.get(i));
            }
        }

        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        if (tempList != null && tempList.size() > 0) {
            for (int i = 0; i < tempList.size(); i++) {
                int newDiff = Math.abs(tempList.get(i).width - screenResolution.x) + Math.abs(tempList.get(i).height - screenResolution.y);
                float ratio = (float) tempList.get(i).height / tempList.get(i).width;
                Log.d(TAG, "ratio = " + ratio);
                if (newDiff < diff && ratio != 0.75) {  // 确保图片是16：9的
                    bestX = tempList.get(i).width;
                    bestY = tempList.get(i).height;
                    diff = newDiff;
                }
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        } else {
            return findMaxPictureSizeValue(sizeList);
        }
    }

    public static Point findMaxPictureSizeValue(List<Camera.Size> sizeList) {


        /*
        降序排序
         */
        Collections.sort(sizeList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width < rhs.width) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return sizeList != null && sizeList.size() > 0 ? new Point(sizeList.get(0).width, sizeList.get(0).height) : null;
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
