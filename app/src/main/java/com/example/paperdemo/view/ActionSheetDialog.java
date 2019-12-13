package com.example.paperdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.paperdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * desc
 *
 * @author xiongyl 2019/12/11 22:24
 */
public class ActionSheetDialog {
    protected Dialog dialog;

    public void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public boolean showing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }

        return false;
    }


    public ActionSheetDialog withOverLay() {
        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
        }
        return this;
    }

    private Context context;
    private TextView txt_title;
    private TextView txt_cancel;
    private LinearLayout lLayout_content;
    private ScrollView sLayout_content;
    private boolean showTitle = false;
    private List<SheetItem> sheetItemList;
    private Display display;

    public ActionSheetDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ActionSheetDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_actionsheet, null);

        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        sLayout_content = view.findViewById(R.id.sLayout_content);
        lLayout_content = view
                .findViewById(R.id.lLayout_content);
        txt_title = view.findViewById(R.id.txt_title);
        txt_cancel = view.findViewById(R.id.txt_cancel);
        //关闭cameraActivity
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        return this;
    }

    public ActionSheetDialog setTitle(String title) {
        showTitle = true;
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        return this;
    }

    public ActionSheetDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ActionSheetDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public ActionSheetDialog setOnDismissListener(final DialogInterface.OnDismissListener onDismissListener) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(dialog);
                }
            }
        });
        return this;
    }

    /**
     * @param strItem  条目名称
     * @param color    条目字体颜色，设置null则默认蓝色
     * @param listener
     * @return
     */
    public ActionSheetDialog addSheetItem(String strItem, SheetItemColor color,
                                          OnSheetItemClickListener listener) {
        if (sheetItemList == null) {
            sheetItemList = new ArrayList<>();
        }
        sheetItemList.add(new SheetItem(strItem, color, listener));
        return this;
    }


    /**
     * 设置条目布局
     */
    private void setSheetItems() {
        if (sheetItemList == null || sheetItemList.size() <= 0) {
            return;
        }

        int size = sheetItemList.size();

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sLayout_content
                    .getLayoutParams();
            params.height = display.getHeight() / 2;
            sLayout_content.setLayoutParams(params);
        }

        // 循环添加条目
        for (int i = 1; i <= size; i++) {
            final int index = i;
            SheetItem sheetItem = sheetItemList.get(i - 1);
            String strItem = sheetItem.name;
            SheetItemColor color = sheetItem.color;
            final OnSheetItemClickListener listener = sheetItem.itemClickListener;

            TextView textView = new TextView(context);
            textView.setText(strItem);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);

            View view = new View(context);
            view.setBackgroundColor(Color.parseColor("#aaaaaa"));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

            // 背景图片
            if (size == 1) {
                if (showTitle) {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
                }
            } else {
                if (showTitle) {
                    if (i >= 1 && i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                } else {
                    if (i == 1) {
                        textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
                    } else if (i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                }
            }

            // 字体颜色
            if (color == null) {
                textView.setTextColor(Color.parseColor(SheetItemColor.Blue
                        .getName()));
            } else {
                textView.setTextColor(Color.parseColor(color.getName()));
            }

            // 高度
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int) (45 * scale + 0.5f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, height));

            // 点击事件
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(index);
                    dialog.dismiss();
                }
            });

            if (size == 1) {
                if (showTitle) {
                    lLayout_content.addView(view);
                } else {
                }
            } else {
                if (showTitle) {
                    if (i >= 1 && i < size) {
                        lLayout_content.addView(view);
                    } else {
                        lLayout_content.addView(view);
                    }
                } else {
                    if (i == 1) {
                    } else if (i < size) {
                        lLayout_content.addView(view);
                    } else {
                        lLayout_content.addView(view);
                    }
                }
            }

            lLayout_content.addView(textView);
        }
    }

    public ActionSheetDialog show() {
        //8.0新特性
        setSheetItems();
        if (Build.VERSION.SDK_INT >= 26) {
            //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1);
        }
        dialog.show();
        return this;
    }

    public interface OnSheetItemClickListener {
        void onClick(int which);
    }

    public class SheetItem {
        String name;
        OnSheetItemClickListener itemClickListener;
        SheetItemColor color;

        public SheetItem(String name, SheetItemColor color,
                         OnSheetItemClickListener itemClickListener) {
            this.name = name;
            this.color = color;
            this.itemClickListener = itemClickListener;
        }
    }

    public enum SheetItemColor {
        Blue("#FF7486"), Red("#ff2121");

        private String name;

        SheetItemColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
