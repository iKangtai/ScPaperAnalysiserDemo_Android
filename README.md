# ScPaperAnalysiserDemo_Android
#一.复制sdk到项目libs目录
   1.ScOpenCV-release.aar，ScPaperAnalysiserLib-release.aar到libs目录
   2.添加implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])
#二.添加依赖库
  implementation  'org.tensorflow:tensorflow-android:+'
  implementation  'com.squareup.okhttp3:okhttp:3.9.1'
  implementation  'com.squareup.okhttp3:logging-interceptor:3.9.1'
  implementation  'com.squareup.okio:okio:1.13.0'
  implementation  'com.squareup.retrofit2:retrofit:2.3.0'
  implementation  'com.squareup.retrofit2:converter-gson:2.3.0'
  implementation  'io.reactivex.rxjava2:rxandroid:2.1.1'
  implementation  'io.reactivex.rxjava2:rxjava:2.x.x'
  implementation  'com.github.bumptech.glide:glide:3.7.0'

#三.使用方法
  1.初始化
    //初始化sdk
    paperAnalysiserClient = new PaperAnalysiserClient(getContext(), appId, appSecret, "xyl1@qq.com");

    String titleText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_title);
    int titleTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    int tagLineImageResId = com.ikangtai.papersdk.R.drawable.paper_line;
    int tLineResId = com.ikangtai.papersdk.R.drawable.test_paper_t_line;
    int cLineResId = com.ikangtai.papersdk.R.drawable.test_paper_c_line;
    String flipText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_flip);
    int flipTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_67A3FF);
    String hintText = getContext().getString(com.ikangtai.papersdk.R.string.paper_result_dialog_hit);
    int hintTextColor = getContext().getResources().getColor(com.ikangtai.papersdk.R.color.color_444444);
    int backResId = com.ikangtai.papersdk.R.drawable.test_paper_return;
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
    Config config = new Config.Builder().pixelOfdExtended(false).margin(50).uiOption(uiOption).build();
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