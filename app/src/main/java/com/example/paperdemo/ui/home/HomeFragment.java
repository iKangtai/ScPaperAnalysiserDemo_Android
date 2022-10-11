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
import com.example.paperdemo.util.AiCode;
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

/**
 * Test paper picture recognition
 *
 * @author
 */
public class HomeFragment extends Fragment {
    private PaperAnalysiserClient paperAnalysiserClient;
    private ImageView paperImageView, paperNoMarginImageView;
    private TextView detailTv;
    private long startTime, endTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AiCode.initCodeData(getContext());
        /**
         * Use test network
         */
        Config.setTestServer(true);
        /**
         * Network timeout
         */
        Config.setNetTimeOut(30);

        //Customized test paper Ui display
        /**
         * title
         */
        String titleText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_title);
        /**
         * title color
         */
        int titleTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * paper line
         */
        int tagLineImageResId = com.ikangtai.papersdk.R.drawable.paper_line;
        /**
         * t line slider icon
         */
        int tLineResId = com.ikangtai.papersdk.R.drawable.test_paper_t_line;
        /**
         * c line slider icon
         */
        int cLineResId = com.ikangtai.papersdk.R.drawable.test_paper_c_line;
        /**
         * Flip text horizontally
         */
        String flipText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_flip);
        /**
         * Flip text color horizontally
         */
        int flipTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
        /**
         * Prompt text
         */
        String hintText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_hit);
        /**
         * Prompt text color
         */
        int hintTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * Back button
         */
        int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
        /**
         * Confirm button
         */
        int confirmResId = com.ikangtai.papersdk.R.drawable.test_paper_confirm;
        /**
         * Back button text color
         */
        int backButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * Confirm button text color
         */
        int confirmButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
        /**
         * Bottom menu way display button
         */
        boolean visibleBottomButton = false;
        /**
         * tc line default value width
         */
        float tcLineWidth = getContext().getResources().getDimension(com.ikangtai.papersdk.R.dimen.dp_2);
        /**
         * Back button background id
         */
        int backButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
        /**
         * Confirm button background id
         */
        int confirmButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
        /**
         * Back button text
         */
        String backButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_back);
        /**
         * Confirm button text
         */
        String confirmButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_confirm);
        /**
         * sample pic id
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
                .sampleResId(sampleResId)
                .feedbackTextColor(feedbackTextColor)
                .language(Locale.ENGLISH.getLanguage())
                .build();
        /**
         * There are two ways to customize the log file, just set it once
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
        //Test paper to identify sdk related configuration
        Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 20)).uiOption(uiOption).logWriter(logWriter).netTimeOutRetryCount(1).build();
        //init sdk
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), AppConstant.appId, AppConstant.appSecret, "xyl1@qq.com", config, new InitCallback() {
            @Override
            public void onFailure(int code, String message) {
                ToastUtils.show(getContext(), message);
            }

            @Override
            public void onSuccess() {
                if (!SupportDeviceUtil.isSupport(getContext(), AppConstant.appId, AppConstant.appSecret)) {
                    new AlertDialog.Builder(getContext()).setMessage("The current device performance is too poor, and the automatic SDK recognition is slow").show();
                }
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
     * Open the interface for selecting pictures
     */
    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    private void showPaperDialog(final Uri fileUri) {
        if (fileUri != null) {
            Bitmap fileBitmap = null;
            try {
                fileBitmap = ImageUtil.getUriToBitmap(getContext(), fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileBitmap == null) {
                ToastUtils.show(getContext(), getString(R.string.image_file_error));
                return;
            }
            startTime = System.currentTimeMillis();
            paperAnalysiserClient.analysisBitmap(fileBitmap, new SampleBitmapAnalysisEventAdapter() {
                @Override
                public void save(PaperResult paperResult) {
                    super.save(paperResult);
                    LogUtils.d("Save test paper analysis results：\n" + paperResult.toString());
                    //Confirmation of test paper result confirmation box Display test paper result
                    if (paperResult.getErrNo() != 0) {
                        ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                    }
                    detailTv.setText("time " + (endTime - startTime) + "\n" + paperResult.toString());
                    //Test paper cutout result
                    paperImageView.setImageBitmap(paperResult.getPaperBitmap());
                    //After turning on the external expansion switch, it will return to the bitmap without margins
                    paperNoMarginImageView.setImageBitmap(paperResult.getNoMarginBitmap());
                    //Show test strip result
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
                    //Test paper cutout successful result
                    LogUtils.d("Test strips are automatically cut out successfully");
                    endTime = System.currentTimeMillis();
                    return false;
                }

                @Override
                public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                    super.analysisError(paperCoordinatesData, errorResult, code);
                    //Test paper cutout failed result
                    LogUtils.d("Error in automatic matting of test strips code：" + code + " errorResult:" + errorResult);
                    ToastUtils.show(getContext(), AiCode.getMessage(code));
                    detailTv.setText("time " + (System.currentTimeMillis() - startTime) + "\n" + "Blur Value " + (paperCoordinatesData != null ? paperCoordinatesData.getBlurValue() : 0) + "\n" + "Error " + code + "\n" + "message " + errorResult);
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
                    //Test paper saas analysis failed
                    LogUtils.d("Test strip analysis error code：" + code + " errorResult:" + errorResult);
                    ToastUtils.show(getContext(), AiCode.getMessage(code));
                }
            });
        } else {
            ToastUtils.show(getContext(), "Insufficient permissions");
        }
    }

    Bitmap paperBitmap = null;

    private void showPaperType(final Uri fileUri) {
        if (fileUri != null) {
            try {
                paperBitmap = ImageUtil.getUriToBitmap(getContext(), fileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startTime = System.currentTimeMillis();
            paperAnalysiserClient.paperTypeAnalysis(paperBitmap, new IPaperTypeAnalysisResultEvent() {
                @Override
                public void onSuccessPaperTypeAnalysis(int paperType) {
                    LogUtils.d("Type：\n" + paperType);
                    detailTv.setText("Time " + (System.currentTimeMillis() - startTime) + "\nType " + paperType);
                    paperImageView.setImageBitmap(paperBitmap);
                    paperNoMarginImageView.setImageBitmap(null);

                }

                @Override
                public void onFailurePaperTypeAnalysis(int errorCode, String message) {
                    LogUtils.d("Test paper type analysis error code：" + errorCode + " errorResult:" + message);
                    ToastUtils.show(getContext(), AiCode.getMessage(errorCode));
                    paperImageView.setImageBitmap(null);
                    paperNoMarginImageView.setImageBitmap(null);
                    detailTv.setText("Time " + (System.currentTimeMillis() - startTime) + "\n" + message);
                }
            });
        } else {
            ToastUtils.show(getContext(), "Insufficient permissions");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("requestCode：" + requestCode + " resultCode:" + resultCode);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                showPaperDialog(data.getData());
            }
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            int paperValue = data.getIntExtra("paperValue", 0);
            //Manually modify lhValue
            paperAnalysiserClient.updatePaperValue(paperValue);
        } else if (requestCode == 1003 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
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