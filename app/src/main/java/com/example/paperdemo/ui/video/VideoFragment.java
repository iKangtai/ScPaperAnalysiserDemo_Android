package com.example.paperdemo.ui.video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.paperdemo.AppConstant;
import com.example.paperdemo.BuildConfig;
import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.example.paperdemo.util.AiCode;
import com.example.paperdemo.view.ActionSheetDialog;
import com.example.paperdemo.view.CardAutoSmartPaperMeasureLayout;
import com.example.paperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.paperdemo.view.ProgressDialog;
import com.example.paperdemo.view.SmartPaperMeasureContainerLayout;
import com.google.android.cameraview.CameraUtil;
import com.google.android.cameraview.CameraView;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.PaperResultDialog;
import com.ikangtai.papersdk.event.IBaseAnalysisEvent;
import com.ikangtai.papersdk.event.ICameraAnalysisEvent;
import com.ikangtai.papersdk.event.IScanBarcodeResultEvent;
import com.ikangtai.papersdk.event.InitCallback;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.PxDxUtil;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

/**
 * Test paper video stream recognition
 *
 * @author
 */
public class VideoFragment extends Fragment {
    private CameraView cameraView;
    private SmartPaperMeasureContainerLayout smartPaperMeasureContainerLayout;
    private PaperAnalysiserClient paperAnalysiserClient;
    private long startTime, endTime;
    private CameraUtil cameraUtil;
    private TextView ovulationCameraTips, flashTv, modeSwitchTv, switchCardMode;
    private ImageView shutterBtn;
    private int scanMode;
    private static final int AUTOSMART = 0;
    private static final int MANUALSMART = 1;
    private static final int SCANBARCODE = 2;
    private static final int ALBUM = 4;
    private static final int AUTOSMART_MANUALSMART = 5;
    private boolean isCardMode = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Config.setNetTimeOut(30);
        Config.setDebug(BuildConfig.DEBUG);
        //init sdk
        //识别孕橙品牌试纸
        //Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(),24)).netTimeOutRetryCount(1).showYcPaperResultDialog(false).scanYcBarcode(true).paperType(Config.PAPER_LH).paperBrand(Config.PAPER_BRAND_YC).blurLimitValue(25).minBlurLimitValue(8).build();
        //识别其它品牌试纸
        Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 24)).analysisTime(60).netTimeOutRetryCount(1).blurLimitValue(16).minBlurLimitValue(8).showYcPaperResultDialog(true).scanYcBarcode(true).paperType(Config.PAPER_LH).paperBrand(Config.PAPER_BRAND_OTHER).red(1).build();
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
        cameraView = view.findViewById(R.id.camera_cameraView);
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
                cameraUtil.takePicture();
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
                if (isCardMode) {
                    if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC) {
                        paperAnalysiserClient.getConfig().setPaperBrand(Config.PAPER_BRAND_YC_CARD);
                    } else {
                        paperAnalysiserClient.getConfig().setPaperBrand(Config.PAPER_BRAND_OTHER_CARD);
                    }
                    ovulationCameraTips.setText(getContext().getString(R.string.ovulation_camera_card_tips));
                } else {
                    if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                        paperAnalysiserClient.getConfig().setPaperBrand(Config.PAPER_BRAND_YC);
                    } else {
                        paperAnalysiserClient.getConfig().setPaperBrand(Config.PAPER_BRAND_OTHER);
                    }
                    ovulationCameraTips.setText(Html.fromHtml(getContext().getString(R.string.ovulation_camera_tips)));
                }
                switchCardMode.setText(isCardMode ? getText(R.string.bar_strip) : getText(R.string.card_strip));
                if (smartPaperMeasureContainerLayout != null) {
                    if (isCardMode) {
                        if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                            smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure();
                        } else {
                            smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                        }
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
                    if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                        smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure();
                    } else {
                        smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                    }
                } else {
                    smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
                }
            }
            //rescan

            paperAnalysiserClient.stop();
        } else {
            LogUtils.d("切换自动扫描");
            //重新扫描
            restartScan(false);
        }
    }

    private void restartScan(boolean restartOpenCamera) {
        firstSuccessFocus = false;
        scanMode = AUTOSMART;
        shutterBtn.setVisibility(View.GONE);
        switchCardMode.setVisibility(View.VISIBLE);
        if (smartPaperMeasureContainerLayout != null) {
            if (isCardMode) {
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure();
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                }
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraUtil == null) {
                    cameraUtil = new CameraUtil();
                    cameraUtil.setHoldCameraHigh(true);
                }
                cameraUtil.initCenterCamera(getActivity(), cameraView, mPreviewCallback);
                cameraUtil.startCamera();
            }
        });
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
        float scaleValue = fileBitmap.getWidth() * 1f / data.outerWidth;
        Point upLeftPoint = new Point((int) (data.innerLeft * scaleValue), (int) (data.innerTop * scaleValue));
        Point rightBottomPoint = new Point((int) (data.innerRight * scaleValue), (int) (data.innerBottom * scaleValue));
        Integer boxXEnd = null;
        Integer boxXStart = null;
        if (isCardMode) {
            boxXStart = data.outerWidth / 2 - 128;
            boxXEnd = data.outerWidth / 2 + 128;
        }
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
                dismissProgressDialog();
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
        if (isCardMode) {
            paperAnalysiserClient.analysisClipCardBitmapFromCamera(fileBitmap, upLeftPoint, rightBottomPoint, boxXStart, boxXEnd, iBaseAnalysisEvent);
        } else {
            paperAnalysiserClient.analysisClipBitmapFromCamera(fileBitmap, upLeftPoint, rightBottomPoint, iBaseAnalysisEvent);
        }
    }

    private Bitmap highOriginSquareBitmap;
    /**
     * Real-time preview callback
     */
    private CameraView.Callback mPreviewCallback = new CameraView.Callback() {
        @Override
        public void onPictureTaken(CameraView cameraView, Bitmap bitmap) {
            super.onPictureTaken(cameraView, bitmap);
            clipPaperDialog(bitmap);
        }

        @Override
        public void onPreviewFrame(CameraView cameraView, Image image) {
            super.onPreviewFrame(cameraView, image);
            if (paperAnalysiserClient.isObtainPreviewFrame()) {
                return;
            }
            onPreviewFrame(cameraView, CameraUtil.YUV_420_888toNV21(image));
        }

        public void onPreviewFrame(CameraView cameraView, byte[] data) {
            super.onPreviewFrame(cameraView, data);
            if (paperAnalysiserClient.isObtainPreviewFrame()) {
                return;
            }
            startTime = System.currentTimeMillis();
            float scaleValue = 1;
            if (cameraView.getWidth() != cameraView.getPreviewHeight()) {
                int sourceWidth = cameraView.getPreviewHeight();
                scaleValue = ((float) cameraView.getWidth()) / sourceWidth;
            }
            //The top half of the video is a square image
            Bitmap originSquareBitmap;
            if (cameraView.getBitmap() != null) {
                originSquareBitmap = ImageUtil.cropBitmap(cameraView.getBitmap(), cameraUtil != null ? cameraUtil.getLightFix() : 0);
            } else {
                originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, cameraView.getPreviewWidth(), cameraView.getPreviewHeight(), CameraUtil.getDegree(getActivity()), cameraUtil != null ? (int) (cameraUtil.getLightFix() / scaleValue) : 0);
            }
            if (originSquareBitmap != null && scaleValue != 1) {
                int sourceWidth = originSquareBitmap.getWidth();
                int sourceHeight = originSquareBitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleValue, scaleValue);
                highOriginSquareBitmap = originSquareBitmap;
                originSquareBitmap = Bitmap.createBitmap(highOriginSquareBitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
            } else {
                highOriginSquareBitmap = null;
            }
            if (scanMode == SCANBARCODE) {
                final ManualSmartPaperMeasureLayout.Data manualSmartPaperMeasuereData =
                        smartPaperMeasureContainerLayout.getManualSmartPaperMeasuereData();
                Point upLeftPoint = new Point(manualSmartPaperMeasuereData.innerLeft, manualSmartPaperMeasuereData.innerTop);
                Point rightBottomPoint = new Point(manualSmartPaperMeasuereData.innerRight, manualSmartPaperMeasuereData.innerBottom);
                if (isCardMode) {
                    paperAnalysiserClient.scanBarcodeClipCardBitmapFromCamera(originSquareBitmap, upLeftPoint, rightBottomPoint, iScanBarcodeResultEvent);
                } else {
                    paperAnalysiserClient.scanBarcodeClipBitmapFromCamera(originSquareBitmap, upLeftPoint, rightBottomPoint, iScanBarcodeResultEvent);
                }
            } else {
                if (isCardMode) {
                    if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                        paperAnalysiserClient.analysisCameraShecareCardData(originSquareBitmap);
                    } else {
                        CardAutoSmartPaperMeasureLayout.Data imageData = smartPaperMeasureContainerLayout.getCardAutoSmartPaperMeasureData();
                        if (imageData != null) {
                            Bitmap smallSquareBitmap = ImageUtil.cropBitmap(originSquareBitmap, imageData.innerLeft, imageData.innerTop, imageData.innerWidth, imageData.innerHeight);
                            paperAnalysiserClient.analysisCameraCardData(originSquareBitmap, smallSquareBitmap, imageData.innerLeft, imageData.innerTop);
                        }
                    }
                } else {
                    paperAnalysiserClient.analysisCameraData(originSquareBitmap);
                }
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

    private IScanBarcodeResultEvent iScanBarcodeResultEvent = new IScanBarcodeResultEvent() {
        @Override
        public void scanResult(String result) {
            scanMode = MANUALSMART;
            shutterBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void scanError(String errorResult, int code) {
            LogUtils.d("scanError code：" + code + " errorResult:" + errorResult);
            if (code == AiCode.CODE_17) {
                new ActionSheetDialog(getContext())
                        .builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setTitle(errorResult)
                        .addSheetItem(getString(R.string.intelligent_matting), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        scanMode = AUTOSMART;
                                        switchMode();
                                    }
                                })
                        .addSheetItem(getString(R.string.paper_reset), ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        paperAnalysiserClient.setObtainPreviewFrame(false);
                                        paperAnalysiserClient.reset();
                                    }
                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        }).show();
            }
        }
    };
    private boolean firstSuccessFocus;
    private ICameraAnalysisEvent iCameraAnalysisEvent = new ICameraAnalysisEvent() {
        @Override
        public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
            if (scanMode != AUTOSMART) {
                return false;
            }
            LogUtils.d("Test strips are automatically cut out successfully");
            if (isCardMode) {
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure(paperCoordinatesData, originSquareBitmap);
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure(originSquareBitmap);
                }
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, originSquareBitmap);
            }
            if (!isCardMode && highOriginSquareBitmap != null) {
                scanMode = AUTOSMART_MANUALSMART;
                paperAnalysiserClient.analysisBitmapResultCamera(highOriginSquareBitmap, iCameraAnalysisEvent);
                return true;
            } else {
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
            }
            return false;
        }

        @Override
        public void analysisResult(PaperCoordinatesData paperCoordinatesData) {
            if (scanMode != AUTOSMART) {
                return;
            }
            LogUtils.d("Automatic drawing line on test paper");
            LogUtils.d("Time " + (System.currentTimeMillis() - startTime));
            LogUtils.d("耗时: " + (System.currentTimeMillis() - startTime));
            if (isCardMode) {
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure(paperCoordinatesData, null);
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure(paperCoordinatesData, null);
                }
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
            }
            if (!firstSuccessFocus) {
                firstSuccessFocus = true;
                if (paperCoordinatesData != null && paperCoordinatesData.getPoint1() != null && paperCoordinatesData.getPoint3() != null) {
                    Point point = new Point((paperCoordinatesData.getPoint1().x + paperCoordinatesData.getPoint3().x) / 2, (paperCoordinatesData.getPoint1().y + paperCoordinatesData.getPoint3().y) / 2);
                    LogUtils.d("首次抠图成功自动对焦 " + point);
                    if (cameraUtil != null) {
                        cameraUtil.focusOnPoint(point);
                    }
                }
            }
        }

        @Override
        public void analysisEnd(Bitmap originSquareBitmap, int code, String errorResult) {
            LogUtils.d("Test paper cutout end code：" + code + " errorResult:" + errorResult);
            ToastUtils.show(getContext(), getString(R.string.cutout_timeout));
            modeSwitchTv.setText(getText(R.string.intelligent_matting));
            switchCardMode.setVisibility(View.GONE);
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
            if (scanMode == AUTOSMART) {
                scanMode = SCANBARCODE;
                paperAnalysiserClient.setObtainPreviewFrame(false);
                paperAnalysiserClient.reset();
            }
            if (cameraView != null) {
                cameraView.freshFocus();
            }
        }

        @Override
        public void analysisCancel(Bitmap originSquareBitmap, int code, String errorResult) {
            LogUtils.d("Test paper cutout cancel code：" + code + " errorResult:" + errorResult);
            ToastUtils.show(getContext(), getString(R.string.cutout_cancel));
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
            if (scanMode == AUTOSMART) {
                scanMode = SCANBARCODE;
                paperAnalysiserClient.setObtainPreviewFrame(false);
                paperAnalysiserClient.reset();
            }
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
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure();
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                }
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);
            }
            //Restart scanning
            restartScan(false);
        }

        @Override
        public void save(PaperResult paperResult) {
            dismissProgressDialog();
            LogUtils.d("Save test paper analysis results：\n" + paperResult.toString());
            if (paperResult.getErrNo() != 0) {
                ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
            }
            if (isCardMode) {
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure();
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure();
                }
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
            if (scanMode != AUTOSMART && scanMode != AUTOSMART_MANUALSMART) {
                return;
            }
            LogUtils.d("Test paper cutout error code：" + code + " errorResult:" + errorResult);
            LogUtils.d("Time " + (System.currentTimeMillis() - startTime));
            if (paperCoordinatesData == null) {
                paperCoordinatesData = new PaperCoordinatesData();
            } else if (code == AiCode.CODE_11) {
                //The picture is blurred and refocus
                if (paperCoordinatesData != null && paperCoordinatesData.getPoint1() != null && paperCoordinatesData.getPoint3() != null) {
                    Point point = new Point((paperCoordinatesData.getPoint1().x + paperCoordinatesData.getPoint3().x) / 2, (paperCoordinatesData.getPoint1().y + paperCoordinatesData.getPoint3().y) / 2);
                    LogUtils.d("模糊自动对焦 " + point);
                    if (cameraUtil != null) {
                        cameraUtil.focusOnPoint(point);
                    }
                } else {
                    if (cameraUtil != null) {
                        cameraUtil.holdFocus();
                    }
                }
            }
            paperCoordinatesData.setCode(code);
            if (isCardMode) {
                if (paperAnalysiserClient.getConfig().getPaperBrand() == Config.PAPER_BRAND_YC_CARD) {
                    smartPaperMeasureContainerLayout.showShecareCardAutoSmartPaperMeasure(paperCoordinatesData, null);
                } else {
                    smartPaperMeasureContainerLayout.showCardAutoSmartPaperMeasure(paperCoordinatesData, null);
                }
            } else {
                smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
            }
            if (scanMode == AUTOSMART_MANUALSMART) {
                scanMode = AUTOSMART;
                paperAnalysiserClient.reset();
                if (paperAnalysiserClient.isObtainPreviewFrame()) {
                    paperAnalysiserClient.setObtainPreviewFrame(false);
                }
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