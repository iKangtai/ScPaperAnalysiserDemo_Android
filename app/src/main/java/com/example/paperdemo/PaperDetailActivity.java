package com.example.paperdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.paperdemo.view.OvulationSeekBar;
import com.example.paperdemo.view.TopBar;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.FileUtil;

import java.io.File;
import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * 试纸详情
 *
 * @author
 */
public class PaperDetailActivity extends Activity implements View.OnClickListener {
    public static final String TAG = PaperDetailActivity.class.getSimpleName();
    private TopBar topBar;
    /**
     * 试纸条
     */
    private ImageView paperImg;
    /**
     * 修改试纸条参考值
     */
    private TextView updatePaperResult;
    private OvulationSeekBar ovulationSeekBar;
    /**
     * 试纸结果
     */
    private TextView analysisResultTitle;

    /**
     * 试纸结果描述
     */

    private TextView analysisResult;
    /**
     * 试纸结果描述提醒
     */
    private TextView analysisDescHint;
    /**
     * 试纸时间
     */
    private TextView paperTime;
    /**
     * 闹钟选择框
     */
    private CheckBox remindCB;
    /**
     * 保存
     */
    private Button saveBtn;
    private String paperDate;
    private String paperNameId;
    private int paperResult;
    private int lhPaperAlType;

    private PaperResult paperBean;
    public static final String PIC_JPG = ".jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_detail_layout);
        topBar = findViewById(R.id.topBar);
        ovulationSeekBar = findViewById(R.id.ovulationSeekBar);
        paperImg = findViewById(R.id.paperImg);
        analysisResultTitle = findViewById(R.id.analysisResultTitle);
        analysisResult = findViewById(R.id.analysisResult);
        analysisDescHint = findViewById(R.id.analysisDescHint);
        updatePaperResult = findViewById(R.id.updatePaperResult);
        paperTime = findViewById(R.id.camera_result_time);
        remindCB = findViewById(R.id.remindCB);
        saveBtn = findViewById(R.id.save_btn);
        loadData();
    }

    private void loadData() {
        Serializable serializable = getIntent().getSerializableExtra("bean");
        if (serializable != null) {
            if (serializable instanceof PaperResult) {
                paperBean = (PaperResult) serializable;
                paperDate = paperBean.getPaperTime();
                paperNameId = paperBean.getPaperId();
                paperResult = (int) paperBean.getPaperValue();
                paperResult = handlePaperResult(paperResult);
                lhPaperAlType = paperBean.getPaperType();
            }
        }

        if (topBar != null) {
            topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
                @Override
                public void leftClick() {
                    finish();
                }

                @Override
                public void midLeftClick() {

                }

                @Override
                public void midRightClick() {

                }

                @Override
                public void rightClick() {

                }
            });
        }

        if (ovulationSeekBar != null) {
            ovulationSeekBar.setSeekBarTitle(getString(R.string.color_reference_bar_title_1));

            ovulationSeekBar.setCallbackListener(new OvulationSeekBar.CallbackListener() {
                @Override
                public void changeResult(int result) {
                    showAnalysisResult(result);
                    result = result == 0 ? 1 : result;
                    paperResult = result;
                }
            });

        }

        if (updatePaperResult != null) {
            updatePaperResult.getPaint().setUnderlineText(true);
            updatePaperResult.setOnClickListener(this);
        }

        if (remindCB != null) {
            remindCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
        }

        if (paperImg != null) {
            //显示试纸照片
            String paperName;
            if (!TextUtils.isEmpty(paperNameId) && paperNameId.endsWith(PIC_JPG)) {
                paperName = paperNameId;
            } else {
                paperName = paperNameId + PIC_JPG;
            }
            String paperImgPath = FileUtil.getPlayCameraPath() + File.separator + paperName;
            File file = new File(paperImgPath);
            if (file.exists()) {
                Glide.clear(paperImg);
                Glide.with(PaperDetailActivity.this).load(file)
                        .signature(new StringSignature("1")).into(paperImg);
            }
        }

        if (paperTime != null) {
            if (!TextUtils.isEmpty(paperDate)) {
                int index = paperDate.lastIndexOf(":");
                if (index > 0) {
                    paperTime.setText(paperDate.substring(0, index));
                }

            }
        }

        if (saveBtn != null) {
            saveBtn.setOnClickListener(this);
        }


        //新的试纸需要sass返回的结果进行分析
        AnalysisOvulationPaperEventBus analysisOvulationPaperEventBus
                = new AnalysisOvulationPaperEventBus();
        analysisOvulationPaperEventBus.setValue(paperResult);
        analysisOvulationPaperEventBus.setLhPaperAlType(lhPaperAlType);
        analysisOvulationPaper(analysisOvulationPaperEventBus);

        if (analysisResult != null) {
            analysisResult.setVisibility(View.VISIBLE);
        }
        if (analysisDescHint != null) {
            analysisDescHint.setVisibility(View.VISIBLE);
        }

        showAnalysisResult(paperResult);
    }

    private void showAnalysisResult(int paperResult) {

        if (ovulationSeekBar != null) {
            ovulationSeekBar.setSeekBarStatus(paperResult);
        }

        if (analysisResultTitle != null) {
            String analysisResult = String.format(getString(R.string.lh_refer_result), paperResult);
            analysisResultTitle.setText(Html.fromHtml(analysisResult));
        }
        if (lhPaperAlType != 7) {
            //非孕橙试纸增加描述提醒
            if (analysisDescHint != null) {
                String desc = getString(R.string.paper_brand_shecare_result_hint);
                String filterDesc = getString(R.string.buy_shecare_brand_paper);
                int index = desc.indexOf(filterDesc);
                if (index > 0) {
                    analysisDescHint.setText(desc.substring(0, index));
                }

            }
        }

    }


    private int handlePaperResult(int paperResult) {
        if (paperResult < 0) {
            paperResult = 0;
        }
        return paperResult;
    }

    public void analysisOvulationPaper(AnalysisOvulationPaperEventBus analysisOvulationPaperEventBus) {
        if (analysisOvulationPaperEventBus.isShowDesc()) {
            String hint = analysisOvulationPaperEventBus.getDesc();
            if (!TextUtils.isEmpty(hint)) {
                analysisResult.setText(hint);
            } else {
                analysisResult.setText("");
            }
        } else {
            //获取sass分析出的试纸品牌信息
            lhPaperAlType = analysisOvulationPaperEventBus.getLhPaperAlType();
            int result = analysisOvulationPaperEventBus.getValue();
            //用户未拖动滑块，按照智能分析结果进行展示
            showAnalysisResult(result);
            ovulationSeekBar.setSeekBarStatus(result);
        }
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra("paperValue", paperResult);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        if (v == updatePaperResult) {
            v.setVisibility(View.INVISIBLE);
            ovulationSeekBar.setSeekbarEnable(true);
            ovulationSeekBar.setSeekBarTitle(getString(R.string.color_reference_bar_title_2));
            ovulationSeekBar.setSeekbarMapEnable(true);
        }

        if (v == saveBtn) {
            save();
        }
    }

    public static class AnalysisOvulationPaperEventBus {
        private String messageId;

        private boolean showDesc;

        private int value;

        private String lhOv;

        private String desc;

        private int lhPaperAlType;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public boolean isShowDesc() {
            return showDesc;
        }

        public void setShowDesc(boolean showDesc) {
            this.showDesc = showDesc;
        }

        public String getLhOv() {
            return lhOv;
        }

        public void setLhOv(String lhOv) {
            this.lhOv = lhOv;
        }

        public int getLhPaperAlType() {
            return lhPaperAlType;
        }

        public void setLhPaperAlType(int lhPaperAlType) {
            this.lhPaperAlType = lhPaperAlType;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
    }
}
