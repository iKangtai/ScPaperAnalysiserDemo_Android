# ScPaperAnalysiserDemo_Android
### 一.引入试纸sdk库

       1.api 'com.ikangtai.papersdk:ScPaperAnalysiserLib:1.1'

### 二.添加依赖库地址

      maven { url 'https://dl.bintray.com/ikangtaijcenter123/ikangtai' }

### 三.使用方法

  1.初始化

    //初始化sdk
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");

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
            .build();
    //试纸识别sdk相关配置
    Config config = new Config.Builder().pixelOfdExtended(true).margin(50).uiOption(uiOption).build();
    paperAnalysiserClient.init(config);

  2.调用识别试纸图片

    paperAnalysiserClient.analysisBitmap(fileBitmap, new IBitmapAnalysisEvent() {
                    @Override
                    public void showProgressDialog() {
                        ToastUtils.show(getContext(), "显示加载框");
                    }

                    @Override
                    public void dismissProgressDialog() {
                        ToastUtils.show(getContext(), "隐藏加载框");
                    }

                    @Override
                    public void cancel() {
                        ToastUtils.show(getContext(), "取消试纸编辑");
                    }

                    @Override
                    public void save(PaperResult paperResult) {
                        if (!TextUtils.isEmpty(paperResult.getErrMsg())) {
                            ToastUtils.show(getContext(), paperResult.getErrMsg());
                        }

                    }

                    @Override
                    public boolean analysisSuccess(PaperCoordinatesData paperCoordinatesData, Bitmap originSquareBitmap, Bitmap clipPaperBitmap) {
                        endTime = System.currentTimeMillis();
                        return false;
                    }

                    @Override
                    public void analysisError(PaperCoordinatesData paperCoordinatesData, String errorResult, int code) {
                        ToastUtils.show(getContext(), errorResult + code);

                    }

                    @Override
                    public void saasAnalysisError(String errorResult, int code) {
                        ToastUtils.show(getContext(), errorResult + code);
                    }
                });
  3.调用完成释放资源

    paperAnalysiserClient.closeSession();