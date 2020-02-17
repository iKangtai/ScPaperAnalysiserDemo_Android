package com.example.paperdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.paperdemo.R;


/**
 * 项目名称：MySeekbar
 * 类描述：伊仕半定量试纸专用
 * 创建时间：2018/4/27 19:15
 */
public class MySeekBar extends AppCompatSeekBar {
    private boolean drawMap;
    /**
     * 文本的颜色
     */
    private int mTitleTextColor;
    /**
     * 文本的大小
     */
    private float mTitleTextSize;
    /**
     * 文字的内容
     */
    private String mTitleText;

    /**
     * 背景图片
     */
    private int img;
    private Bitmap map;
    //bitmap对应的宽高
    private float img_width, img_height;
    Paint paint;

    private float numTextWidth;
    /**
     * 测量seekbar的规格
     */
    private Rect rect_seek;
    private Paint.FontMetrics fm;

    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;
    /**
     * 文本中轴线X坐标
     */
    private float textCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float textBaselineY;
    /**
     * 文字的方位
     */
    private int textAlign;

    public MySeekBar(Context context) {
        this(context, null);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MySeekBar, defStyleAttr, 0);
        img = array.getResourceId(0, R.drawable.cr_yishi_tc0);
        array.recycle();
        getImgWH();
        //设置控件的padding 给提示文字留出位置
        setPadding((int) Math.ceil(img_width) / 2, (int) Math.ceil(img_height) + 45, (int) Math.ceil(img_height) / 2, 0);
    }

    /**
     * 获取图片的宽高
     */
    private void getImgWH() {
        map = BitmapFactory.decodeResource(getResources(), img);
        img_width = map.getWidth();
        img_height = map.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawMap) {
            rect_seek = this.getProgressDrawable().getBounds();
            //定位文字背景图片的位置
            int seekProgress = getProgress();
            if (seekProgress < OvulationSeekBar.POS_2) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc0);
            } else if (seekProgress >= OvulationSeekBar.POS_2 && seekProgress < OvulationSeekBar.POS_3) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc5);
            } else if (seekProgress >= OvulationSeekBar.POS_3 && seekProgress < OvulationSeekBar.POS_4) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc10);
            } else if (seekProgress >= OvulationSeekBar.POS_4 && seekProgress < OvulationSeekBar.POS_5) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc15);
            } else if (seekProgress >= OvulationSeekBar.POS_5 && seekProgress < OvulationSeekBar.POS_6) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc20);
            } else if (seekProgress >= OvulationSeekBar.POS_6 && seekProgress < OvulationSeekBar.POS_7) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc25);
            } else if (seekProgress >= OvulationSeekBar.POS_7 && seekProgress < OvulationSeekBar.POS_8) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc45);
            } else if (seekProgress >= OvulationSeekBar.POS_8) {
                map = BitmapFactory.decodeResource(getResources(), R.drawable.cr_yishi_tc65);
            }
            float bm_x = rect_seek.width() * getProgress() / getMax();
            float bm_y = rect_seek.height() + 20;
            //画背景图
            canvas.drawBitmap(map, bm_x, bm_y, paint);
        }
    }

    public void setDrawMap(boolean drawMap) {
        this.drawMap = drawMap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //监听手势滑动，不断重绘文字和背景图的显示位置
        invalidate();
        return super.onTouchEvent(event);
    }

}
