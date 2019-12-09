package com.example.paperdemo.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.event.ICameraAnalysisEvent;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.TensorFlowTools;
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
    private PaperAnalysiserClient paperAnalysiserClient;
    public static String appId = "100017";
    public static String appSecret = "b1eed2fb4686e1b1049a9486d49ba015af00d5a0";
    private long startTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Config.setNetTimeOut(30);
        Config.setTestServer(true);
        //初始化sdk
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
        Config config = new Config.Builder().pixelOfdExtended(true).margin(5).build();
        paperAnalysiserClient.init(config);

        View root = inflater.inflate(R.layout.fragment_video, container, false);
        root.findViewById(R.id.camera_scrollview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView(root);

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

    private ICameraAnalysisEvent iCameraAnalysisEvent = new ICameraAnalysisEvent() {
        @Override
        public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
            ToastUtils.show(getContext(), "抠图最终结果");
            AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            if (volume != 0) {
                MediaPlayer shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                shootMP.start();
            }
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, originSquareBitmap);
            return false;
        }

        @Override
        public void analysisResult(PaperCoordinatesData paperCoordinatesData) {
            Log.d("xyl", "抠图耗时 " + (System.currentTimeMillis() - startTime));
            ToastUtils.show(getContext(), "抠图中间结果");
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
        }

        @Override
        public void analysisEnd(Bitmap originSquareBitmap, int code, String errorResult) {
            ToastUtils.show(getContext(), "抠图超时");
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);
            paperAnalysiserClient.reset();
            paperAnalysiserClient.setObtainPreviewFrame(false);
        }

        @Override
        public void showProgressDialog() {

        }

        @Override
        public void dismissProgressDialog() {

        }

        @Override
        public void cancel() {

            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);
            //重新开始扫描
            paperAnalysiserClient.reset();
            paperAnalysiserClient.setObtainPreviewFrame(false);
        }

        @Override
        public void save(PaperResult paperResult) {
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);

            //显示试纸结果
            paperResult.setPaperBitmap(null);
            paperResult.setNoMarginBitmap(null);
            Intent intent = new Intent(getContext(), PaperDetailActivity.class);
            intent.putExtra("bean", paperResult);
            startActivityForResult(intent, 1002);
        }

        @Override
        public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
            Log.d("xyl", "抠图耗时 " + (System.currentTimeMillis() - startTime));
            if (paperCoordinatesData == null) {
                paperCoordinatesData = new PaperCoordinatesData();
            }
            paperCoordinatesData.setCode(code);
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
        }

        @Override
        public void saasAnalysisError(String errorResult, int code) {
            ToastUtils.show(getContext(), errorResult + code);
        }
    };
    /**
     * 实时预览回调
     */
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {
            if (paperAnalysiserClient.isObtainPreviewFrame()) {
                return;
            }
            startTime = System.currentTimeMillis();
            //视频上半部分正方形图片
            Bitmap originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, surfaceView.getWidth(), surfaceView.getHeight(), TensorFlowTools.getDegree(getActivity()));
            paperAnalysiserClient.analysisCameraData(originSquareBitmap, camera);
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
        paperAnalysiserClient.setCameraDataCallback(iCameraAnalysisEvent);
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
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initCamera();
            }
        }, 200);
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
        paperAnalysiserClient.closeSession();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                int paperValue = data.getIntExtra("paperValue", 0);
                //手动修改lhValue
                paperAnalysiserClient.updatePaperValue(paperValue);
            }

            //重新开始扫描
            paperAnalysiserClient.reset();
            paperAnalysiserClient.setObtainPreviewFrame(false);
        }
    }
}