package com.example.paperdemo.ui.home;

import android.app.Activity;
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

import com.example.paperdemo.R;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.UiOption;
import com.ikangtai.papersdk.event.IBitmapAnalysisEvent;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    private PaperAnalysiserClient paperAnalysiserClient;
    public static String appId = "100017";
    public static String appSecret = "b1eed2fb4686e1b1049a9486d49ba015af00d5a0";
    private ImageView paperImageView, paperNoMarginImageView;
    private TextView detailTv;
    private long startTime, endTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //初始化sdk
        paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
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
         * 返回按钮
         */
        int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
        /**
         * 确认按钮
         */
        int confirmResId = com.ikangtai.papersdk.R.drawable.test_paper_confirm;

        UiOption uiOption = new UiOption.Builder()
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
                .build();
        //试纸识别sdk相关配置
        Config config = new Config.Builder().pixelOfdExtended(true).margin(50).uiOption(uiOption).build();
        paperAnalysiserClient.init(config);

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

    private void showPaperDialog(Uri uri) {
        if (uri != null) {
            Bitmap fileBitmap = null;
            try {
                fileBitmap = ImageUtil.getUriToBitmap(getContext(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileBitmap == null) {
                ToastUtils.show(getContext(), "解析试纸图片出现异常");
                return;
            }
            startTime = System.currentTimeMillis();
            paperAnalysiserClient.analysisBitmap(fileBitmap, new IBitmapAnalysisEvent() {
                @Override
                public void showProgressDialog() {
                    ToastUtils.show(getContext(), "显示加载框");
                }

                @Override
                public void dismissProgressDialog() {
                    ToastUtils.show(getContext(), "隐藏加载框");
                }

                @Override
                public void cancel() {
                    ToastUtils.show(getContext(), "取消试纸编辑");
                }

                @Override
                public void save(PaperResult paperResult) {
                    if (!TextUtils.isEmpty(paperResult.getErrMsg())) {
                        ToastUtils.show(getContext(), paperResult.getErrMsg());
                    }
                    detailTv.setText("耗时 " + (endTime - startTime) + "\n" + paperResult.toString());
                    paperImageView.setImageBitmap(paperResult.getPaperBitmap());
                    //开启外扩开关后 会返回不带边距bitmap
                    paperNoMarginImageView.setImageBitmap(paperResult.getNoMarginBitmap());

                    //手动修改lhValue
                    //paperAnalysiserClient.updatePaperValue(100);
                }

                @Override
                public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                    endTime = System.currentTimeMillis();
                    return false;
                }

                @Override
                public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                    ToastUtils.show(getContext(), errorResult + code);
                    detailTv.setText("耗时 " + (System.currentTimeMillis() - startTime) + "\n" + "模糊值 " + (paperCoordinatesData != null ? paperCoordinatesData.getBlurValue() : 0) + "\n" + "错误码 " + code + "\n" + "message " + errorResult);
                    if (paperCoordinatesData != null && paperCoordinatesData.getImageL() != null) {
                        paperImageView.setImageBitmap(TensorFlowTools.getBitmapByMat(paperCoordinatesData.getImageL()));
                    } else {
                        paperImageView.setImageBitmap(null);
                    }
                }

                @Override
                public void saasAnalysisError(String errorResult, int code) {
                    ToastUtils.show(getContext(), errorResult + code);
                }
            });
        } else {
            ToastUtils.show(getContext(), "权限不足");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                showPaperDialog(data.getData());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paperAnalysiserClient.closeSession();
    }
}