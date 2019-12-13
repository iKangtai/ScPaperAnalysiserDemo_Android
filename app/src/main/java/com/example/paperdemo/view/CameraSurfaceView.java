package com.example.paperdemo.view;

/**
 * desc
 *
 * @author xiongyl 2019/11/6 21:17
 */

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mSurfaceHolder;

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

}
