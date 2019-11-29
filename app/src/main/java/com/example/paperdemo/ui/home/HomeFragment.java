package com.example.paperdemo.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.paperdemo.PaperDetailActivity;
import com.example.paperdemo.R;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.UiOption;
import com.ikangtai.papersdk.event.IBitmapAnalysisEvent;
import com.ikangtai.papersdk.model.PaperCoordinatesData;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.AiCode;
import com.ikangtai.papersdk.util.ImageUtil;
import com.ikangtai.papersdk.util.TensorFlowTools;
import com.ikangtai.papersdk.util.ToastUtils;

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
        String titleText = getContext().getString(R.string.paper_result_dialog_title);
        /**
         * 标题颜色
         */
        int titleTextColor = getContext().getResources().getColor(R.color.color_444444);
        /**
         * 标尺线
         */
        int tagLineImageResId = R.drawable.paper_line;
        /**
         * t滑块图标
         */
        int tLineResId = R.drawable.test_paper_t_line;
        /**
         * c滑块图标
         */
        int cLineResId = R.drawable.test_paper_c_line;
        /**
         * 水平翻转文字
         */
        String flipText = getContext().getString(R.string.paper_result_dialog_flip);
        /**
         * 水平翻转文字颜色
         */
        int flipTextColor = getContext().getResources().getColor(R.color.color_67A3FF);
        /**
         * 提示文字
         */
        String hintText = getContext().getString(R.string.paper_result_dialog_hit);
        /**
         * 提示文字颜色
         */
        int hintTextColor = getContext().getResources().getColor(R.color.color_444444);
        /**
         * 返回按钮
         */
        int backResId = R.drawable.test_paper_return;
        /**
         * 确认按钮
         */
        int confirmResId = R.drawable.test_paper_confirm;
        /**
         * tc线默认值宽度
         */
        float tcLineWidth = getContext().getResources().getDimension(R.dimen.dp_2);
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
                .tcLineWidth(tcLineWidth)
                .build();
        //试纸识别sdk相关配置
        Config config = new Config.Builder().pixelOfdExtended(true).margin(10).uiOption(uiOption).build();
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

    private void showPaperDialog(Uri uriStr) {
        if (uriStr != null) {
            //File file = ImageUtil.getFileFromUril(uriStr);
            Bitmap fileBitmap = null;
            try {
                fileBitmap = ImageUtil.getUriToBitmap(getContext(), uriStr);
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
                    detailTv.setText("耗时 " + (endTime - startTime) + "\n" + paperResult.toString());
                    //试纸抠图结果
                    paperImageView.setImageBitmap(paperResult.getPaperBitmap());
                    //开启外扩开关后 会返回不带边距bitmap
                    paperNoMarginImageView.setImageBitmap(paperResult.getNoMarginBitmap());

                    //显示试纸结果
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
                    detailTv.setText("耗时 " + (System.currentTimeMillis() - startTime) + "\n" + "模糊值 " + (paperCoordinatesData != null ? paperCoordinatesData.getBlurValue() : 0) + "\n" + "错误码 " + code + "\n" + "message " + errorResult);
                    paperNoMarginImageView.setImageBitmap(null);
                    if (paperCoordinatesData != null && paperCoordinatesData.getImageL() != null) {
                        paperImageView.setImageBitmap(TensorFlowTools.getBitmapByMat(paperCoordinatesData.getImageL()));
                    } else {
                        paperImageView.setImageBitmap(null);
                    }
                }

                @Override
                public void saasAnalysisError(String errorResult, int code) {
                    ToastUtils.show(getContext(), AiCode.getMessage(code));
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
                // 得到图片的全路径
                //String uriStr = ImageUtil.getPathFromUri(getContext(), data.getData());
                showPaperDialog(data.getData());
            }
        }else if (requestCode == 1002 && resultCode == Activity.RESULT_OK){
            int paperValue=data.getIntExtra("paperValue",0);
            //手动修改lhValue
            paperAnalysiserClient.updatePaperValue(paperValue);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paperAnalysiserClient.closeSession();
    }
}