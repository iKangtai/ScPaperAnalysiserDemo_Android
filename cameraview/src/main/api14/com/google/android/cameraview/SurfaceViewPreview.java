/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.cameraview;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

class SurfaceViewPreview extends PreviewImpl {

    final SurfaceView mSurfaceView;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    SurfaceViewPreview(Context context, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.surface_view, parent);
        mSurfaceView = view.findViewById(R.id.surface_view);
        final SurfaceHolder holder = mSurfaceView.getHolder();
        //noinspection deprecation
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder h, int format, int width, int height) {
                setSize(width, height);
                //setAspectRatio(getPreviewHeight(), getPreviewWidth()); //固定竖屏显示
                float ratio;
                if (width > height) {
                    ratio = height * 1.0f / width;
                } else {
                    ratio = width * 1.0f / height;
                }
                if (ratio == getPreviewHeight() * 1f / getPreviewWidth()) {
                    if (!ViewCompat.isInLayout(mSurfaceView)) {
                        dispatchSurfaceChanged();
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder h) {
                setSize(0, 0);
            }
        });
    }

    @Override
    Surface getSurface() {
        return getSurfaceHolder().getSurface();
    }

    @Override
    SurfaceHolder getSurfaceHolder() {
        return mSurfaceView.getHolder();
    }

    @Override
    Bitmap getBitmap() {
        return null;
    }

    @Override
    View getView() {
        return mSurfaceView;
    }

    @Override
    Class getOutputClass() {
        return SurfaceHolder.class;
    }

    @Override
    void setDisplayOrientation(int displayOrientation) {
    }

    @Override
    boolean isReady() {
        return getPreviewWidth() != 0 && getPreviewHeight() != 0 && getWidth() != 0 && getHeight() != 0;
    }


    @Override
    void setPreviewSize(int width, int height) {
        super.setPreviewSize(width, height);
    }

    private void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        mSurfaceView.getLayoutParams().height = mSurfaceView.getWidth() * mRatioHeight / mRatioWidth;
//        if (width < height * mRatioWidth / mRatioHeight) {
//            setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//        } else {
//            setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//        }
    }
}
