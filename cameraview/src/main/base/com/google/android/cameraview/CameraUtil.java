package com.google.android.cameraview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.Image;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import java.nio.ByteBuffer;

public class CameraUtil {
    private static final String TAG = CameraUtil.class.getName();
    private CameraView cameraView;
    private int lightFix = 0;
    private long lastHoldFocusTime;
    private boolean holdCameraCenter = false;
    private boolean holdCameraHigh = false;
    private boolean holdClip = true;

    public int getLightFix() {
        return lightFix;
    }

    public void setLightFix(int lightFix) {
        this.lightFix = lightFix;
    }

    public boolean isHoldCameraCenter() {
        return holdCameraCenter;
    }

    public void setHoldCameraCenter(boolean holdCameraCenter) {
        this.holdCameraCenter = holdCameraCenter;
    }

    public boolean isHoldCameraHigh() {
        return holdCameraHigh;
    }

    public void setHoldCameraHigh(boolean holdCameraHigh) {
        this.holdCameraHigh = holdCameraHigh;
    }

    public boolean isHoldClip() {
        return holdClip;
    }

    public void setHoldClip(boolean holdClip) {
        this.holdClip = holdClip;
    }

    public static int getDegree(Activity context) {
        //度数
        int degree = 0;
        if (context != null) {
            //获取当前屏幕旋转的角度
            int rotating = context.getWindowManager().getDefaultDisplay().getRotation();
            //根据手机旋转的角度，来设置surfaceView的显示的角度
            switch (rotating) {
                case Surface.ROTATION_0:
                    degree = 90;
                    break;
                case Surface.ROTATION_90:
                    degree = 0;
                    break;
                case Surface.ROTATION_180:
                    degree = 270;
                    break;
                case Surface.ROTATION_270:
                    degree = 180;
                    break;
            }
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap b, float rotateDegree) {
        if (b == null) {
            return null;
        }
        if (rotateDegree == 0 || rotateDegree == 360 || rotateDegree == -360) {
            return b;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, int fix) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        int cropHeight = cropWidth;
        if (fix > (h - cropHeight) / 2) {
            fix = (h - cropHeight) / 2;
        }
        Bitmap bitmapNew = Bitmap.createBitmap(bitmap, (w - cropWidth) / 2, (h - cropHeight) / 2 - fix, cropWidth, cropHeight, null, false);
        return bitmapNew;
    }

    public void initCenterCamera(Activity context, final CameraView cameraView, final CameraView.Callback callback) {
        this.holdCameraCenter = true;
        //调整闪光灯焦点移动到显示区域
        this.lightFix = dip2px(context, 50);
        try {
            this.initCamera(context, cameraView, callback);
        } catch (Exception e) {
            e.printStackTrace();
            //ToastUtils.show(context, context.getString(R.string.open_camera_fail));
        }
    }

    public static int dip2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void initCamera(final Activity context, final CameraView cameraView, final CameraView.Callback callback) {
        this.cameraView = cameraView;
        cameraView.setHoldCameraHigh(holdCameraHigh);
        cameraView.removeCallbackAll();
        cameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onCameraOpened(final CameraView cameraView) {
                super.onCameraOpened(cameraView);
                if (cameraView.getPreviewHeight() == 0) {
                    return;
                }
                cameraView.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup.LayoutParams layoutParams = cameraView.getLayoutParams();
                        layoutParams.height = (int) (cameraView.getPreviewWidth() * 1.0 * cameraView.getWidth() / cameraView.getPreviewHeight());
                        if (holdCameraCenter) {
                            View previewView = cameraView.getPreview().getView();
                            ViewGroup.LayoutParams layoutParams1 = previewView.getLayoutParams();
                            layoutParams1.height = layoutParams.height;
                            int topMargin = -(layoutParams.height - previewView.getWidth()) / 2 + lightFix;
                            if (layoutParams1 instanceof ViewGroup.MarginLayoutParams) {
                                ((ViewGroup.MarginLayoutParams) layoutParams1).topMargin = topMargin;
                            }
                            previewView.setLayoutParams(layoutParams1);
                        }
                        cameraView.setLayoutParams(layoutParams);
                    }
                });
                if (callback != null) {
                    callback.onCameraOpened(cameraView);
                }
            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                super.onCameraClosed(cameraView);
                if (callback != null) {
                    callback.onCameraClosed(cameraView);
                }
            }

            @Override
            public void onPictureTaken(CameraView cameraView, byte[] data) {
                super.onPictureTaken(cameraView, data);
                if (callback != null) {
                    Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //int degree = getDegree(context);
                    //source = rotateBitmapByDegree(source, degree);
                    int sourceWidth = source.getWidth();
                    int sourceHeight = source.getHeight();
                    int surfaceViewWidth = 0;
                    int surfaceViewHeight = 0;
                    surfaceViewWidth = cameraView.getWidth();
                    surfaceViewHeight = cameraView.getHeight();
                    if (sourceWidth > sourceHeight && surfaceViewWidth < surfaceViewHeight) {
                        int degree = 90;
                        source = rotateBitmapByDegree(source, degree);
                        sourceWidth = source.getWidth();
                        sourceHeight = source.getHeight();
                    }
                    if (!holdCameraHigh) {
                        float scaleWidth = ((float) surfaceViewWidth) / sourceWidth;
                        float scaleHeight = ((float) surfaceViewHeight) / sourceHeight;
                        Matrix matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        source = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
                    }
                    if (holdClip) {
                        //裁剪为正方形
                        if (holdCameraCenter) {
                            int fix = lightFix;
                            if (holdCameraHigh) {
                                fix = fix * sourceWidth / surfaceViewWidth;
                                source = cropBitmap(source, fix);
                            } else {
                                if (surfaceViewWidth > source.getWidth()) {
                                    source = cropBitmap(source, fix);
                                } else {
                                    if (fix > (surfaceViewHeight - surfaceViewWidth) / 2) {
                                        fix = (surfaceViewHeight - surfaceViewWidth) / 2;
                                    }
                                    source = Bitmap.createBitmap(source, 0, (surfaceViewHeight - surfaceViewWidth) / 2 - fix, surfaceViewWidth, surfaceViewWidth);
                                }
                            }
                        } else {
                            if (holdCameraHigh) {
                                source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getWidth());
                            } else {
                                if (surfaceViewWidth > source.getWidth()) {
                                    source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getWidth());
                                } else {
                                    source = Bitmap.createBitmap(source, 0, 0, surfaceViewWidth, surfaceViewWidth);
                                }
                            }

                        }
                    }
                    callback.onPictureTaken(cameraView, source);
                }
            }

            @Override
            public void onPreviewFrame(CameraView cameraView, byte[] data) {
                super.onPreviewFrame(cameraView, data);
                if (callback != null) {
                    callback.onPreviewFrame(cameraView, data);
                }
            }

            @Override
            public void onPreviewFrame(CameraView cameraView, Image image) {
                super.onPreviewFrame(cameraView, image);
                if (callback != null) {
                    callback.onPreviewFrame(cameraView, image);
                }
            }
        });
    }

    private boolean openFlashLight;

    public boolean isOpenFlashLight() {
        return openFlashLight;
    }

    public void setOpenFlashLight(boolean openFlashLight) {
        this.openFlashLight = openFlashLight;
    }

    public void openFlashLight() {
        try {
            setOpenFlashLight(true);
            cameraView.setFlash(CameraView.FLASH_ON);
        } catch (Exception ex) {
            Log.e(TAG, "openFlashLight>>" + ex.getMessage());
        }

    }

    public void closeFlashLight() {
        try {
            setOpenFlashLight(false);
            cameraView.setFlash(CameraView.FLASH_OFF);
        } catch (Exception ex) {
            Log.e(TAG, "closeFlashLight>>" + ex.getMessage());
        }
    }

    public void startCamera() {
        if (cameraView != null) {
            cameraView.start();
        }
    }

    public void stopCamera() {
        if (cameraView != null) {
            cameraView.stop();
        }
    }

    public void takePicture() {
        cameraView.takePicture();
    }

    //Planar格式（P）的处理
    private static ByteBuffer getuvBufferWithoutPaddingP(ByteBuffer uBuffer, ByteBuffer vBuffer, int width, int height, int rowStride, int pixelStride) {
        int pos = 0;
        byte[] byteArray = new byte[height * width / 2];
        for (int row = 0; row < height / 2; row++) {
            for (int col = 0; col < width / 2; col++) {
                int vuPos = col * pixelStride + row * rowStride;
                byteArray[pos++] = vBuffer.get(vuPos);
                byteArray[pos++] = uBuffer.get(vuPos);
            }
        }
        ByteBuffer bufferWithoutPaddings = ByteBuffer.allocate(byteArray.length);
        // 数组放到buffer中
        bufferWithoutPaddings.put(byteArray);
        //重置 limit 和postion 值否则 buffer 读取数据不对
        bufferWithoutPaddings.flip();
        return bufferWithoutPaddings;
    }

    //Semi-Planar格式（SP）的处理和y通道的数据
    private static ByteBuffer getBufferWithoutPadding(ByteBuffer buffer, int width, int rowStride, int times, boolean isVbuffer) {
        if (width == rowStride) return buffer;  //没有buffer,不用处理。
        int bufferPos = buffer.position();
        int cap = buffer.capacity();
        byte[] byteArray = new byte[times * width];
        int pos = 0;
        //对于y平面，要逐行赋值的次数就是height次。对于uv交替的平面，赋值的次数是height/2次
        for (int i = 0; i < times; i++) {
            buffer.position(bufferPos);
            //part 1.1 对于u,v通道,会缺失最后一个像u值或者v值，因此需要特殊处理，否则会crash
            if (isVbuffer && i == times - 1) {
                width = width - 1;
            }
            buffer.get(byteArray, pos, width);
            bufferPos += rowStride;
            pos = pos + width;
        }

        //nv21数组转成buffer并返回
        ByteBuffer bufferWithoutPaddings = ByteBuffer.allocate(byteArray.length);
        // 数组放到buffer中
        bufferWithoutPaddings.put(byteArray);
        //重置 limit 和postion 值否则 buffer 读取数据不对
        bufferWithoutPaddings.flip();
        return bufferWithoutPaddings;
    }

    public static byte[] YUV_420_888toNV21(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        ByteBuffer yBuffer = getBufferWithoutPadding(image.getPlanes()[0].getBuffer(), image.getWidth(), image.getPlanes()[0].getRowStride(), image.getHeight(), false);
        ByteBuffer vBuffer;
        //part1 获得真正的消除padding的ybuffer和ubuffer。需要对P格式和SP格式做不同的处理。如果是P格式的话只能逐像素去做，性能会降低。
        if (image.getPlanes()[2].getPixelStride() == 1) { //如果为true，说明是P格式。
            vBuffer = getuvBufferWithoutPaddingP(image.getPlanes()[1].getBuffer(), image.getPlanes()[2].getBuffer(),
                    width, height, image.getPlanes()[1].getRowStride(), image.getPlanes()[1].getPixelStride());
        } else {
            vBuffer = getBufferWithoutPadding(image.getPlanes()[2].getBuffer(), image.getWidth(), image.getPlanes()[2].getRowStride(), image.getHeight() / 2, true);
        }

        //part2 将y数据和uv的交替数据（除去最后一个v值）赋值给nv21
        int ySize = yBuffer.remaining();
        int vSize = vBuffer.remaining();
        byte[] nv21;
        int byteSize = width * height * 3 / 2;
        nv21 = new byte[byteSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);

        //part3 最后一个像素值的u值是缺失的，因此需要从u平面取一下。
        ByteBuffer uPlane = image.getPlanes()[1].getBuffer();
        byte lastValue = uPlane.get(uPlane.capacity() - 1);
        nv21[byteSize - 1] = lastValue;
        return nv21;
    }

    public void holdFocus() {
        int fix = 0;
        if (holdCameraCenter) {
            fix = Math.abs(cameraView.getWidth() - cameraView.getHeight()) / 2 + lightFix;
        }
        int focusX = cameraView.getWidth() > cameraView.getHeight() ? cameraView.getHeight() / 2 : cameraView.getWidth() / 2;
        int focusY = focusX + fix;
        holdFocus(focusX, focusY);
    }

    public void holdFocus(float x, float y) {
        if (cameraView != null) {
            if (System.currentTimeMillis() - lastHoldFocusTime < 3000) {
                return;
            }
            lastHoldFocusTime = System.currentTimeMillis();
            cameraView.holdFocus(x, y);
        }
    }

    public void focusOnPoint(Point point) {
        int fix = 0;
        if (holdCameraCenter) {
            fix = Math.abs(cameraView.getWidth() - cameraView.getHeight()) / 2 + lightFix;
        }
        holdFocus(point.x, point.y + fix);
    }
}
