package com.example.paperdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.paperdemo.R;

/**
 * desc
 *
 * @author xiongyl 2020/2/26 18:49
 */
public class ProgressDialog {
    private static ImageView spaceshipImage;
    private static TextView tipTextView;
    private static ImageView closeDialog;

    public static Dialog createLoadingDialog(Context context, View.OnClickListener onClickListener) {
        return createLoadingDialog(context, null, onClickListener);
    }

    /**
     * 自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    private static Dialog createLoadingDialog(Context context, String msg, View.OnClickListener onClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        RelativeLayout layout = v.findViewById(R.id.dialog_view);
        // main.xml中的ImageView
        spaceshipImage = v.findViewById(R.id.img);
        tipTextView = v.findViewById(R.id.tipTextView);
        closeDialog = v.findViewById(R.id.close_dialog);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (TextUtils.isEmpty(msg)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            // 设置加载信息
            tipTextView.setText(msg);
        }
        closeDialog.setOnClickListener(onClickListener);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout);
        WindowManager.LayoutParams lay = loadingDialog.getWindow().getAttributes();
        Rect rect = new Rect();
        View decorView = loadingDialog.getWindow().getDecorView();
        decorView.getWindowVisibleDisplayFrame(rect);
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        lay.height = display.getHeight();
        lay.width = display.getWidth();
        return loadingDialog;
    }

    public static void setTipTextView(String msg) {
        tipTextView.setText(msg);
    }

    public static void setTipTextVisibility(int visibility) {
        tipTextView.setVisibility(visibility);
    }
}
