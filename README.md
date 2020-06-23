# ScPaperAnalysiserDemo_Android
### 一.引入试纸sdk库
   ```java
       api 'com.ikangtai.papersdk:ScPaperAnalysiserLib:1.5.5-alpha4'
   ```
### 二.添加依赖库地址
   ```java
      maven { url 'https://dl.bintray.com/ikangtaijcenter123/ikangtai' }
   ```
### 三.使用方法
  ```java
      //网络配置需要在初始化sdk之前
      //使用测试网络
      Config.setTestServer(true);
      //网络超时时间
      Config.setNetTimeOut(30);

      //判断手机性能是否满足sdk要求
      1.SupportDeviceUtil.isSupport(getContext(),AppConstant.appId, AppConstant.appSecret)#第一次校验不准
      2.application初始化中调用SupportDeviceUtil.isSupport(getContext(),AppConstant.appId, AppConstant.appSecret)，实际判断处调用SupportDeviceUtil.isSupport(getContext())
  ```
  1.初始化
  ```java
    //初始化sdk
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
  ```
  2.常规配置
  ```java
    /**
    * log默认路径/data/Android/pageName/files/Documents/log.txt,可以通过LogUtils.getLogFilePath()获取
    * 自定义log文件有两种方式,设置一次即可
    * 1. {@link Config.Builder#logWriter(Writer)}
    * 2. {@link Config.Builder#logFilePath(String)}
    */
    String logFilePath = new File(FileUtil.createRootPath(getContext()), "log_test.txt").getAbsolutePath();
    BufferedWriter logWriter = null;
    try {
        logWriter = new BufferedWriter(new FileWriter(logFilePath, true), 2048);
    } catch (IOException e) {
       e.printStackTrace();
    }
    //试纸识别sdk相关配置
    Config config = new Config.Builder().pixelOfdExtended(true).paperMinHeight(PxDxUtil.dip2px(getContext(), 20)).uiOption(uiOption).logWriter(logWriter).build();
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com",config);
    或者
    paperAnalysiserClient.init(config);a
  ```
  3.UI配置
  ```java
    //定制试纸Ui显示
    /**
     * 标题
     */
    String titleText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_title);
    /**
     * 标题颜色
     */
    int titleTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    /**
     * 标尺线
     */
    int tagLineImageResId = com.ikangtai.papersdk.R.drawable.paper_line;
    /**
     * t滑块图标
     */
    int tLineResId = com.ikangtai.papersdk.R.drawable.test_paper_t_line;
    /**
     * c滑块图标
     */
    int cLineResId = com.ikangtai.papersdk.R.drawable.test_paper_c_line;
    /**
     * 水平翻转文字
     */
    String flipText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_flip);
    /**
     * 水平翻转文字颜色
     */
    int flipTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
    /**
     * 提示文字
     */
    String hintText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_hit);
    /**
     * 提示文字颜色
     */
    int hintTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    /**
     * 返回按钮
     */
    int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
    /**
     * 确认按钮
     */
    int confirmResId = com.ikangtai.papersdk.R.drawable.test_paper_confirm;
    /**
     * 返回按钮文字颜色
     */
    int backButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    /**
     * 确认按钮文字颜色
     */
    int confirmButtonTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    /**
     * 显示底部按钮
     */
    boolean visibleBottomButton = false;
    UiOption uiOption = new UiOption.Builder()
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
            .backButtonTextColor(backButtonTextColor)
            .confirmButtonTextColor(confirmButtonTextColor)
            .visibleBottomButton(visibleBottomButton)
            .build();
    //试纸识别sdk相关配置
    Config config = new Config.Builder().pixelOfdExtended(true).margin(50).uiOption(uiOption).netTimeOutRetryCount(1).build();
    paperAnalysiserClient.init(config);
  ```
  4.调用识别试纸图片
  ```java
    paperAnalysiserClient.analysisBitmap(fileBitmap, new IBitmapAnalysisEvent() {
                    @Override
                    public void showProgressDialog() {
                        //显示加载框
                        LogUtils.d("Show Loading Dialog");
                        that.showProgressDialog(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                //停止网络请求
                                paperAnalysiserClient.stopShowProgressDialog();
                            }
                        });
                    }

                    @Override
                    public void dismissProgressDialog() {
                        //隐藏加载框
                        LogUtils.d("Hide Loading Dialog");
                    }

                    @Override
                    public void cancel() {
                        LogUtils.d("取消试纸结果确认");
                        //试纸结果确认框取消
                        ToastUtils.show(getContext(), AiCode.getMessage(AiCode.CODE_201));
                    }

                    @Override
                    public void save(PaperResult paperResult) {
                        LogUtils.d("保存试纸分析结果：\n"+paperResult.toString());
                        //试纸结果确认框确认 显示试纸结果
                        if (paperResult.getErrNo() != 0) {
                            ToastUtils.show(getContext(), AiCode.getMessage(paperResult.getErrNo()));
                        }

                    }

                    @Override
                    public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                        LogUtils.d("试纸自动抠图成功");
                        return false;
                    }

                    @Override
                    public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                        LogUtils.d("试纸自动抠图出错 code：" + code + " errorResult:" + errorResult);
                        //试纸抠图失败结果
                        ToastUtils.show(getContext(), AiCode.getMessage(code));

                    }

                    @Override
                    public void saasAnalysisError(String errorResult, int code) {
                        LogUtils.d("试纸分析出错 code：" + code + " errorResult:" + errorResult);
                        //试纸saas分析失败
                        ToastUtils.show(getContext(), AiCode.getMessage(code));

                    }
                    @Override
                    public void paperResultDialogShow(PaperResultDialog paperResultDialog) {
                        paperResultDialog.getHintTv().setGravity(Gravity.LEFT);
                        paperResultDialog.setSampleResId(R.drawable.confirm_sample_pic_lh);
                    }
                });
  ```
  5.识别视频流
  TextureView视频预览
  ```java
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (paperAnalysiserClient.isObtainPreviewFrame()) {
                    return;
                }
                startTime = System.currentTimeMillis();
                //视频上半部分正方形图片
                //Bitmap originSquareBitmap= TensorFlowTools.convertFrameToBitmap(data, camera, surfaceView.getWidth(), surfaceView.getWidth(), TensorFlowTools.getDegree(getActivity()));
                //Bitmap originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, TensorFlowTools.getDegree(getActivity()));
                Bitmap originSquareBitmap = ImageUtil.topCropBitmap(textureView.getBitmap());
                paperAnalysiserClient.analysisCameraData(originSquareBitmap);
            }
        };
    cameraUtil.initCamera(getActivity(), textureView, mPreviewCallback);
  ```

  SurfaceView视频预览
  ```java
        Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (paperAnalysiserClient.isObtainPreviewFrame()) {
                    return;
                }
                startTime = System.currentTimeMillis();
                //视频上半部分正方形图片
                Bitmap originSquareBitmap = TensorFlowTools.convertFrameToBitmap(data, camera, TensorFlowTools.getDegree(getActivity()));
                paperAnalysiserClient.analysisCameraData(originSquareBitmap);
            }
        };
        cameraUtil.initCamera(getActivity(), textureView, mPreviewCallback);
        //需要在analysisSuccess和analysisResult回调方法进行坐标转换
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
  7.调用完成释放资源
  ```java
    paperAnalysiserClient.closeSession();
  ```

  8.混淆代码过滤
  ```java
    -dontwarn  com.ikangtai.papersdk.**
    -keep class com.ikangtai.papersdk.** {*;}
    -keepclasseswithmembernames class *{
    	native <methods>;
    }
  ```
