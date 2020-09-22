# TencentSDK
基于官方SDK进行集成，因此名字还是使用官方，主要方便开发者QQ登录和分享的使用。

## 方法一  ARR依赖
[TencentSDK.arr](https://github.com/RelinRan/TencentSDK/blob/master/QQSDK.aar)
```
android {
    ....
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    implementation 'com.android.support:design:26.+'   26.+版本根据自己的设置不能低于26
    implementation 'com.android.support:recyclerview-v7:26.+'//26.+版本根据自己的设置不能低于26
    implementation(name: 'QQSDK', ext: 'aar')
}

```

## 方法二   JitPack依赖
### A.项目/build.grade
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### B.项目/app/build.grade
```
	dependencies {
	        implementation 'com.github.RelinRan:SDK:1.0.0'
	        implementation 'com.android.support:design:26.+'
	        implementation 'com.android.support:recyclerview-v7:26.+'
	}
```

## AndroidManifest.xml配置
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <!-- SDK2.1新增获取用户位置信息 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.GET_TASKS"/>
    
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:usesCleartextTraffic="true"
        android:theme="@style/AppTheme">    
        <uses-library android:name="org.apache.http.legacy" android:required="false" />
        <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:noHistory="true"
            android:launchMode="singleTask" >
               <intent-filter>
                    <action android:name="android.intent.action.VIEW" />
                    <category android:name="android.intent.category.DEFAULT" />
                    <category android:name="android.intent.category.BROWSABLE" />
                    <data android:scheme="tencent101650216" />
                </intent-filter>
            </activity>
            <activity
                android:name="com.tencent.connect.common.AssistActivity"
                android:screenOrientation="behind"
                android:theme="@android:style/Theme.Translucent.NoTitleBar"
                android:configChanges="orientation|keyboardHidden">
            </activity>
    </application>

```

## Application配置
```
    @Override
    public void onCreate() {
        super.onCreate();
         //初始化操作，APP_ID换为对应APP的ID
         TencentSDK.init(this,"APP_ID");
    }

```
## Activity配置
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TencentSDK.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

```
## QQ登录
```
        TencentLogin.Builder builder = new TencentLogin.Builder(this);
        //如果不需要每次授权就true，需要每次授权就false
        builder.cacheAuth(true);
        builder.listener(new OnTencentLoginListener() {
            @Override
            public void onTencentLogin(int status, int error, String describe, TencentUser user) {
                if (status == TencentSDK.STATUS_SUCCEED) {
                
                }
                if (status == TencentSDK.STATUS_CANCEL) {
                
                }
            }
        });
        builder.build();
```
## APP退出
```         
        TencentSDK.logout(this);
        TencentSDK.deleteAuth(this);
```
## QQ分享
目前只支持常用的图文分享、纯图片分享、音乐分享，如果是其他类型建议使用图文分享。
注意：如果是分享到QQ空间，只需要设置shareBuilder.extInt(TencentShare.EXNT_QZONE);
### A.图文分享
```
        TencentShare.Builder shareBuilder = new TencentShare.Builder(this);
        //如果是分享到QQ空间，只需要设置shareBuilder.extInt(TencentShare.EXNT_QZONE);
        shareBuilder.extInt(TencentShare.EXNT_MESSAGE);
        shareBuilder.title("标题");
        shareBuilder.summary("描述信息");
        shareBuilder.imageUrl("http:imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        shareBuilder.targetUrl("http:www.qq.com/news/1.html");
        shareBuilder.listener(new OnTencentShareListener() {
            @Override
            public void onTencentShare(int status, int error, String describe) {
                if (status==TencentSDK.STATUS_SUCCEED){
            
                }
                if (status==TencentSDK.STATUS_CANCEL){
                
                }
        });
        shareBuilder.build();
```
### B.纯图片分享
```
        TencentShare.Builder shareBuilder = new TencentShare.Builder(this);
        //如果是分享到QQ空间，只需要设置shareBuilder.extInt(TencentShare.EXNT_QZONE);
        shareBuilder.extInt(TencentShare.EXNT_MESSAGE);
        shareBuilder.imageLocalUrl(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ImageSelector/IMG20190917203727.jpg");
        shareBuilder.listener(new OnTencentShareListener() {
            @Override
            public void onTencentShare(int status, int error, String describe) {
                if (status==TencentSDK.STATUS_SUCCEED){
            
                }
                if (status==TencentSDK.STATUS_CANCEL){
                
                }
            }
        });
        shareBuilder.build();
```
### C.音乐分享
```
        TencentShare.Builder shareBuilder = new TencentShare.Builder(this);
        //如果是分享到QQ空间，只需要设置shareBuilder.extInt(TencentShare.EXNT_QZONE);
        shareBuilder.extInt(TencentShare.EXNT_MESSAGE);
        shareBuilder.title("标题");
        shareBuilder.summary("描述信息");
        shareBuilder.imageUrl("http:imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        shareBuilder.targetUrl("http:music.163.com/song/media/outer/url?id=5231448.mp3");
        shareBuilder.audioUrl("http:music.163.com/song/media/outer/url?id=5231448.mp3");
        shareBuilder.listener(new OnTencentShareListener() {
            @Override
            public void onTencentShare(int status, int error, String describe) {
                if (status==TencentSDK.STATUS_SUCCEED){
            
                }
                if (status==TencentSDK.STATUS_CANCEL){
                
                }
            }
        });
        shareBuilder.build();
```
