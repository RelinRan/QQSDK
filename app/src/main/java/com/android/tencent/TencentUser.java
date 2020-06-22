package com.android.tencent;

public class TencentUser {

    private String openid;
    private String expires_time;
    private String expires_in;
    private String access_token;

    private String province;
    private String city;
    private String nickname;
    private String gender;
    private String gender_type;
    private String head;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getExpires_time() {
        return expires_time;
    }

    public void setExpires_time(String expires_time) {
        this.expires_time = expires_time;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender_type() {
        return gender_type;
    }

    public void setGender_type(String gender_type) {
        this.gender_type = gender_type;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "TencentUser{" +
                "openid='" + openid + '\'' +
                ", expires_time='" + expires_time + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", access_token='" + access_token + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", gender_type='" + gender_type + '\'' +
                ", head='" + head + '\'' +
                '}';
    }
}
