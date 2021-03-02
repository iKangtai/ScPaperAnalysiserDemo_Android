package com.example.paperdemo.util;

import android.content.Context;

import com.example.paperdemo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * desc
 *
 * @author xiongyl 2021/3/2 16:39
 */
public class AiCode {
    private static Map<Integer, String> codes = new HashMap<>();
    //图片错误
    public static final int CODE_IMAGE_ERROR = -1001;
    //网络错误
    public static final int CODE_NET_ERROR = -3;
    //SDK 校验失败或无效
    public static final int CODE_SDK_ERROR = -2;
    //未知错误（默认值）
    public static final int CODE_ERROR = -1;
    //抠图成功、分析成功，流程内没有错误产生；
    public static final int CODE_0 = 0;
    //没有找到试纸；
    public static final int CODE_1 = 1;
    //距离过远；
    public static final int CODE_2 = 2;
    //背景过脏；
    public static final int CODE_3 = 3;
    //距离过近；
    public static final int CODE_4 = 4;
    //试纸不全；
    public static final int CODE_5 = 5;
    //神经网络加载错误；
    public static final int CODE_6 = 6;
    //背景内不止一张试纸；
    public static final int CODE_7 = 7;
    //曝光不足；
    public static final int CODE_8 = 8;
    //曝光过度；
    public static final int CODE_9 = 9;
    //试纸局部曝光过度；
    public static final int CODE_10 = 10;
    //画面模糊；
    public static final int CODE_11 = 11;
    //曝光不足（OpenCV 二次确认）；
    public static final int CODE_12 = 12;
    //抠图失败，视频流扫描超时
    public static final int CODE_17 = 17;
    // 手动裁剪失败，请检查传入的图片和坐标点
    public static final int CODE_50 = 50;
    //抠图成功，但是分析失败
    public static final int CODE_101 = 101;
    //抠图成功，分析成功，但是用户取消确认抠图和分析结果
    public static final int CODE_201 = 201;
    //抠图成功，分析成功，但未检测到参考线，请确认试纸有参考线显示
    public static final int CODE_202 = 202;
    //抠图成功，分析成功，但未检测到T线，请确认试纸有T线显示
    public static final int CODE_203 = 203;


    /*410，401	缺少相关参数
    403	频率过高，关入小黑屋
    400	认证错误，
    402	超时，认证无效
    201	参数不合法
    202	分析结果转化json出错
    203	分析失败，服务返回
    204	t图片base64编解码失败
    410	分析失败
    411	试纸无效
    200	正确*/
    public static final int CODE_501 = 501;
    public static final int CODE_503 = 503;
    public static final int CODE_500 = 500;
    public static final int CODE_502 = 502;
    public static final int CODE_301 = 301;
    public static final int CODE_302 = 302;
    public static final int CODE_303 = 303;
    public static final int CODE_304 = 304;
    public static final int CODE_510 = 510;
    public static final int CODE_511 = 511;

    /*// 试纸抠图、分析错误码
    /// 抠图成功、分析成功，流程内没有错误产生；
    SCErrorCodeNoError = 0,

    /// SDK 校验失败或无效
    SCErrorCodeSDKError = -2,
    /// 未知错误（默认值）；
    SCErrorCodeUnknownError = -1,

    *//* 阶段 1 中间过程错误码（或单张照片扫描结果的错误码） *//*
    /// 没有找到试纸；
    SCErrorCodeNoPaper = 1,
    /// 距离过远；
    SCErrorCodeTooFar = 2,
    /// 背景过脏；
    SCErrorCodeTooDirty = 3,
    /// 距离过近；
    SCErrorCodeTooClose = 4,
    /// 试纸不全；
    SCErrorCodeNotCompleted = 5,
    /// 神经网络加载错误；
    SCErrorCodeHedNetError = 6,
    /// 背景内不止一张试纸；
    SCErrorCodeTooManyPapers = 7,
    /// 曝光不足；
    SCErrorCodeUnderExposure = 8,
    /// 曝光过度；
    SCErrorCodeExposed = 9,
    /// 试纸局部曝光过度；
    SCErrorCodePartlyExposed = 10,
    /// 画面模糊；
    SCErrorCodeBlurred = 11,
    /// 曝光不足（OpenCV 二次确认）；
    SCErrorCodeUnderExposure2 = 12,

    *//* 阶段 1 结束错误码 *//*
    /// 抠图失败，视频流扫描超时
    SCErrorCodeVideoOutofDate = 17,

    /// 手动裁剪失败，请检查传入的图片和坐标点
    SCErrorCodeManualClipError = 50,

    *//* 阶段 2 错误码 *//*
    /// 抠图成功，但是分析失败
    SCErrorCodeGetValueError = 101,

    *//* 阶段 3 错误码 *//*
    /// 抠图成功，分析成功，但是用户取消确认抠图和分析结果
    SCErrorCodeUserCanceled = 201,
    /// 抠图成功，分析成功，但未检测到参考线，请确认试纸有参考线显示
    SCErrorCodeNoCLine = 202,*/

    static {
        //图片错误
        codes.put(CODE_IMAGE_ERROR, "图片错误");
        //未知错误
        codes.put(CODE_ERROR, "未知错误");
        //抠图成功
        codes.put(CODE_0, "抠图成功");
        //没有试纸；错误提示文案：没有找到试纸
        codes.put(CODE_1, "没有找到试纸");
        //距离过远；错误提示文案：距离过远，请调整拍摄距离
        codes.put(CODE_2, "距离过远，请调整拍摄距离");
        // 背景过脏；错误提示文案：背景有干扰，请在浅色纯背景下拍摄
        codes.put(CODE_3, "背景有干扰，请在浅色纯背景下拍摄");
        // 距离过近；错误提示文案：距离过近，请调整拍摄距离
        codes.put(CODE_4, "距离过近，请调整拍摄距离");
        //有残缺；错误提示文案：试纸不全，请保持全部试纸处在取景框内
        codes.put(CODE_5, "试纸不全，请保持全部试纸处在取景框内");
        // 神经网络处理错误；错误提示文案：算法处理中，请稍候
        codes.put(CODE_6, "算法处理中，请稍候");
        // 两张试纸；错误提示文案：一次只能拍摄一张试纸
        codes.put(CODE_7, "一次只能拍摄一张试纸");
        // 曝光不足；错误提示文案：光线太暗，请调整光线或打开闪光灯后拍摄
        codes.put(CODE_8, "光线太暗，请调整光线或打开闪光灯后拍摄");
        // 曝光过度；错误提示文案：光线太强，请调整光线或关掉闪光灯后拍摄
        codes.put(CODE_9, "光线太强，请调整光线或关掉闪光灯后拍摄");
        // 试纸局部过度曝光；错误提示文案：局部光线太强，请调整光线或关掉闪光灯后拍摄
        codes.put(CODE_10, "局部光线太强，请调整光线或关掉闪光灯后拍摄");
        // 画面模糊；错误提示文案：画面模糊，请保持手机稳定或重新对焦
        codes.put(CODE_11, "画面模糊，请保持手机稳定或重新对焦");
        // 曝光不足；错误提示文案：光线太暗，请调整光线或打开闪光灯后拍摄
        codes.put(CODE_12, "光线太暗，请调整光线或打开闪光灯后拍摄");
        //视频流扫描超时
        codes.put(CODE_17, "视频流扫描超时");
        /// 手动裁剪失败，请检查传入的图片和坐标点
        codes.put(CODE_50, "手动裁剪失败，请检查传入的图片和坐标点");
        //抠图成功，分析成功，但是用户取消确认抠图和分析结果。
        codes.put(CODE_201, "用户取消");
        //抠图成功，分析成功，但未检测到参考线，请确认试纸有参考线显示
        codes.put(CODE_202, "未检测到T线和C线，如果T线和C线存在，请拖动到相应的位置");
        codes.put(CODE_203, "未检测到T线，如果T线存在，请拖动到相应的位置");
        //抠图成功，但是分析失败
        codes.put(CODE_101, "试纸分析出错");
        //SDK 校验失败或无效
        codes.put(CODE_SDK_ERROR, "SDK 校验失败或无效");
        //网络错误
        codes.put(CODE_NET_ERROR, "网络错误");

        /*410，401	缺少相关参数
        403	频率过高，关入小黑屋
        400	认证错误，
        402	超时，认证无效
        201	参数不合法
        202	分析结果转化json出错
        203	分析失败，服务返回
        204	t图片base64编解码失败
        410	分析失败
        411	试纸无效
        200	正确*/
        codes.put(CODE_501, "缺少相关参数");
        codes.put(CODE_503, "频率过高，关入小黑屋");
        codes.put(CODE_500, "认证错误");
        codes.put(CODE_502, "超时，认证无效");
        codes.put(CODE_301, "参数不合法");
        codes.put(CODE_302, "分析结果转化json出错");
        codes.put(CODE_303, "分析失败，服务返回");
        codes.put(CODE_304, "图片base64编解码失败");
        codes.put(CODE_510, "分析失败");
        codes.put(CODE_511, "试纸无效");
    }

    public static void initCodeData(Context context) {
        //图片错误
        codes.put(CODE_IMAGE_ERROR, context.getString(R.string.code_image_code));
        //未知错误
        codes.put(CODE_ERROR, context.getString(R.string.code_error));
        //抠图成功
        codes.put(CODE_0, context.getString(R.string.code_0));
        //没有试纸；错误提示文案：没有找到试纸
        codes.put(CODE_1, context.getString(R.string.code_1));
        //距离过远；错误提示文案：距离过远，请调整拍摄距离
        codes.put(CODE_2, context.getString(R.string.code_2));
        // 背景过脏；错误提示文案：背景有干扰，请在浅色纯背景下拍摄
        codes.put(CODE_3, context.getString(R.string.code_3));
        // 距离过近；错误提示文案：距离过近，请调整拍摄距离
        codes.put(CODE_4, context.getString(R.string.code_4));
        //有残缺；错误提示文案：试纸不全，请保持全部试纸处在取景框内
        codes.put(CODE_5, context.getString(R.string.code_5));
        // 神经网络处理错误；错误提示文案：算法处理中，请稍候
        codes.put(CODE_6, context.getString(R.string.code_6));
        // 两张试纸；错误提示文案：一次只能拍摄一张试纸
        codes.put(CODE_7, context.getString(R.string.code_7));
        // 曝光不足；错误提示文案：光线太暗，请调整光线或打开闪光灯后拍摄
        codes.put(CODE_8, context.getString(R.string.code_8));
        // 曝光过度；错误提示文案：光线太强，请调整光线或关掉闪光灯后拍摄
        codes.put(CODE_9, context.getString(R.string.code_9));
        // 试纸局部过度曝光；错误提示文案：局部光线太强，请调整光线或关掉闪光灯后拍摄
        codes.put(CODE_10, context.getString(R.string.code_10));
        // 画面模糊；错误提示文案：画面模糊，请保持手机稳定或重新对焦
        codes.put(CODE_11, context.getString(R.string.code_11));
        // 曝光不足；错误提示文案：光线太暗，请调整光线或打开闪光灯后拍摄
        codes.put(CODE_12, context.getString(R.string.code_12));
        //视频流扫描超时
        codes.put(CODE_17, context.getString(R.string.code_17));
        /// 手动裁剪失败，请检查传入的图片和坐标点
        codes.put(CODE_50, context.getString(R.string.code_50));
        //抠图成功，分析成功，但是用户取消确认抠图和分析结果。
        codes.put(CODE_201, context.getString(R.string.code_201));
        //抠图成功，分析成功，但未检测到参考线，请确认试纸有参考线显示
        codes.put(CODE_202, context.getString(R.string.code_202));
        codes.put(CODE_203, context.getString(R.string.code_203));
        //抠图成功，但是分析失败
        codes.put(CODE_101, context.getString(R.string.code_101));
        //SDK 校验失败或无效
        codes.put(CODE_SDK_ERROR, context.getString(R.string.code_sdk_error));
        //网络错误
        codes.put(CODE_NET_ERROR, context.getString(R.string.code_net_error));

        codes.put(CODE_501, context.getString(R.string.code_501));
        codes.put(CODE_503, context.getString(R.string.code_503));
        codes.put(CODE_500, context.getString(R.string.code_500));
        codes.put(CODE_502, context.getString(R.string.code_502));
        codes.put(CODE_301, context.getString(R.string.code_301));
        codes.put(CODE_302, context.getString(R.string.code_302));
        codes.put(CODE_303, context.getString(R.string.code_303));
        codes.put(CODE_304, context.getString(R.string.code_304));
        codes.put(CODE_510, context.getString(R.string.code_510));
        codes.put(CODE_511, context.getString(R.string.code_511));
    }


    public static String getMessage(int code) {
        if (codes.containsKey(code)) {
            return codes.get(code);
        }
        return "Unknown Error";
    }
}
