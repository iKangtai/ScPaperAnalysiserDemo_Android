package com.example.paperdemo.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paperdemo.AppConstant;
import com.example.paperdemo.PaperClipActivity;
import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.example.paperdemo.view.ActionSheetDialog;
import com.example.paperdemo.view.ProgressDialog;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.UiOption;
import com.ikangtai.papersdk.event.IPaperTypeAnalysisResultEvent;
import com.ikangtai.papersdk.event.InitCallback;
import com.ikangtai.papersdk.event.SampleBitmapAnalysisEventAdapter;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.LogUtils;
import com.ikangtai.papersdk.util.PxDxUtil;
import com.ikangtai.papersdk.util.SupportDeviceUtil;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private PaperAnalysiserClient paperAnalysiserClient;
    private ImageView paperImageView, paperNoMarginImageView;
    private TextView detailTv;
    private long startTime, endTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /**
         * 使用测试网络
         */
        Config.setTestServer(true);
        /**
         * 网络超时时间
         */
        Config.setNetTimeOut(30);

        if (!SupportDeviceUtil.isSupport(getContext(), AppConstant.appId, AppConstant.appSecret)) {
            new AlertDialog.Builder(getContext()).setMessage("当前设备性能太差,SDK自动识别较慢").show();
        }

        //定制试纸Ui显示
        /**
         * 标题
         */
        String titleText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_title);
        /**
         * 标题颜色
         */
        int titleTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * 标尺线
         */
        int tagLineImageResId = com.ikangtai.papersdk.R.drawable.paper_line;
        /**
         * t滑块图标
         */
        int tLineResId = com.ikangtai.papersdk.R.drawable.test_paper_t_line;
        /**
         * c滑块图标
         */
        int cLineResId = com.ikangtai.papersdk.R.drawable.test_paper_c_line;
        /**
         * 水平翻转文字
         */
        String flipText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_flip);
        /**
         * 水平翻转文字颜色
         */
        int flipTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
        /**
         * 提示文字
         */
        String hintText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_hit);
        /**
         * 提示文字颜色
         */
        int hintTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * 返回图片
         */
        int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
        /**
         * 确认图片
         */
        int confirmResId = com.ikangtai.papersdk.R.drawable.test_paper_confirm;
        /**
         * tc线默认值宽度
         */
        float tcLineWidth = getContext().getResources().getDimension(com.ikangtai.papersdk.R.dimen.dp_2);
        /**
         * 返回按钮背景
         */
        int backButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
        /**
         * 确认按钮背景
         */
        int confirmButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
        /**
         * 返回按钮文字
         */
        String backButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_back);
        /**
         * 确认按钮文字
         */
        String confirmButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_confirm);
        /**
         * 返回按钮文字颜色
         */
        int backButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * 确认按钮文字颜色
         */
        int confirmButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * 显示底部按钮
         */
        boolean visibleBottomButton = false;
        /**
         * 样张
         */
        int sampleResId = com.ikangtai.papersdk.R.drawable.confirm_sample_pic_lh;
        int feedbackTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
        UiOption uiOption = new UiOption.Builder(getContext())
                .titleText(titleText)
                .tagLineImageResId(tagLineImageResId)
                .titleTextColor(titleTextColor)
                .tLineResId(tLineResId)
                .cLineResId(cLineResId)
                .flipText(flipText)
                .flipTextColor(flipTextColor)
                .hintText(hintText)
                .hintTextColor(hintTextColor)
                .backResId(backResId)
                .confirmResId(confirmResId)
                .tcLineWidth(tcLineWidth)
                .backButtonBgResId(backButtonBgResId)
                .backButtonText(backButtonText)
                .confirmButtonBgResId(confirmButtonBgResId)
                .confirmButtonText(confirmButtonText)
                .backButtonTextColor(backButtonTextColor)
                .confirmButtonTextColor(confirmButtonTextColor)
                .visibleBottomButton(visibleBottomButton)
                .sampleResUrl("https:/yunchengfile.oss-cn-beijing.aliyuncs.com/app/confirm_sample_pic_lh.png")
                .feedbackTextColor(feedbackTextColor)
                .language(Locale.ENGLISH.getLanguage())
                .build();
        /**
         * 自定义log文件有两种方式,设置一次即可
         * 1.new Config.Builder().logWriter(logWriter).
         * 2.new Config.Builder().logFilePath(logFilePath).
         */
        String logFilePath = new File(FileUtil.createRootPath(getContext()), "log_test.txt").getAbsolutePath();
        BufferedWriter logWriter = null;
        try {
            logWriter = new BufferedWriter(new FileWriter(logFilePath, true), 2048);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //试纸识别sdk相关配置
        Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 20)).uiOption(uiOption).logWriter(logWriter).netTimeOutRetryCount(1).build();
        //初始化sdk
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), AppConstant.appId, AppConstant.appSecret, "xyl1@qq.com", config, new InitCallback() {
            @Override
            public void onSuccess() {
                ToastUtils.show(getContext(), "初始化成功");
            }

            @Override
            public void onFailure(int code, String message) {
                ToastUtils.show(getContext(), message);
            }
        });

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        paperImageView = root.findViewById(R.id.paper_image_home);
        paperNoMarginImageView = root.findViewById(R.id.paper_image_nomargin_home);
        detailTv = root.findViewById(R.id.paper_text_home);
        root.findViewById(R.id.image_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
        root.findViewById(R.id.image_type_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1003);
            }
        });
        return root;
    }

    /**
     * 打开选择图片的界面
     */
    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    private void showPaperDialog(final Uri fileUri) {
        if (fileUri != null) {
            //File file = ImageUtil.getFileFromUril(fileUri.toString());
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
            startTime = System.currentTimeMillis();
            paperAnalysiserClient.analysisBitmap(fileBitmap, new SampleBitmapAnalysisEventAdapter() {
                @Override
                public void save(PaperResult paperResult) {
                    super.save(paperResult);
                    LogUtils.d("保存试纸分析结果：\n" + paperResult.toString());
                    //试纸结果确认框确认 显示试纸结果
                    if (paperResult.getErrNo() != 0) {
                        ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                    }
                    detailTv.setText("耗时 " + (endTime - startTime) + "\n" + paperResult.toString());
                    //试纸抠图结果
                    paperImageView.setImageBitmap(paperResult.getPaperBitmap());
                    //开启外扩开关后 会返回不带边距bitmap
                    paperNoMarginImageView.setImageBitmap(paperResult.getNoMarginBitmap());
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
                    super.analysisSuccess(paperCoordinatesData, originSquareBitmap, clipPaperBitmap);
                    LogUtils.d("试纸自动抠图成功");
                    //试纸抠图成功结果
                    endTime = System.currentTimeMillis();
                    return false;
                }

                @Override
                public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                    super.analysisError(paperCoordinatesData, errorResult, code);
                    LogUtils.d("试纸自动抠图出错 code：" + code + " errorResult:" + errorResult);
                    //试纸抠图失败结果
                    ToastUtils.show(getContext(), AiCode.getMessage(code));
                    detailTv.setText("耗时 " + (System.currentTimeMillis() - startTime) + "\n" + "模糊值 " + (paperCoordinatesData != null ? paperCoordinatesData.getBlurValue() : 0) + "\n" + "错误码 " + code + "\n" + "message " + errorResult);
                    paperNoMarginImageView.setImageBitmap(null);
                    if (paperCoordinatesData != null && paperCoordinatesData.getImageL() != null) {
                        paperImageView.setImageBitmap(TensorFlowTools.getBitmapByMat(paperCoordinatesData.getImageL()));
                    } else {
                        paperImageView.setImageBitmap(null);
                    }

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
                                                choosePhoto();
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
                    super.saasAnalysisError(errorResult, code);
                    LogUtils.d("试纸分析出错 code：" + code + " errorResult:" + errorResult);
                    //试纸saas分析失败
                    ToastUtils.show(getContext(), AiCode.getMessage(code));
                }
            });
        } else {
            ToastUtils.show(getContext(), "权限不足");
        }
    }

    Bitmap paperBitmap = null;

    private void showPaperType(final Uri fileUri) {
        if (fileUri != null) {
            //File file = ImageUtil.getFileFromUril(fileUri.toString());
            try {
                paperBitmap = ImageUtil.getUriToBitmap(getContext(), fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startTime = System.currentTimeMillis();
            paperAnalysiserClient.paperTypeAnalysis(paperBitmap, new IPaperTypeAnalysisResultEvent() {
                @Override
                public void onSuccessPaperTypeAnalysis(int paperType) {
                    LogUtils.d("试纸识类型：\n" + paperType);
                    detailTv.setText("耗时 " + (System.currentTimeMillis() - startTime) + "\n试纸识类型 " + paperType);
                    paperImageView.setImageBitmap(paperBitmap);
                    paperNoMarginImageView.setImageBitmap(null);

                }

                @Override
                public void onFailurePaperTypeAnalysis(int errorCode, String message) {
                    LogUtils.d("试纸类型分析出错 code：" + errorCode + " errorResult:" + message);
                    ToastUtils.show(getContext(), AiCode.getMessage(errorCode));
                    paperImageView.setImageBitmap(null);
                    paperNoMarginImageView.setImageBitmap(null);
                    detailTv.setText("耗时 " + (System.currentTimeMillis() - startTime) + "\n" + message);
                }
            });
        } else {
            ToastUtils.show(getContext(), "权限不足");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("页面返回结果 requestCode：" + requestCode + " resultCode:" + resultCode);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // 得到图片的全路径
                //String uriStr = ImageUtil.getPathFromUri(getContext(), data.getData());
                showPaperDialog(data.getData());
            }
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            int paperValue = data.getIntExtra("paperValue", 0);
            //手动修改lhValue
            paperAnalysiserClient.updatePaperValue(paperValue);
        } else if (requestCode == 1003 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // 得到图片的全路径
                //String uriStr = ImageUtil.getPathFromUri(getContext(), data.getData());
                showPaperType(data.getData());
            }
        }

    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("paper sdk closeSession");
        paperAnalysiserClient.closeSession();
    }
}