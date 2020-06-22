package com.android.tencent;

/**
 * 腾讯分享监听
 */
public interface OnTencentShareListener {

    /**
     * 分享结果
     *
     * @param status   状态{@link TencentSDK#STATUS_SUCCEED}
     * @param error    错误代码{@link TencentSDK#ERROR_CANCEL}
     * @param describe 描述信息
     */
    void onTencentShare(int status, int error, String describe);

}
