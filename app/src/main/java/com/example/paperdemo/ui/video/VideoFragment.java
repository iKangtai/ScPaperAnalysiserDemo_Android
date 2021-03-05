package com.example.paperdemo.ui.video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paperdemo.AppConstant;
import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.example.paperdemo.util.AiCode;
import com.example.paperdemo.view.CardAutoSmartPaperMeasureLayout;
import com.example.paperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.paperdemo.view.ProgressDialog;
import com.example.paperdemo.view.SmartPaperMeasureContainerLayout;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.PaperResultDialog;
import com.ikangtai.papersdk.event.IBaseAnalysisEvent;
import com.ikangtai.papersdk.event.ICameraAnalysisEvent;
import com.ikangtai.papersdk.event.InitCallback;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.CameraUtil;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Test paper video stream recognition
 *
 * @author
 */
public class VideoFragment extends Fragment {
    private TextureView textureView;
    private SmartPaperMeasureContainerLayout smartPaperMeasureContainerLayout;
    private PaperAnalysiserClient paperAnalysiserClient;
    private long startTime, endTime;
    private CameraUtil cameraUtil;
    private TextView ovulationCameraTips, flashTv, modeSwitchTv, switchCardMode;
    private ImageView shutterBtn;
    private int scanMode;
    private static final int AUTOSMART = 0;
    private static final int MANUALSMART = 1;
    private boolean isCardMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Config.setTestServer(true);
        Config.setNetTimeOut(30);
        //init sdk
        Config config = new Config.Builder().pixelOfdExtended(true).margin(5).build();
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), AppConstant.appId, AppConstant.appSecret, "xyl1@qq.com", config, new InitCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int code, String message) {
                ToastUtils.show(getContext(), message);
            }
        });
        paperAnalysiserClient.setCameraDataCallback(iCameraAnalysisEvent);
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        root.findViewById(R.id.camera_scrollview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        initView(root);
        initData();
        return root;
    }

    private void initView(View view) {
        textureView = view.findViewById(R.id.camera_textureview);
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cameraUtil != null) {
                    cameraUtil.focusOnTouch(event);
                }
                return true;
            }
        });
        smartPaperMeasureContainerLayout = view.findViewById(R.id.paper_scan_content_view);
        ovulationCameraTips = view.findViewById(R.id.ovulationCameraTips);
        flashTv = view.findViewById(R.id.paper_flash_tv);
        modeSwitchTv = view.findViewById(R.id.paper_mode_switch);
        shutterBtn = view.findViewById(R.id.shutterBtn);
        switchCardMode = view.findViewById(R.id.switch_card_paper);
    }

    private void initData() {
        ovulationCameraTips.setText(Html.fromHtml(getContext().getString(R.string.ovulation_camera_tips)));
        flashTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraUtil == null) {
                    return;
                }
                if (cameraUtil.isOpenFlashLight()) {
                    flashTv.setText(getText(R.string.paper_open_flashlight));
                    flashTv.setTextColor(getResources().getColor(R.color.white));
                    flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_close, 0, 0);
                    cameraUtil.closeFlashLight();
                } else {
                    flashTv.setText(getText(R.string.paper_close_flashlight));
                    flashTv.setTextColor(0xFFF4F400);
                    flashTv.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.icon_lamp_open, 0, 0);
                    cameraUtil.openFlashLight();
                }
            }
        });
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Manually take photos
                cameraUtil.takePicture(new CameraUtil.ICameraTakeEvent() {
                    @Override
                    public void takeBitmap(Bitmap originSquareBitmap) {
                        clipPaperDialog(originSquareBitmap);
                    }
                });
            }
        });

        modeSwitchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 1000);
                if (scanMode == AUTOSMART) {
                    scanMode = MANUALSMART;
                } else {
                    scanMode = AUTOSMART;
                }
                switchMode();
            }
        });
        switchCardMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 1000);
                isCardMode = !isCardMode;
                switchCardMode.setText(isCardMode ? getText(R.string.bar_strip) : getText(R.string.card_strip));
                if (smartPaperMeasureContainerLayout != null) {
                    if (isCardMode) {
                        smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                    } else {
                        smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
                    }
                }
            }
        });
    }

    private void switchMode() {
        if (scanMode == AUTOSMART) {
            switchCardMode.setVisibility(View.VISIBLE);
            switchCardMode.setText(isCardMode ? getText(R.string.bar_strip) : getText(R.string.card_strip));
            shutterBtn.setVisibility(View.GONE);
            modeSwitchTv.setText(getText(R.string.paper_manau_clip));
            if (smartPaperMeasureContainerLayout != null) {
                if (isCardMode) {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                } else {
                    smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
                }
            }
            //rescan
            restartScan(false);
        } else {
            switchCardMode.setVisibility(View.GONE);
            shutterBtn.setVisibility(View.VISIBLE);
            modeSwitchTv.setText(getText(R.string.intelligent_matting));
            if (smartPaperMeasureContainerLayout != null) {
                smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
            }
            paperAnalysiserClient.stop();
        }
    }

    private void restartScan(boolean restartOpenCamera) {
        scanMode = AUTOSMART;
        switchCardMode.setVisibility(View.VISIBLE);
        if (smartPaperMeasureContainerLayout != null) {
            if (isCardMode) {
                smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
            }
            paperAnalysiserClient.setObtainPreviewFrame(false);
            paperAnalysiserClient.reset();
        }
        if (restartOpenCamera) {
            handleCamera();
        }
    }

    private void handleCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cameraUtil == null) {
                    cameraUtil = new CameraUtil();
                }
                cameraUtil.initCamera(getActivity(), textureView, mPreviewCallback);
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        handleCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (cameraUtil != null) {
            cameraUtil.stopCamera();
            cameraUtil = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("paper sdk closeSession");
        paperAnalysiserClient.closeSession();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("requestCode：" + requestCode + " resultCode:" + resultCode);
        if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                int paperValue = data.getIntExtra("paperValue", 0);
                //Manually modify lhValue
                paperAnalysiserClient.updatePaperValue(paperValue);
            }
            //Restart scanning
            restartScan(false);
        } else if (requestCode == 1003) {
            //Restart scanning
            restartScan(false);
        }
    }

    private void clipPaperDialog(Bitmap fileBitmap) {
        final ManualSmartPaperMeasureLayout.Data data =
                smartPaperMeasureContainerLayout.getManualSmartPaperMeasuereData();
        Point upLeftPoint = new Point(data.innerLeft, data.innerTop);
        Point rightBottomPoint = new Point(data.innerRight, data.innerBottom);
        startTime = System.currentTimeMillis();
        IBaseAnalysisEvent iBaseAnalysisEvent = new IBaseAnalysisEvent() {
            @Override
            public void showProgressDialog() {
                LogUtils.d("Show Loading Dialog");
                VideoFragment.this.showProgressDialog(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        paperAnalysiserClient.stopShowProgressDialog();
                    }
                });
            }

            @Override
            public void dismissProgressDialog() {
                LogUtils.d("Show Loading Dialog");
                VideoFragment.this.dismissProgressDialog();
            }

            @Override
            public void cancel() {
                LogUtils.d("Cancel test strip result confirmation");
                ToastUtils.show(getContext(), AiCode.getMessage(AiCode.CODE_201));
            }

            @Override
            public void save(PaperResult paperResult) {
                LogUtils.d("Save test paper analysis results：\n" + paperResult.toString());
                if (paperResult.getErrNo() != 0) {
                    ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                }
                //显示试纸结果
                FileUtil.saveBitmap(paperResult.getPaperBitmap(), paperResult.getPaperId());
                paperResult.setPaperBitmap(null);
                paperResult.setNoMarginBitmap(null);
                Intent intent = new Intent(getContext(), PaperDetailActivity.class);
                intent.putExtra("bean", paperResult);
                startActivityForResult(intent, 1002);
            }

            @Override
            public void saasAnalysisError(String errorResult, int code) {
                LogUtils.d("Test strip analysis error code：" + code + " errorResult:" + errorResult);
                ToastUtils.show(getContext(), AiCode.getMessage(code));
            }

            @Override
            public void paperResultDialogShow(PaperResultDialog paperResultDialog) {

            }
        };
        paperAnalysiserClient.analysisClipBitmapFromCamera(fileBitmap, upLeftPoint, rightBottomPoint, iBaseAnalysisEvent);
    }


    /**
     * Real-time preview callback
     */
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {
            if (paperAnalysiserClient.isObtainPreviewFrame()) {
                return;
            }
            startTime = System.currentTimeMillis();
            //The top half of the video is a square image
            Bitmap originSquareBitmap;
            if (textureView.getBitmap() != null) {
                originSquareBitmap = ImageUtil.topCropBitmap(textureView.getBitmap());
            } else {
                originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, TensorFlowTools.getDegree(getActivity()));
            }
            if (isCardMode) {
                CardAutoSmartPaperMeasureLayout.Data imageData = smartPaperMeasureContainerLayout.getCardAutoSmartPaperMeasureData();
                if (imageData != null) {
                    Bitmap smallSquareBitmap = ImageUtil.cropBitmap(originSquareBitmap, imageData.innerLeft, imageData.innerTop, imageData.innerWidth, imageData.innerHeight);
                    paperAnalysiserClient.analysisCameraCardData(originSquareBitmap, smallSquareBitmap, imageData.innerLeft, imageData.innerTop);
                }
            } else {
                paperAnalysiserClient.analysisCameraData(originSquareBitmap);
            }
        }
    };
    private Dialog progressDialog;

    public void showProgressDialog(View.OnClickListener onClickListener) {
        progressDialog = ProgressDialog.createLoadingDialog(getContext(), onClickListener);
        if (progressDialog != null && !progressDialog.isShowing() && !getActivity().isFinishing()) {
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


    private ICameraAnalysisEvent iCameraAnalysisEvent = new ICameraAnalysisEvent() {
        @Override
        public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
            if (scanMode == MANUALSMART) {
                return false;
            }
            LogUtils.d("Test strips are automatically cut out successfully");
            try {
                AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
                if (volume != 0) {
                    MediaPlayer shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                    if (shootMP != null) {
                        shootMP.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isCardMode) {
                smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure(originSquareBitmap);
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, originSquareBitmap);
            }
            return false;
        }

        @Override
        public void analysisResult(PaperCoordinatesData paperCoordinatesData) {
            if (scanMode == MANUALSMART) {
                return;
            }
            LogUtils.d("Automatic drawing line on test paper");
            LogUtils.d("Time " + (System.currentTimeMillis() - startTime));
            if (!isCardMode) {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
            }
        }

        @Override
        public void analysisEnd(Bitmap originSquareBitmap, int code, String errorResult) {
            LogUtils.d("Test paper cutout end code：" + code + " errorResult:" + errorResult);
            ToastUtils.show(getContext(), getString(R.string.cutout_timeout));
            scanMode = MANUALSMART;
            shutterBtn.setVisibility(View.VISIBLE);
            modeSwitchTv.setText(getText(R.string.intelligent_matting));
            switchCardMode.setVisibility(View.GONE);
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
        }

        @Override
        public void analysisCancel(Bitmap originSquareBitmap, int code, String errorResult) {
            LogUtils.d("Test paper cutout cancel code：" + code + " errorResult:" + errorResult);
            ToastUtils.show(getContext(), getString(R.string.cutout_cancel));
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
        }

        @Override
        public void showProgressDialog() {
            LogUtils.d("Show Loading Dialog");
            VideoFragment.this.showProgressDialog(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    paperAnalysiserClient.stopShowProgressDialog();
                }
            });
        }

        @Override
        public void dismissProgressDialog() {
            LogUtils.d("Hide Loading Dialog");
            VideoFragment.this.dismissProgressDialog();
        }

        @Override
        public void cancel() {
            LogUtils.d("Cancel test strip result confirmation");
            if (isCardMode) {
                smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);
            }
            //Restart scanning
            restartScan(false);
        }

        @Override
        public void save(PaperResult paperResult) {
            LogUtils.d("Save test paper analysis results：\n" + paperResult.toString());
            if (paperResult.getErrNo() != 0) {
                ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
            }
            if (isCardMode) {
                smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);
            }

            //Show test strip result
            FileUtil.saveBitmap(paperResult.getPaperBitmap(), paperResult.getPaperId());
            paperResult.setPaperBitmap(null);
            paperResult.setNoMarginBitmap(null);
            Intent intent = new Intent(getContext(), PaperDetailActivity.class);
            intent.putExtra("bean", paperResult);
            startActivityForResult(intent, 1002);
        }

        @Override
        public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
            if (scanMode == MANUALSMART) {
                return;
            }
            LogUtils.d("Test paper cutout error code：" + code + " errorResult:" + errorResult);
            LogUtils.d("Time " + (System.currentTimeMillis() - startTime));
            if (paperCoordinatesData == null) {
                paperCoordinatesData = new PaperCoordinatesData();
            } else if (code == AiCode.CODE_11) {
                //The picture is blurred and refocus
                if (cameraUtil != null) {
                    cameraUtil.holdFocus();
                }

            }
            paperCoordinatesData.setCode(code);
            if (!isCardMode) {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
            }
        }

        @Override
        public void saasAnalysisError(String errorResult, int code) {
            LogUtils.d("Test paper analysis error code：" + code + " errorResult:" + errorResult);
            ToastUtils.show(getContext(), AiCode.getMessage(code));
        }

        @Override
        public void paperResultDialogShow(PaperResultDialog paperResultDialog) {

        }
    };
}