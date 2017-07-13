package com.creadigol.drivehere.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class PreferenceSettings {

    private String TAG = "CashTag";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public PreferenceSettings(Context context) {
        sp = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
        editor = sp.edit();
    }

    private String PRE_IS_LOGIN = "is_login";
    private String userId = "user_id";
    private String userName= "user_name";
    private String password= "password";
    private String email= "email";


    private String serverAppVersion = "serverAppVersion";

    public int getServerAppVersion() {
        return sp.getInt(serverAppVersion, 0);
    }

    public void setServerAppVersion(int serverAppVersion) {
        editor.putInt(this.serverAppVersion, serverAppVersion).commit();
    }

    public void setIslogin(boolean flag) {
        editor.putBoolean(PRE_IS_LOGIN, flag).commit();
    }

    public boolean getIsLogin() {
        return sp.getBoolean(PRE_IS_LOGIN, false);
    }

    public String getUserId() {
        return sp.getString(this.userId, "0"); // change default user id in PreferenceSettings when login is working in app
    }
    public String getUserName() {
        return sp.getString(this.userName, ""); // change default user id in PreferenceSettings when login is working in app
    }

    public void setUserName(String userName) {
        editor.putString(this.userName, userName).commit();
    }
    public String getPassword() {
        return sp.getString(this.password, ""); // change default user id in PreferenceSettings when login is working in app
    }

    public void setPassword(String password) {
        editor.putString(this.password, password).commit();
    }

    public String getEmail() {
        return sp.getString(this.email, ""); // change default user id in PreferenceSettings when login is working in app
    }

    public void setEmail(String email) {
        editor.putString(this.email, email).commit();
    }
    public void setUserId(String userId) {
        editor.putString(this.userId, userId).commit();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }
}
