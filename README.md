# ScPaperAnalysiserDemo_Android
### 一.引入试纸sdk库

       1.api 'com.ikangtai.papersdk:ScPaperAnalysiserLib:1.5.2'

### 二.添加依赖库地址

      maven { url 'https://dl.bintray.com/ikangtaijcenter123/ikangtai' }

### 三.使用方法

      //网络配置需要在初始化sdk之前
      //使用测试网络
      Config.setTestServer(true);
      //网络超时时间
      Config.setNetTimeOut(30);
          
  1.初始化
    
    //初始化sdk
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");
  2.常规配置
  
    /**
    * log默认路径/data/Android/pageName/files/Documents/log.txt,可以通过LogUtils.getLogFilePath()获取
    * 自定义log文件有两种方式,设置一次即可
    *   1.new Config.Builder().logWriter(logWriter).
    *   2.new Config.Builder().logFilePath(logFilePath).
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
    paperAnalysiserClient.init(config);
    
  3.UI配置
  
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
    Config config = new Config.Builder().pixelOfdExtended(true).margin(50).uiOption(uiOption).build();
    paperAnalysiserClient.init(config);

  4.调用识别试纸图片

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
                    }
                });
  5.调用完成释放资源

    paperAnalysiserClient.closeSession();
    
  6.混淆代码过滤
    -dontwarn  com.ikangtai.papersdk.**
    -keep class com.ikangtai.papersdk.** {*;}
    -keepclasseswithmembernames class *{
    	native <methods>;
    }