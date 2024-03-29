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

import android.media.Image;
import android.view.View;

import java.util.Set;

abstract class CameraViewImpl {

    protected final Callback mCallback;

    protected final PreviewImpl mPreview;

    private boolean holdCameraHigh = false;

    CameraViewImpl(Callback callback, PreviewImpl preview) {
        mCallback = callback;
        mPreview = preview;
    }

    View getView() {
        return mPreview.getView();
    }

    /**
     * @return {@code true} if the implementation was able to start the camera session.
     */
    abstract boolean start();

    abstract void stop();

    abstract boolean isCameraOpened();

    abstract void setFacing(int facing);

    abstract int getFacing();

    abstract Set<AspectRatio> getSupportedAspectRatios();

    /**
     * @return {@code true} if the aspect ratio was changed.
     */
    abstract boolean setAspectRatio(AspectRatio ratio);

    abstract AspectRatio getAspectRatio();

    abstract void setAutoFocus(boolean autoFocus);

    abstract boolean getAutoFocus();

    abstract void setFlash(int flash);

    abstract int getFlash();

    abstract void takePicture();

    abstract void setDisplayOrientation(int displayOrientation);

    public boolean isHoldCameraHigh() {
        return holdCameraHigh;
    }

    public void setHoldCameraHigh(boolean holdCameraHigh) {
        this.holdCameraHigh = holdCameraHigh;
    }

    abstract void freshFocus();

    interface Callback {

        void onCameraOpened();

        void onCameraClosed();

        void onPictureTaken(byte[] data);

        void onPreviewFrame(byte[] data);

        void onPreviewFrame(Image image);
    }


    abstract void holdFocus(float x, float y);
}
