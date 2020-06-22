package com.android.tencent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * SDK初始化操作等
 */
public class TencentSDK implements IUiListener {

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_START = 0;
    public static final int STATUS_GET_TOKEN = 1;
    public static final int STATUS_SUCCEED = 2;
    public static final int STATUS_CANCEL = 3;
    public static final int ERROR_NONE = -10;
    public static final int ERROR_JSON = -11;
    public static final int ERROR_PARAMS = -6;
    public static final int ERROR_CANCEL = -12;
    /**
     * 平台分配的应用ID
     */
    private static String appId;
    /**
     * SDK
     */
    private static Tencent tencent;
    /**
     * SDK操作对象
     */
    private static TencentSDK sdk;

    public TencentSDK() {
        sdk = this;
    }

    /**
     * 初始化SDK
     *
     * @param context 上下文对象
     * @param appId   QQ平台ID
     */
    public static void init(Context context, String appId) {
        TencentSDK.appId = appId;
        tencent = Tencent.createInstance(appId, context.getApplicationContext());
    }

    /**
     * 平台分配的应用ID
     *
     * @return
     */
    public static String appId() {
        return appId;
    }

    /**
     * SDK
     *
     * @return
     */
    public static Tencent tencent() {
        return tencent;
    }

    /**
     * 退出登录
     */
    public static void logout(Activity activity) {
        if (tencent == null) {
            return;
        }
        tencent.logout(activity);
    }

    /**
     * 删除授权
     *
     * @param context
     */
    public static void deleteAuth(Context context) {
        TencentLogin.deleteAuth(context);
    }

    /**
     * 处理结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, sdk);
        }
    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

}
