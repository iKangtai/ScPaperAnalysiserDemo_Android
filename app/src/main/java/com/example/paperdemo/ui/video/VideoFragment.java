package com.example.paperdemo.ui.video;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paperdemo.PaperClipActivity;
import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.example.paperdemo.util.CameraUtil;
import com.example.paperdemo.view.ActionSheetDialog;
import com.example.paperdemo.view.CameraSurfaceView;
import com.example.paperdemo.view.ManualSmartPaperMeasureLayout;
import com.example.paperdemo.view.SmartPaperMeasureContainerLayout;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.event.IBaseAnalysisEvent;
import com.ikangtai.papersdk.event.IBitmapAnalysisEvent;
import com.ikangtai.papersdk.event.ICameraAnalysisEvent;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {
    private CameraSurfaceView surfaceView;
    private SmartPaperMeasureContainerLayout smartPaperMeasureContainerLayout;
    private PaperAnalysiserClient paperAnalysiserClient;
    public static String appId = "100017";
    public static String appSecret = "b1eed2fb4686e1b1049a9486d49ba015af00d5a0";
    private long startTime, endTime;
    private CameraUtil cameraUtil;
    private TextView ovulationCameraTips, flashTv, modeSwitchTv;
    private ImageView shutterBtn, openAlbumTv;
    private int scanMode;
    private static final int AUTOSMART = 0;
    private static final int MANUALSMART = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Config.setTestServer(true);
        Config.setNetTimeOut(30);
        //初始化sdk
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
        Config config = new Config.Builder().pixelOfdExtended(true).margin(5).build();
        paperAnalysiserClient.init(config);
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
        surfaceView = view.findViewById(R.id.camera_surfaceview);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cameraUtil.holdFocus();
                return true;
            }
        });
        smartPaperMeasureContainerLayout = view.findViewById(R.id.paper_scan_content_view);
        ovulationCameraTips = view.findViewById(R.id.ovulationCameraTips);
        flashTv = view.findViewById(R.id.paper_flash_tv);
        openAlbumTv = view.findViewById(R.id.paper_open_album);
        modeSwitchTv = view.findViewById(R.id.paper_mode_switch);
        shutterBtn = view.findViewById(R.id.shutterBtn);
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
                //手动拍照
                cameraUtil.takePicture(new CameraUtil.ICameraTakeEvent() {
                    @Override
                    public void takeBitmap(Bitmap originSquareBitmap) {
                        clipPaperDialog(originSquareBitmap);
                    }
                });
            }
        });

        openAlbumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanMode == AUTOSMART) {
                    paperAnalysiserClient.stop();
                }
                checkPerm();
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
                    shutterBtn.setVisibility(View.VISIBLE);
                    modeSwitchTv.setText(getText(R.string.intelligent_matting));
                    scanMode = MANUALSMART;
                    if (smartPaperMeasureContainerLayout != null) {
                        smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
                    }
                    paperAnalysiserClient.stop();
                } else {
                    shutterBtn.setVisibility(View.GONE);
                    modeSwitchTv.setText(getText(R.string.paper_manau_clip));
                    scanMode = AUTOSMART;
                    if (smartPaperMeasureContainerLayout != null) {
                        smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
                    }
                    //重新扫描
                    restartScan(false);
                }
            }
        });
    }

    private void restartScan(boolean restartOpenCamera) {
        scanMode = AUTOSMART;
        if (smartPaperMeasureContainerLayout != null) {
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure();
            paperAnalysiserClient.setObtainPreviewFrame(false);
            paperAnalysiserClient.reset();
        }
        if (restartOpenCamera) {
            handleCamera();
        }
    }

    private void checkPerm() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), R.string.write_permission, Toast.LENGTH_SHORT).show();
        } else {//权限被授予
            choosePhoto();
            //直接操作
        }
    }

    /**
     * 打开选择图片的界面
     */
    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    private void handleCamera() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cameraUtil == null) {
                    cameraUtil = new CameraUtil();
                }
                cameraUtil.initCamera(getActivity(), surfaceView, mPreviewCallback);
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
        paperAnalysiserClient.closeSession();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // 得到图片的全路径
                //String uriStr = ImageUtil.getPathFromUri(getContext(), data.getData());
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    //File file = ImageUtil.getFileFromUril(uriStr);
                    Bitmap fileBitmap = null;
                    try {
                        fileBitmap = ImageUtil.getUriToBitmap(getContext(), fileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (fileBitmap == null) {
                        ToastUtils.show(getContext(), "解析试纸图片出现异常");
                        return;
                    }
                    showPaperDialog(fileBitmap, fileUri);
                } else {
                    ToastUtils.show(getContext(), "权限不足");
                }
            }
        } else if (requestCode == 1002) {
            if (resultCode == Activity.RESULT_OK) {
                int paperValue = data.getIntExtra("paperValue", 0);
                //手动修改lhValue
                paperAnalysiserClient.updatePaperValue(paperValue);
            }
            //重新开始扫描
            restartScan(false);
        } else if (requestCode == 1003) {
            //重新开始扫描
            restartScan(false);
        }
    }

    private void clipPaperDialog(Bitmap fileBitmap) {
        final ManualSmartPaperMeasureLayout.Data data =
                smartPaperMeasureContainerLayout.getManualSmartPaperMeasuereData();
        Point upLeftPoint = new Point(data.innerLeft, data.innerTop);
        Point rightBottomPoint = new Point(data.innerRight, data.innerBottom);
        startTime = System.currentTimeMillis();
        paperAnalysiserClient.analysisClipBitmap(fileBitmap, upLeftPoint, rightBottomPoint, new IBaseAnalysisEvent() {
            @Override
            public void showProgressDialog() {

            }

            @Override
            public void dismissProgressDialog() {

            }

            @Override
            public void cancel() {
                ToastUtils.show(getContext(), AiCode.getMessage(AiCode.CODE_201));
            }

            @Override
            public void save(PaperResult paperResult) {
                if (paperResult.getErrNo() != 0) {
                    ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                }
                //显示试纸结果
                paperResult.setPaperBitmap(null);
                paperResult.setNoMarginBitmap(null);
                Intent intent = new Intent(getContext(), PaperDetailActivity.class);
                intent.putExtra("bean", paperResult);
                startActivityForResult(intent, 1002);
            }

            @Override
            public void saasAnalysisError(String errorResult, int code) {
                ToastUtils.show(getContext(), AiCode.getMessage(code));
            }
        });


    }

    private void showPaperDialog(Bitmap fileBitmap, final Uri fileUri) {

        startTime = System.currentTimeMillis();
        paperAnalysiserClient.analysisBitmap(fileBitmap, new IBitmapAnalysisEvent() {
            @Override
            public void showProgressDialog() {
            }

            @Override
            public void dismissProgressDialog() {
            }

            @Override
            public void cancel() {
                ToastUtils.show(getContext(), AiCode.getMessage(AiCode.CODE_201));
            }

            @Override
            public void save(PaperResult paperResult) {
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
            public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                endTime = System.currentTimeMillis();
                return false;
            }

            @Override
            public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                ToastUtils.show(getContext(), AiCode.getMessage(code));
                if (fileUri != null) {
                    final String uriStr = ImageUtil.getPathFromUri(getContext(), fileUri);
                    if (TextUtils.isEmpty(uriStr)) {
                        return;
                    }
                    new ActionSheetDialog(getContext())
                            .builder()
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .setTitle(errorResult)
                            .addSheetItem(getString(R.string.paper_clip_pic), ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            Intent intent = new Intent(getContext(), PaperClipActivity.class);
                                            intent.putExtra("paperUri", uriStr);
                                            startActivityForResult(intent, 1003);
                                        }
                                    })
                            .addSheetItem(getString(R.string.paper_reset), ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            checkPerm();
                                        }
                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    }).show();
                }
            }

            @Override
            public void saasAnalysisError(String errorResult, int code) {
                ToastUtils.show(getContext(), AiCode.getMessage(code));
            }
        });


    }

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
            paperAnalysiserClient.analysisCameraData(originSquareBitmap);
        }
    };
    private ICameraAnalysisEvent iCameraAnalysisEvent = new ICameraAnalysisEvent() {
        @Override
        public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
            if (scanMode == MANUALSMART) {
                return true;
            }
            ToastUtils.show(getContext(), "抠图最终结果");
            AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            if (volume != 0) {
                MediaPlayer shootMP = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                shootMP.start();
            }
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, originSquareBitmap);
            return true;
        }

        @Override
        public void analysisResult(PaperCoordinatesData paperCoordinatesData) {
            if (scanMode == MANUALSMART) {
                return;
            }
            Log.d("xyl", "抠图耗时 " + (System.currentTimeMillis() - startTime));
            ToastUtils.show(getContext(), "抠图中间结果");
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(paperCoordinatesData, null);
        }

        @Override
        public void analysisEnd(Bitmap originSquareBitmap, int code, String errorResult) {
            ToastUtils.show(getContext(), "抠图超时");
            scanMode = MANUALSMART;
            shutterBtn.setVisibility(View.VISIBLE);
            modeSwitchTv.setText(getText(R.string.intelligent_matting));
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
        }

        @Override
        public void analysisCancel(Bitmap originSquareBitmap, int code, String errorResult) {
            ToastUtils.show(getContext(), "切换抠图模式，取消视频流分析");
            smartPaperMeasureContainerLayout.showManualSmartPaperMeasure();
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
            restartScan(false);
        }

        @Override
        public void save(PaperResult paperResult) {
            smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(null, null);

            //显示试纸结果
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
}