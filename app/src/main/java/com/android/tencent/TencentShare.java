package com.android.tencent;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * QQ分享
 */
public class TencentShare implements IUiListener {
    /**
     * 日志标识
     */
    public static String TAG = "TencentShare";
    /**
     * 图文消息分享
     */
    public static final int EXNT_MESSAGE = QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE;
    /**
     * 图片分享到空间
     */
    public static final int EXNT_QZONE = QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN;
    /**
     * 上下文对象
     */
    public final Activity activity;
    /**
     * 分享的标题, 最长30个字符(必填)
     */
    public final String title;
    /**
     * 分享的消息摘要，最长40个字(可选)
     */
    public final String summary;
    /**
     * 消息被好友点击后的跳转URL(必填)
     */
    public final String targetUrl;
    /**
     * 分享图片的URL或者本地路径(可选)
     */
    public final String imageUrl;
    /**
     * 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替(可选)
     */
    public final String appName;
    /**
     * 分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
     */
    public final int extInt;
    /**
     * 需要分享的本地图片路径,(必选，纯图片分享使用)
     */
    public final String imageLocalUrl;
    /**
     * 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。
     */
    public final String audioUrl;
    /**
     * 分享监听
     */
    public final OnTencentShareListener listener;
    /**
     * 腾讯SDK
     */
    private Tencent tencent;
    /**
     * 分享对象
     */
    private static TencentShare share;


    public TencentShare(Builder builder) {
        this.activity = builder.activity;
        this.title = builder.title;
        this.summary = builder.summary;
        this.targetUrl = builder.targetUrl;
        this.imageUrl = builder.imageUrl;
        this.appName = builder.appName;
        this.extInt = builder.extInt;
        this.imageLocalUrl = builder.imageLocalUrl;
        this.audioUrl = builder.audioUrl;
        this.listener = builder.listener;
        tencent = TencentSDK.tencent();
        share = this;
        shareImageText();
        shareImage();
        shareAudio();
    }

    /**
     * 分享图文
     */
    private void shareImageText() {
        if (audioUrl != null) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName == null || appName.length() == 0 ? getAppName(activity) : appName);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        tencent.shareToQQ(activity, params, share);
    }

    /**
     * 分享图片
     */
    private void shareImage() {
        if (imageLocalUrl == null || imageLocalUrl.length() == 0) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageLocalUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName == null || appName.length() == 0 ? getAppName(activity) : appName);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        tencent.shareToQQ(activity, params, share);
    }

    /**
     * 分享音乐
     */
    private void shareAudio() {
        if (audioUrl == null || audioUrl.length() == 0) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName == null || appName.length() == 0 ? getAppName(activity) : appName);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        tencent.shareToQQ(activity, params, share);
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onComplete(Object o) {
        JSONObject response = (JSONObject) o;
        Log.i(TAG, "->" + getClass().getSimpleName() + " onComplete " + response.toString());
        if (listener != null) {
            listener.onTencentShare(TencentSDK.STATUS_SUCCEED, TencentSDK.ERROR_NONE, "SHARE_SUCCEED");
        }
    }

    @Override
    public void onError(UiError uiError) {
        Log.i(TAG, "->" + getClass().getSimpleName() + " onError " + " " + uiError.toString());
        if (listener != null) {
            listener.onTencentShare(TencentSDK.STATUS_ERROR, uiError.errorCode, uiError.errorMessage);
        }
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "->" + getClass().getSimpleName() + " onCancel ");
        if (listener != null) {
            listener.onTencentShare(TencentSDK.STATUS_CANCEL, TencentSDK.ERROR_NONE, "SHARE_CANCEL");
        }
    }

    public static class Builder {
        /**
         * 上下文对象
         */
        private Activity activity;
        /**
         * 分享的标题, 最长30个字符(必填)
         */
        private String title;
        /**
         * 分享的消息摘要，最长40个字(可选)
         */
        private String summary;
        /**
         * 消息被好友点击后的跳转URL(必填)
         */
        private String targetUrl;
        /**
         * 分享图片的URL或者本地路径(可选)
         */
        private String imageUrl;
        /**
         * 手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替(可选)
         */
        private String appName;
        /**
         * 分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
         */
        private int extInt;
        /**
         * 需要分享的本地图片路径,(必选，纯图片分享使用)
         */
        private String imageLocalUrl;
        /**
         * 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。
         */
        private String audioUrl;
        /**
         * 分享监听
         */
        private OnTencentShareListener listener;

        /**
         * 构建者
         *
         * @param activity 分享页面
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 分享页面
         *
         * @return
         */
        public Activity activity() {
            return activity;
        }

        /**
         * 分享的标题, 最长30个字符。
         *
         * @return
         */
        public String title() {
            return title;
        }

        /**
         * 设置分享的标题
         *
         * @param title 享的标题, 最长30个字符。
         * @return
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * 分享的消息摘要，最长40个字
         *
         * @return
         */
        public String summary() {
            return summary;
        }

        /**
         * 设置分享的消息摘要
         *
         * @param summary 分享的消息摘要，最长40个字
         * @return
         */
        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        /**
         * 这条分享消息被好友点击后的跳转URL
         *
         * @return
         */
        public String targetUrl() {
            return targetUrl;
        }

        /**
         * 设置这条分享消息被好友点击后的跳转URL
         *
         * @param targetUrl 跳转URL
         * @return
         */
        public Builder targetUrl(String targetUrl) {
            this.targetUrl = targetUrl;
            return this;
        }

        /**
         * 分享图片的URL或者本地路径
         *
         * @return
         */
        public String imageUrl() {
            return imageUrl;
        }

        /**
         * 设置分享图片的URL或者本地路径
         *
         * @param imageUrl 图片的URL或者本地路径
         * @return
         */
        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        /**
         * 分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
         *
         * @return
         */
        public int extInt() {
            return extInt;
        }

        /**
         * 分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
         * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
         *
         * @param extInt
         * @return
         */
        public Builder extInt(int extInt) {
            this.extInt = extInt;
            return this;
        }


        /**
         * 需要分享的本地图片路径
         *
         * @return
         */
        public String imageLocalUrl() {
            return imageLocalUrl;
        }

        /**
         * 设置需要分享的本地图片路径
         *
         * @param imageLocalUrl 本地图片路径
         * @return
         */
        public Builder imageLocalUrl(String imageLocalUrl) {
            this.imageLocalUrl = imageLocalUrl;
            return this;
        }

        /**
         * 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
         *
         * @return
         */
        public String audioUrl() {
            return imageLocalUrl;
        }

        /**
         * 设置音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
         *
         * @param imageLocalUrl 音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
         * @return
         */
        public Builder audioUrl(String imageLocalUrl) {
            this.imageLocalUrl = imageLocalUrl;
            return this;
        }

        /**
         * 分享监听
         *
         * @return
         */
        public OnTencentShareListener listener() {
            return listener;
        }

        /**
         * 设置分享监听
         *
         * @param listener
         * @return
         */
        public Builder listener(OnTencentShareListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 构建分享对象
         *
         * @return
         */
        public TencentShare build() {
            return new TencentShare(this);
        }

    }

}
