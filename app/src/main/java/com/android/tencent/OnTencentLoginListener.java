package com.android.tencent;

/**
 * 腾讯登录监听
 */
public interface OnTencentLoginListener {

    /**
     * 登录结果
     *
     * @param status   状态{@link TencentSDK#STATUS_SUCCEED}
     * @param error    错误代码{@link TencentSDK#ERROR_CANCEL}
     * @param describe 描述信息
     * @param user     用户信息
     */
    void onTencentLogin(int status, int error, String describe, TencentUser user);

}
