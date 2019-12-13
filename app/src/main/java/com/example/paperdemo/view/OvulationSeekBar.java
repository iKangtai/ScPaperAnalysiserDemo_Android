package com.example.paperdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.paperdemo.R;

import androidx.annotation.Nullable;


/**
 * 类描述：伊仕半定量试纸专用
 * 创建时间：2018/8/10 16:39
 */
public class OvulationSeekBar extends LinearLayout  {
    public static final int POS_1 = 0;
    public static final int POS_2 = 15;
    public static final int POS_3 = 29;
    public static final int POS_4 = 43;
    public static final int POS_5 = 57;
    public static final int POS_6 = 71;
    public static final int POS_7 = 85;
    public static final int POS_8 = 100;
    private TextView seekbarTitle;
    private MySeekBar mySeekBar;
    private LinearLayout seekbar1, seekbar2, seekbar3, seekbar4, seekbar5, seekbar6,seekbar7,seekbar8;

    public OvulationSeekBar(Context context) {
        this(context, null);
    }

    public OvulationSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OvulationSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.activity_show_lh_seekbar, this);
        mySeekBar = findViewById(R.id.seekBar_shecare);
        seekbarTitle = findViewById(R.id.seekbarTitle);
        seekbar1 = findViewById(R.id.ll_seekbar_1);
        seekbar2 = findViewById(R.id.ll_seekbar_2);
        seekbar3 = findViewById(R.id.ll_seekbar_3);
        seekbar4 = findViewById(R.id.ll_seekbar_4);
        seekbar5 = findViewById(R.id.ll_seekbar_5);
        seekbar6 = findViewById(R.id.ll_seekbar_6);
        seekbar7 = findViewById(R.id.ll_seekbar_7);
        seekbar8 = findViewById(R.id.ll_seekbar_8);
        seekbar1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(0);
                if (callback != null) {
                    callback.changeResult(1);
                }
            }
        });
        seekbar2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(15);
                if (callback != null) {
                    callback.changeResult(5);
                }
            }
        });
        seekbar3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(29);
                if (callback != null) {
                    callback.changeResult(10);
                }
            }
        });
        seekbar4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(43);
                if (callback != null) {
                    callback.changeResult(15);
                }
            }
        });
        seekbar5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(57);
                if (callback != null) {
                    callback.changeResult(20);
                }
            }
        });
        seekbar6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(71);
                if (callback != null) {
                    callback.changeResult(25);
                }
            }
        });
        seekbar7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(85);
                if (callback != null) {
                    callback.changeResult(45);
                }
            }
        });
        seekbar8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(100);
                if (callback != null) {
                    callback.changeResult(65);
                }
            }
        });
        mySeekBar.setEnabled(false);
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekProgress = seekBar.getProgress();
                if (callback != null) {
                    if (seekProgress < 11) {
                        setSeekBarStatus(0);
                        callback.changeResult(0);
                    } else if (seekProgress >= 11 && seekProgress < 31) {
                        setSeekBarStatus(5);
                        callback.changeResult(5);
                    } else if (seekProgress >= 31 && seekProgress < 51) {
                        setSeekBarStatus(10);
                        callback.changeResult(10);
                    } else if (seekProgress >= 51 && seekProgress < 71) {
                        setSeekBarStatus(25);
                        callback.changeResult(25);
                    } else if (seekProgress >= 71 && seekProgress < 91) {
                        setSeekBarStatus(45);
                        callback.changeResult(45);
                    } else if (seekProgress >= 91) {
                        setSeekBarStatus(65);
                        callback.changeResult(65);
                    }
                }
            }
        });
    }


    public void setSeekBarStatus(int result) {
        switch (result) {
            case 0:
                mySeekBar.setProgress(0);
                break;
            case 5:
                mySeekBar.setProgress(20);
                break;
            case 10:
                mySeekBar.setProgress(40);
                break;
            case 25:
                mySeekBar.setProgress(60);
                break;
            case 45:
                mySeekBar.setProgress(80);
                break;
            case 65:
                mySeekBar.setProgress(100);
                break;
        }
    }

    public void setSeekBarTitle(String title) {
        if (seekbarTitle != null) {
            seekbarTitle.setText(title);
        }
    }

    public void setSeekbarEnable(boolean enable) {
        if (mySeekBar != null) {
            mySeekBar.setEnabled(enable);
        }
    }

    public void setSeekbarMapEnable(boolean enable) {
        if (mySeekBar != null) {
            mySeekBar.setDrawMap(enable);
        }
    }

    private CallbackListener callback = null;

    public void setCallbackListener(CallbackListener listener) {
        callback = listener;
    }

    public interface CallbackListener {
        void changeResult(int result);
    }
}

