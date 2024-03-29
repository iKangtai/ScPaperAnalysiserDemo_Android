package com.example.paperdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paperdemo.view.OvulationSeekBar;
import com.example.paperdemo.view.TopBar;
import com.ikangtai.papersdk.Config;
import com.ikangtai.papersdk.PaperAnalysiserClient;
import com.ikangtai.papersdk.event.ICyclesAnalysisResultEvent;
import com.ikangtai.papersdk.http.reqmodel.PaperCyclesAnalysisReq;
import com.ikangtai.papersdk.http.respmodel.PaperCyclesAnalysisResp;
import com.ikangtai.papersdk.model.PaperResult;
import com.ikangtai.papersdk.util.DateUtil;
import com.ikangtai.papersdk.util.FileUtil;
import com.ikangtai.papersdk.util.ToastUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Test paper details
 *
 * @author
 */
public class PaperDetailActivity extends Activity implements View.OnClickListener {
    public static final String TAG = PaperDetailActivity.class.getSimpleName();
    private TopBar topBar;
    /**
     * Test strip
     */
    private ImageView paperImg;
    /**
     * Modify the reference value of the test strip
     */
    private TextView updatePaperResult;
    private OvulationSeekBar ovulationSeekBar;
    /**
     * Test paper result
     */
    private TextView analysisResultTitle;
    private TextView analysisRatioResultTitle;
    /**
     * Test paper result description
     */
    private TextView analysisResult;
    /**
     * Test paper result description reminder
     */
    private TextView analysisDescHint;
    /**
     * Test paper time
     */
    private TextView paperTime;
    /**
     * save
     */
    private Button saveBtn;
    private String paperDate;
    private String paperNameId;
    private int paperResult;
    private int lhPaperAlType;

    private PaperResult paperBean;
    public static final String PIC_JPG = ".jpg";
    private PaperAnalysiserClient paperAnalysiserClient;
    private TextView console;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.setNetTimeOut(30);
        //init sdk
        paperAnalysiserClient = new PaperAnalysiserClient(this, AppConstant.appId, AppConstant.appSecret, "xyl1@qq.com");
        setContentView(R.layout.activity_paper_detail_layout);
        topBar = findViewById(R.id.topBar);
        ovulationSeekBar = findViewById(R.id.ovulationSeekBar);
        paperImg = findViewById(R.id.paperImg);
        analysisResultTitle = findViewById(R.id.analysisResultTitle);
        analysisRatioResultTitle = findViewById(R.id.analysisRatioResultTitle);
        analysisResult = findViewById(R.id.analysisResult);
        analysisDescHint = findViewById(R.id.analysisDescHint);
        updatePaperResult = findViewById(R.id.updatePaperResult);
        paperTime = findViewById(R.id.camera_result_time);
        saveBtn = findViewById(R.id.save_btn);
        console = findViewById(R.id.console);
        loadData();
        ArrayList<PaperCyclesAnalysisReq.Paper> papers = new ArrayList<>();
        PaperCyclesAnalysisReq.Paper paper = new PaperCyclesAnalysisReq.Paper();
        paper.setTimestamp(DateUtil.getStringToDate(paperBean.getPaperTime()));
        paper.setValue(paperBean.getPaperValue());
        paper.setRatio((float) paperBean.getRatioValue());
        papers.add(paper);

        PaperCyclesAnalysisReq.CyclePaper cyclePaper = new PaperCyclesAnalysisReq.CyclePaper();
        cyclePaper.setYcSemiQuantitative(papers);
        ArrayList<PaperCyclesAnalysisReq.CyclePaper> cyclePapers = new ArrayList<>();
        cyclePapers.add(cyclePaper);
        paperAnalysiserClient.paperCyclesAnalysis(true, 0, cyclePapers, new ICyclesAnalysisResultEvent() {
            @Override
            public void onSuccessPaperCyclesAnalysis(ArrayList<PaperCyclesAnalysisResp.Data> beans) {
                if (!beans.isEmpty()) {
                    ToastUtils.show(PaperDetailActivity.this, TextUtils.isEmpty(beans.get(0).getYcQuanInfo()) ? beans.get(0).getYcSemiQuanInfo() : beans.get(0).getYcQuanInfo());
                }
            }

            @Override
            public void onFailurePaperCyclesAnalysis(int errorCode, String message) {
                ToastUtils.show(PaperDetailActivity.this, message);
            }
        });

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

        if (paperImg != null) {
            //Show test strip photo
            String paperName = paperNameId + PIC_JPG;
            FileUtil.initPath(PaperDetailActivity.this, "");
            String paperImgPath = FileUtil.getPlayCameraPath() + File.separator + paperName;
            File file = new File(paperImgPath);
            if (file.exists()) {
                Glide.with(PaperDetailActivity.this).load("file://" + paperImgPath).into(paperImg);
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


        //The new test paper needs to analyze the results returned by sass
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

        console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(console.getText(), "console")) {
                    StringBuffer stringBuffer = new StringBuffer();
                    if (paperBean != null) {
                        stringBuffer.append(paperBean.toString());
                        stringBuffer.append("\n------------------------------\n");
                    }
                    console.setText(stringBuffer.toString());
                } else {
                    console.setText("");
                }
            }
        });
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

        String analysisRatioResult = String.format(getString(R.string.retio_refer_result), paperBean.getRatioValue() + "");
        analysisRatioResultTitle.setText(Html.fromHtml(analysisRatioResult));
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
            //Get the brand information of the test paper analyzed by sass
            lhPaperAlType = analysisOvulationPaperEventBus.getLhPaperAlType();
            int result = analysisOvulationPaperEventBus.getValue();
            //The user did not drag the slider and displayed according to the intelligent analysis results
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


