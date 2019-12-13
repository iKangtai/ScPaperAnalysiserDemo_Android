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
 * desc
 *
 * @author xiongyl 2019/11/6 21:22
 */
public class SmartPaperMeasureContainerLayout extends FrameLayout {

    private ManualSmartPaperMeasureLayout manualSmartPaperMeasureLayout;
    private AutoSmartPaperMeasureLayout autoSmartPaperMeasureLayout;
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
        if (paperScanView != null) {
            paperScanView.setVisibility(VISIBLE);
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
        if (paperScanView != null) {
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
        if (paperScanView!=null){
            paperScanView.setVisibility(VISIBLE);
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
        if (paperScanView!=null){
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

    }

}

