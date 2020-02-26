package com.example.paperdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    /**
     * 自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg, View.OnClickListener onClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);
        LinearLayout layout = v.findViewById(R.id.dialog_view);
        // main.xml中的ImageView
        spaceshipImage = v.findViewById(R.id.img);
        tipTextView = v.findViewById(R.id.tipTextView);
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
        layout.setOnClickListener(onClickListener);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        return loadingDialog;
    }

    public static void setTipTextView(String msg) {
        tipTextView.setText(msg);
    }

    public static void setTipTextVisibility(int visibility) {
        tipTextView.setVisibility(visibility);
    }
}
