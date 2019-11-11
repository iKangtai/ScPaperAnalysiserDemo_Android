package com.example.paperdemo.ui.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.event.ICameraAnalysisEvent;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {
    private CameraSurfaceView surfaceView;
    private SmartPaperMeasureContainerLayout smartPaperMeasureContainerLayout;
    private Camera mCamera;
    private Camera.Parameters mParams;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_video, container, false);
        root.findViewById(R.id.camera_scrollview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView(root);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initCamera();
            }
        }, 200);

        return root;
    }

    private void initView(View view) {
        surfaceView = view.findViewById(R.id.camera_surfaceview);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCamera != null) {
                    Camera.Parameters params = mCamera.getParameters();
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mCamera.setParameters(params);

                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {

                        }
                    });
                }
                return true;
            }
        });
        smartPaperMeasureContainerLayout = view.findViewById(R.id.scan_view);
    }


    /**
     * 实时预览回调
     */
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {

        }
    };

    private void initCamera() {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(getContext(), "打开相机失败");
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
                layoutParams.height = (int) (pWidth * pHeight * 1.0 / surfaceView.getWidth());
                surfaceView.setLayoutParams(layoutParams);

            }
        });
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

    @Override
    public void onStop() {
        super.onStop();
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}