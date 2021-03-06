package com.example.paperdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.paperdemo.R;
import com.ikangtai.papersdk.model.PaperCoordinatesData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Test paper finder frame View
 *
 * @author xiongyl 2019/11/6 21:22
 */
public class SmartPaperMeasureContainerLayout extends FrameLayout {

    private ManualSmartPaperMeasureLayout manualSmartPaperMeasureLayout;
    private AutoSmartPaperMeasureLayout autoSmartPaperMeasureLayout;
    private CardAutoSmartPaperMeasureLayout cardAutoSmartPaperMeasureLayout;
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
        paperScanView = findViewById(R.id.paper_scan_view);
    }

    /**
     * Show manual photo test strip
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
        if (paperScanView != null) {
            paperScanView.setVisibility(GONE);
        }

    }

    /**
     * Display card type test paper automatic recognition
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
        if (paperScanView != null) {
            paperScanView.setCardMode(true);
            paperScanView.setVisibility(GONE);
        }

    }

    /**
     * Manually cut out the measurement range Data
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
     * Card type matting range Data
     *
     * @return
     */
    public CardAutoSmartPaperMeasureLayout.Data getCardAutoSmartPaperMeasureData() {
        if (manualSmartPaperMeasureLayout != null) {
            return cardAutoSmartPaperMeasureLayout.getData();
        }
        return null;
    }

    /**
     * Show automatic photo test strip
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
        if (paperScanView != null) {
            paperScanView.setCardMode(false);
            paperScanView.setVisibility(VISIBLE);
        }

    }

    /**
     * Display the results of automatic scanning test paper
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
        if (paperScanView != null) {
            paperScanView.setCardMode(false);
            paperScanView.setVisibility(VISIBLE);
        }

        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(paperCoordinatesData);
        }
    }

    /**
     * Display the results of automatic scanning test paper
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
            paperScanView.setCardMode(false);
            paperScanView.setVisibility(VISIBLE);
        }
        if (cardAutoSmartPaperMeasureLayout != null) {
            cardAutoSmartPaperMeasureLayout.setVisibility(GONE);
            cardAutoSmartPaperMeasureLayout.scanPaperCoordinatesData(null);
        }
        if (autoSmartPaperMeasureLayout != null) {
            autoSmartPaperMeasureLayout.scanPaperCoordinatesData(paperCoordinatesData, originSquareBitmap);
        }
    }

    /**
     * Display the results of manual photo test strips
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
        if (paperScanView != null) {
            paperScanView.setCardMode(true);
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

    }

}

