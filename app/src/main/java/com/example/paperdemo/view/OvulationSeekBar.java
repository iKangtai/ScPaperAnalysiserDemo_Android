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

    private final int VALUE_1 = 0;
    private final int VALUE_2 = 5;
    private final int VALUE_3 = 10;
    private final int VALUE_4 = 15;
    private final int VALUE_5 = 20;
    private final int VALUE_6 = 25;
    private final int VALUE_7 = 45;
    private final int VALUE_8 = 65;

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
                mySeekBar.setProgress(POS_1);
                if (callback != null) {
                    callback.changeResult(1);
                }
            }
        });
        seekbar2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_2);
                if (callback != null) {
                    callback.changeResult(5);
                }
            }
        });
        seekbar3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_3);
                if (callback != null) {
                    callback.changeResult(10);
                }
            }
        });
        seekbar4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_4);
                if (callback != null) {
                    callback.changeResult(15);
                }
            }
        });
        seekbar5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_5);
                if (callback != null) {
                    callback.changeResult(20);
                }
            }
        });
        seekbar6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_6);
                if (callback != null) {
                    callback.changeResult(25);
                }
            }
        });
        seekbar7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_7);
                if (callback != null) {
                    callback.changeResult(45);
                }
            }
        });
        seekbar8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mySeekBar.setProgress(POS_8);
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
                    if (seekProgress < POS_2) {
                        setSeekBarStatus(POS_1);
                        callback.changeResult(VALUE_1);
                    } else if (seekProgress >= POS_2 && seekProgress < POS_3) {
                        setSeekBarStatus(POS_2);
                        callback.changeResult(VALUE_2);
                    } else if (seekProgress >= POS_3 && seekProgress < POS_4) {
                        setSeekBarStatus(POS_3);
                        callback.changeResult(VALUE_3);
                    } else if (seekProgress >= POS_4 && seekProgress < POS_5) {
                        setSeekBarStatus(POS_4);
                        callback.changeResult(VALUE_4);
                    } else if (seekProgress >= POS_5 && seekProgress < POS_6) {
                        setSeekBarStatus(POS_5);
                        callback.changeResult(VALUE_5);
                    } else if (seekProgress >= POS_6 && seekProgress < POS_7) {
                        setSeekBarStatus(POS_6);
                        callback.changeResult(VALUE_6);
                    } else if (seekProgress >= POS_7 && seekProgress < POS_8) {
                        setSeekBarStatus(POS_7);
                        callback.changeResult(VALUE_7);
                    } else if (seekProgress >= POS_8) {
                        setSeekBarStatus(POS_8);
                        callback.changeResult(VALUE_8);
                    }
                }
            }
        });
    }


    public void setSeekBarStatus(int result) {
        switch (result) {
            case VALUE_1:
                mySeekBar.setProgress(POS_1);
                break;
            case VALUE_2:
                mySeekBar.setProgress(POS_2);
                break;
            case VALUE_3:
                mySeekBar.setProgress(POS_3);
                break;
            case VALUE_4:
                mySeekBar.setProgress(POS_4);
                break;
            case VALUE_5:
                mySeekBar.setProgress(POS_5);
                break;
            case VALUE_6:
                mySeekBar.setProgress(POS_6);
                break;
            case VALUE_7:
                mySeekBar.setProgress(POS_7);
                break;
            case VALUE_8:
                mySeekBar.setProgress(POS_8);
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

