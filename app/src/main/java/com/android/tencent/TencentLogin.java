package com.android.tencent;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 腾讯登录
 * 此类主要处理QQ登录逻辑，包含授权登录缓存授权，用户如果需要每次重新授权
 * 登录，需要自己设置是否缓存授权，或者删除之前的授权信息，重新登录即可。
 */
public class TencentLogin extends TencentSDK {

    public final String TAG = "TencentLogin";
    /**
     * 获取范围 - 用户数据
     */
    public static final String SCOPE_GET_USER_INFO = "get_user_info";
    /**
     * 获取范围 - 所有数据
     */
    public static final String SCOPE_ALL = "all";
    /**
     * 登录当前页面
     */
    public final Activity activity;
    /**
     * 数据范围
     */
    public final String scope;
    /**
     * 缓存授权
     */
    public final boolean cacheAuth;
    /**
     * 登录监听
     */
    private final OnTencentLoginListener listener;
    /**
     * 腾讯SDK
     */
    private Tencent tencent;
    /**
     * 请求步骤
     */
    private int step = 0;
    /**
     * 用户数据
     */
    private TencentUser user;

    /**
     * 登录构造函数
     *
     * @param builder
     */
    public TencentLogin(Builder builder) {
        this.activity = builder.activity;
        this.scope = builder.scope;
        this.listener = builder.listener;
        this.cacheAuth = builder.cacheAuth;
        tencent = TencentSDK.tencent();
        logout();
        login();
    }

    /**
     * 登录
     */
    private void login() {
        step = 0;
        if (cacheAuth && enableCacheAuth()) {
            step = 1;
            String token = TencentStorage.with(activity).getString("token", "");
            String expires_in = TencentStorage.with(activity).getString("expires_in", "");
            String openId = TencentStorage.with(activity).getString("openId", "");
            user = new TencentUser();
            tencent.setAccessToken(token, expires_in);
            tencent.setOpenId(openId);
            UserInfo userInfo = new UserInfo(activity, tencent.getQQToken());
            userInfo.getUserInfo(this);
        } else {
            if (listener != null) {
                listener.onTencentLogin(STATUS_START, ERROR_NONE, "START", user);
            }
            tencent.login(activity, scope, this);
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        tencent.logout(activity);
    }

    @Override
    public void onComplete(Object o) {
        JSONObject response = (JSONObject) o;
        Log.i(TAG, "->" + getClass().getSimpleName() + " step = " + step + " " + response.toString().length() + " " + response.toString());
        if (response.toString().length() == 0) {
            return;
        }
        if (step == 0) {
            String token = response.optString(Constants.PARAM_ACCESS_TOKEN);
            String expires_in = response.optString(Constants.PARAM_EXPIRES_IN);
            String openId = response.optString(Constants.PARAM_OPEN_ID);
            String expires_time = response.optString(Constants.PARAM_EXPIRES_TIME);
            user = new TencentUser();
            user.setOpenid(openId);
            user.setAccess_token(token);
            user.setExpires_in(expires_in);
            user.setExpires_time(expires_time);
            //获取用户信息
            step = 1;
            if (cacheAuth) {
                cacheAuth(token, expires_in, expires_time, openId);
            }
            tencent.setAccessToken(token, expires_in);
            tencent.setOpenId(openId);
            UserInfo userInfo = new UserInfo(activity, tencent.getQQToken());
            userInfo.getUserInfo(this);
            if (listener != null) {
                listener.onTencentLogin(STATUS_GET_TOKEN, ERROR_NONE, "GET_TOKEN", user);
            }
        }
        if (step == 1) {
            String province = response.optString("province");
            String city = response.optString("city");
            String nickname = response.optString("nickname");
            String gender = response.optString("gender");
            String gender_type = response.optString("gender_type");
            String head = response.optString("figureurl_qq");
            user.setProvince(province);
            user.setCity(city);
            user.setNickname(nickname);
            user.setGender(gender);
            user.setGender_type(gender_type);
            user.setHead(head);
            if (listener != null && user.getNickname().length() != 0) {
                listener.onTencentLogin(STATUS_SUCCEED, ERROR_NONE, "SUCCEED", user);
            }
        }
    }

    @Override
    public void onError(UiError uiError) {
        if (listener != null) {
            listener.onTencentLogin(STATUS_ERROR, uiError.errorCode, uiError.errorMessage, user);
        }
    }

    @Override
    public void onCancel() {
        if (listener != null) {
            listener.onTencentLogin(STATUS_CANCEL, ERROR_CANCEL, "CANCEL", user);
        }
    }

    /**
     * 缓存授权
     *
     * @param token
     * @param expires_in
     * @param expires_time
     * @param openId
     */
    public void cacheAuth(String token, String expires_in, String expires_time, String openId) {
        TencentStorage.with(activity).put("token", token);
        TencentStorage.with(activity).put("expires_in", expires_in);
        TencentStorage.with(activity).put("expires_time", expires_time);
        TencentStorage.with(activity).put("openId", openId);
    }

    /**
     * 删除授权
     *
     * @param context
     */
    public static void deleteAuth(Context context) {
        TencentStorage.with(context).put("token", "");
        TencentStorage.with(context).put("expires_in", "");
        TencentStorage.with(context).put("expires_time", "");
        TencentStorage.with(context).put("openId", "");
    }

    /**
     * 授权是否可用
     *
     * @return
     */
    public boolean enableCacheAuth() {
        String token = TencentStorage.with(activity).getString("token", "");
        if (token.length() == 0) {
            return false;
        }
        String expires_time = TencentStorage.with(activity).getString("expires_time", "");
        Date date = new Date(Long.parseLong(expires_time));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i(TAG, "->enableCacheAuth expires_time:" + format.format(date));
        long time = System.currentTimeMillis() - date.getTime();
        if (time > 0) {
            return false;
        }
        return true;
    }

    public static class Builder {

        /**
         * 登录页面
         */
        private Activity activity;
        /**
         * 登录数据获取范围
         */
        private String scope = SCOPE_GET_USER_INFO;
        /**
         * 登录监听
         */
        private OnTencentLoginListener listener;
        /**
         * 缓存授权
         */
        private boolean cacheAuth;

        public Builder() {

        }

        /**
         * 构建者
         * @param activity 登录页面
         */
        public Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 登录页面
         * @return
         */
        public Activity activity() {
            return activity;
        }

        /**
         * 设置登录页面
         * @param activity
         * @return
         */
        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        /**
         * 登录数据获取范围
         * @return
         */
        public String scope() {
            return scope;
        }

        /**
         * 设置登录数据获取范围
         * @param scope
         * @return
         */
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /**
         * 登录监听
         * @return
         */
        public OnTencentLoginListener listener() {
            return listener;
        }

        /**
         * 设置登录监听
         * @param listener
         * @return
         */
        public Builder listener(OnTencentLoginListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 是否缓存授权
         * @return
         */
        public boolean isCacheAuth() {
            return cacheAuth;
        }

        /**
         * 设置是否缓存授权
         * @param cacheAuth
         * @return
         */
        public Builder cacheAuth(boolean cacheAuth) {
            this.cacheAuth = cacheAuth;
            return this;
        }

        /**
         * 构建登录对象
         * @return
         */
        public TencentLogin build() {
            return new TencentLogin(this);
        }

    }


}
