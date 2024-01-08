package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.model.PaperCoordinatesData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SmartPaperMeasureContainerLayout extends FrameLayout {

    private ManualSmartPaperMeasureLayout manualSmartPaperMeasureLayout;
    private AutoSmartPaperMeasureLayout autoSmartPaperMeasureLayout;
    private CardAutoSmartPaperMeasureLayout cardAutoSmartPaperMeasureLayout;
    private ShecareCardAutoSmartPaperMeasureLayout shecareCardAutoSmartPaperMeasureLayout;
    private PaperScanView paperScanView;

    public SmartPaperMeasureContainerLayout(@NonNull Context context) {
        super(context);
    }

    public SmartPaperMeasureContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartPaperMeasureContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        manualSmartPaperMeasureLayout = findViewById(R.id.paper_manual_smart_paper_measureLayout);
        autoSmartPaperMeasureLayout = findViewById(R.id.paper_auto_smart_paper_measureLayout);
        cardAutoSmartPaperMeasureLayout = findViewById(R.id.card_auto_smart_paper_measureLayout);
        shecareCardAutoSmartPaperMeasureLayout = findViewById(R.id.shecare_card_auto_smart_paper_measureLayout);
        paperScanView = findViewById(R.id.paper_scan_view);
    }

    /**
     * 显示手动拍照试纸
     */
    public void showManualSmartPaperMeasure() {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(VISIBLE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }

    }

    /**
     * 显示卡型试纸自动识别
     */
    public void showCardAutoSmartPaperMeasure() {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setCardMode(true);
            paperScanView.setVisibility(VISIBLE);
        }

    }

    /**
     * 显示孕橙卡型试纸自动识别
     */
    public void showShecareCardAutoSmartPaperMeasure() {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }

        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }

    }

    /**
     * 手动测量范围 Data
     *
     * @return
     */
    public ManualSmartPaperMeasureLayout.Data getManualSmartPaperMeasuereData() {
        if (manualSmartPaperMeasureLayout != null) {
            return manualSmartPaperMeasureLayout.getData();
        }
        return null;
    }

    /**
     * 卡型抠图范围 Data
     *
     * @return
     */
    public CardAutoSmartPaperMeasureLayout.Data getCardAutoSmartPaperMeasureData() {
        if (cardAutoSmartPaperMeasureLayout != null) {
            return cardAutoSmartPaperMeasureLayout.getData();
        }
        return null;
    }

    /**
     * 孕橙卡型抠图范围 Data
     *
     * @return
     */
    public ShecareCardAutoSmartPaperMeasureLayout.Data getShecareCardAutoSmartPaperMeasureData() {
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            return shecareCardAutoSmartPaperMeasureLayout.getData();
        }
        return null;
    }

    /**
     * 显示自动拍照试纸
     */
    public void showAutoSmartPaperMeasure() {
        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setCardMode(false);
            paperScanView.setVisibility(VISIBLE);
        }

    }

    public void showAutoSmartPaperMeasureLayout() {
        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }

    }

    /**
     * 显示自动扫描试纸结果
     */
    public void showAutoSmartPaperMeasure(PaperCoordinatesData paperCoordinatesData) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(VISIBLE);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setCardMode(false);
            paperScanView.setVisibility(VISIBLE);
        }

        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(paperCoordinatesData);
        }
    }

    /**
     * 显示自动扫描试纸结果
     */
    public void showAutoSmartPaperMeasure(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(VISIBLE);
        }
        if (paperScanView != null) {
            if (originSquareBitmap != null) {
                paperScanView.setVisibility(GONE);
            } else {
                paperScanView.setCardMode(false);
                paperScanView.setVisibility(VISIBLE);
            }
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(paperCoordinatesData, originSquareBitmap);
        }
    }

    /**
     * 显示手动拍照试纸结果
     */
    public void showManualSmartPaperMeasure(Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(VISIBLE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(originSquareBitmap);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }
    }

    public void showCardAutoSmartPaperMeasure(Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(originSquareBitmap);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setCardMode(true);
            paperScanView.setVisibility(VISIBLE);
        }
    }

    public void showShecareCardAutoSmartPaperMeasure(Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(originSquareBitmap);
        }
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }
    }

    public void showCardAutoSmartPaperMeasure(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(originSquareBitmap, paperCoordinatesData);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (paperScanView != null) {
            paperScanView.setCardMode(true);
            paperScanView.setVisibility(VISIBLE);
        }
    }

    public void showShecareCardAutoSmartPaperMeasure(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap) {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setVisibility(GONE);
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.setVisibility(GONE);
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.setVisibility(VISIBLE);
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(originSquareBitmap, paperCoordinatesData);
        }
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }
    }

    public void clearImageData() {

        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }

        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }

        if (shecareCardAutoSmartPaperMeasureLayout != null) {
            shecareCardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
    }


    public void setPaperType(int paperType) {
        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setPaperType(paperType);
        }
    }

    public void setPaperViewClick(ManualSmartPaperMeasureLayout.ViewClick viewClick) {
        if (manualSmartPaperMeasureLayout != null) {
            manualSmartPaperMeasureLayout.setViewClick(viewClick);
        }
    }


    public void destroy() {
        if (paperScanView != null) {
            paperScanView.drawStop();
        }
    }

}
