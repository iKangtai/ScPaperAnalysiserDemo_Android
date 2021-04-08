# ScPaperAnalysiserDemo_Android

## Demo
<http://fir.ikangtai.cn/knu1>

## Internationalization
English | [中文文档](README_zh.md)

## Access Guide
### 一.Integrated SDK
   ```java
       api 'com.ikangtai.papersdk:ScPaperAnalysiserLib:1.5.8'
   ```
### 二.Add dependency library url
   ```java
      maven { url 'https://dl.bintray.com/ikangtaijcenter123/ikangtai' }
   ```
### 三.Instructions
  ```java
      //The network configuration needs to be before the SDK initialization
      //Test network
      Config.setTestServer(true);
      //Timeout
      Config.setNetTimeOut(30);

      //Determine whether the mobile phone performance meets the SDK requirements
      1.SupportDeviceUtil.isSupport(getContext(),AppConstant.appId, AppConstant.appSecret)#Inaccurate first time verification
      2.Use SupportDeviceUtil.isSupport(getContext(),AppConstant.appId, AppConstant.appSecret) in application init, and use SupportDeviceUtil.isSupport(getContext()) at the app
  ```
  1.init
  ```java
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
  ```
  2.General configuration
  ```java
    //Test paper to identify sdk related configuration
    Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 20)).uiOption(uiOption).build();
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com",config);
  ```
  3.Use recognition test paper picture
  ```java
    paperAnalysiserClient.analysisBitmap(fileBitmap, new IBitmapAnalysisEvent() {
                    @Override
                    public void showProgressDialog() {
                        //show progress dialog
                        LogUtils.d("Show Loading Dialog");
                        that.showProgressDialog(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //Stop network request
                                paperAnalysiserClient.stopShowProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void dismissProgressDialog() {
                        //hide progress dialog
                        LogUtils.d("Hide Loading Dialog");
                    }

                    @Override
                    public void cancel() {
                        //Cancel test strip result confirm dialog
                        LogUtils.d("Cancel test strip result dialog");
                        ToastUtils.show(getContext(), AiCode.getMessage(AiCode.CODE_201));
                    }

                    @Override
                    public void save(PaperResult paperResult) {
                        //Save test paper analysis results
                        LogUtils.d("Save test paper analysis results：\n"+paperResult.toString());
                        if (paperResult.getErrNo() != 0) {
                            ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                        }

                    }

                    @Override
                    public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                        LogUtils.d("Test strips are automatically cut out successfully");
                        return false;
                    }

                    @Override
                    public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                        //Test paper cutout failed result
                        LogUtils.d("Error in automatic matting of test strips code：" + code + " errorResult:" + errorResult);
                        ToastUtils.show(getContext(), AiCode.getMessage(code));

                    }

                    @Override
                    public void saasAnalysisError(String errorResult, int code) {
                        //Test strip analysis error
                        LogUtils.d("Test strip analysis error code：" + code + " errorResult:" + errorResult);
                        ToastUtils.show(getContext(), AiCode.getMessage(code));

                    }
                    @Override
                    public void paperResultDialogShow(PaperResultDialog paperResultDialog) {
                        paperResultDialog.getHintTv().setGravity(Gravity.LEFT);
                        paperResultDialog.setSampleResId(R.drawable.confirm_sample_pic_lh);
                    }
                });
  ```
  4.Identify the video stream
  TextureView video preview
  ```java
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (paperAnalysiserClient.isObtainPreviewFrame()) {
                    return;
                }
                //The top half of the video is a square image
                Bitmap originSquareBitmap;
                if (textureView.getBitmap()!=null){
                    originSquareBitmap = ImageUtil.topCropBitmap(textureView.getBitmap());
                }else {
                    originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, TensorFlowTools.getDegree(getActivity()));
                }
                paperAnalysiserClient.analysisCameraData(originSquareBitmap);
            }
        };
    cameraUtil.initCamera(getActivity(), textureView, mPreviewCallback);
  ```

  SurfaceView video preview
  ```java
        Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (paperAnalysiserClient.isObtainPreviewFrame()) {
                    return;
                }
                startTime = System.currentTimeMillis();
                //The top half of the video is a square image
                Bitmap originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, TensorFlowTools.getDegree(getActivity()));
                paperAnalysiserClient.analysisCameraData(originSquareBitmap);
            }
        };
        cameraUtil.initCamera(getActivity(), surfaceView, mPreviewCallback);
        //Need to perform coordinate conversion in analysisSuccess and analysisResult callback methods
        ICameraAnalysisEvent iCameraAnalysisEvent = new ICameraAnalysisEvent() {
                @Override
                public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                    ....
                    PaperCoordinatesData newPaperCoordinatesData = TensorFlowTools.convertPointToScreen(cameraUtil.getCurrentCamera(), surfaceView.getWidth(), surfaceView.getHeight(), paperCoordinatesData);
                    smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(newPaperCoordinatesData, originSquareBitmap);
                    return false;
                }

                @Override
                public void analysisResult(PaperCoordinatesData paperCoordinatesData) {
                    ....
                    PaperCoordinatesData newPaperCoordintaesData = TensorFlowTools.convertPointToScreen(cameraUtil.getCurrentCamera(), surfaceView.getWidth(), surfaceView.getHeight(), paperCoordinatesData);
                    smartPaperMeasureContainerLayout.showAutoSmartPaperMeasure(newPaperCoordintaesData, null);
                }
        }
  ```
  5.Actively release resources after use
  ```java
    paperAnalysiserClient.closeSession();
  ```

### View log
  You can control whether the SDK run debug log is output and the output path by calling the following methods. By default, the SDK run debug log is turned on. The user can manually close it.
  You can also filter the "sc-ble-log" Tag through Locat to display SDK specific logs.


  ```java
      /**
       * There are two ways to configure log
       * 1. {@link Config.Builder#logWriter(Writer)}
       * 2. {@link Config.Builder#logFilePath(String)}
       */
      LogUtils.LOG_SWITCH=true;
      Config config = new Config.Builder().logWriter(logWriter).build();
      //Config config = new Config.Builder().logFilePath(logFilePath).build();
      scPeripheralManager.init(getContext(), config);
  ```

### Custom UI
    ```java
      //Customized test paper Ui display
      /**
       * title
       */
      String titleText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_title);
      /**
       * title color
       */
      int titleTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
      /**
       * paper line
       */
      int tagLineImageResId = com.ikangtai.papersdk.R.drawable.paper_line;
      /**
       * t line slider icon
       */
      int tLineResId = com.ikangtai.papersdk.R.drawable.test_paper_t_line;
      /**
       * c line slider icon
       */
      int cLineResId = com.ikangtai.papersdk.R.drawable.test_paper_c_line;
      /**
       * Flip text horizontally
       */
      String flipText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_flip);
      /**
       * Flip text color horizontally
       */
      int flipTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
      /**
       * Prompt text
       */
      String hintText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_hit);
      /**
       * Prompt text color
       */
      int hintTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
      /**
       * Back button
       */
      int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
      /**
       * Confirm button
       */
      int confirmResId = com.ikangtai.papersdk.R.drawable.test_paper_confirm;
      /**
       * Back button text color
       */
      int backButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
      /**
       * Confirm button text color
       */
      int confirmButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
      /**
       * Bottom menu way display button
       */
      boolean visibleBottomButton = false;
      /**
       * tc line default value width
       */
      float tcLineWidth = getContext().getResources().getDimension(com.ikangtai.papersdk.R.dimen.dp_2);
      /**
       * Back button background id
       */
      int backButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
      /**
       * Confirm button background id
       */
      int confirmButtonBgResId = com.ikangtai.papersdk.R.drawable.paper_button_drawable;
      /**
       * Back button text
       */
      String backButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_back);
      /**
       * Confirm button text
       */
      String confirmButtonText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_confirm);
      /**
       * sample pic id
       */
      int sampleResId = com.ikangtai.papersdk.R.drawable.confirm_sample_pic_lh;
      int feedbackTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
      UiOption uiOption = new UiOption.Builder(getContext())
                      .titleText(titleText)
                      .tagLineImageResId(tagLineImageResId)
                      .titleTextColor(titleTextColor)
                      .tLineResId(tLineResId)
                      .cLineResId(cLineResId)
                      .flipText(flipText)
                      .flipTextColor(flipTextColor)
                      .hintText(hintText)
                      .hintTextColor(hintTextColor)
                      .backResId(backResId)
                      .confirmResId(confirmResId)
                      .tcLineWidth(tcLineWidth)
                      .backButtonBgResId(backButtonBgResId)
                      .backButtonText(backButtonText)
                      .confirmButtonBgResId(confirmButtonBgResId)
                      .confirmButtonText(confirmButtonText)
                      .backButtonTextColor(backButtonTextColor)
                      .confirmButtonTextColor(confirmButtonTextColor)
                      .visibleBottomButton(visibleBottomButton)
                      .sampleResId(sampleResId)
                      .feedbackTextColor(feedbackTextColor)
                      .language(Locale.ENGLISH.getLanguage())
                      .build();
      //Test paper to identify sdk related configuration
      Config config = new Config.Builder().pixelOfdExtended(true).margin(50).uiOption(uiOption).netTimeOutRetryCount(1).build();
      paperAnalysiserClient.init(config);
    ```

### Confusion configuration
  If your application uses code obfuscation, please add the following configuration to avoid SDK being unavailable due to incorrect obfuscation.
  ```java
    -dontwarn  com.ikangtai.papersdk.**
    -keep class com.ikangtai.papersdk.** {*;}
    -keepclasseswithmembernames class *{
    	native <methods>;
    }
    -keep class org.tensorflow.** {*;}
    -keep class tensorflow.** {*;}
  ```
## SDK Privacy Agreement
   a) Purpose/purpose of collecting personal information: optimize and improve test strip algorithm<br/>
   b) The type of personal information collected: device model, operating system, mobile phone developer identifier, network data<br/>
   c) Required permissions: network permissions, camera permissions<br/>
   d) Third-party SDK privacy policy link: https://static.shecarefertility.com/shecare/resource/dist/#/papersdk_privacy_policy<br/>
   e) Provider: Beijing ikangtai Technology Co., Ltd.<br/>