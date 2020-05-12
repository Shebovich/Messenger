package com.example.mess;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.io.Serializable;

public class SessionManager implements Serializable {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    private int counter = 0;
    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLanguageUser(String languageUser){
        editor.putString("language",languageUser);
        editor.commit();
    }
    public String getLanguageUser(){
      return   pref.getString("language","en");
    }

    public void setOffsetChannels (int offsetChannels){
        editor.putInt("offset",offsetChannels);
        editor.commit();
    }
    public int getOffsetChannels(){
        return   pref.getInt("offset",0);
    }
    public void setLog(String text){
        counter++;
        editor.putString("log "+counter, text);
        editor.commit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

    }
    public void setBaseUrl(String url){
        editor.putString("baseUrl",url);
        editor.commit();
    }
    public String getBaseUrl(){
        return pref.getString("baseUrl","https://chat.okbtsp.com/");
    }

    public void setLastChannelId(String channelId){
        editor.putString("lastChannelId",channelId);
        editor.commit();
    }
    public String getLastChannelId(){
        return pref.getString("lastChannelId", "lastChannelId");
    }
    public void setToken(String token){
        editor.putString("token",token);
        editor.commit();
    }
    public String getToken(){
        return pref.getString("token", "no token");
    }


    public void setImage(String image){
        editor.putString("image",image);
        editor.commit();
    }


    public String  getUserProfileImage(){
        return pref.getString("image","noimage");
    }
    public String getUserName(){
        return pref.getString("username", "no token");
    }
    public void setUsename(String usename) {
        editor.putString("username", usename);
        editor.commit();
    }



    public void setTeamId(String teamId){
        editor.putString("teamId",teamId);
        editor.commit();
    }
    public String getTeamId(){
        return pref.getString("teamId", "no teamId");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public void setUserId(String userId){
        editor.putString("user_id",userId);
        editor.commit();
    }
    public String getUserId(){
        return pref.getString("user_id", "no user_id");
    }public void setMail(String mail){
        editor.putString("mail",mail);
        editor.commit();
    }
    public String getMail(){
        return pref.getString("mail", "noMail");
    }
    public void setFirstName(String mail){
        editor.putString("first_name",mail);
        editor.commit();
    }
    public String getFirstName(){
        return pref.getString("first_name", "noName");
    }
    public void setLastName(String mail){
        editor.putString("lastName",mail);
        editor.commit();
    }
    public String getLastName(){
        return pref.getString("lastName", "noLastName");
    }

    public void setVibtrate(boolean isVibrate){
        editor.putBoolean("isVibrate",isVibrate);
        editor.commit();
    }
    public boolean isVibrate(){
        return pref.getBoolean("isVibrate", true);
    }


    public void setSound(boolean isSound){
        editor.putBoolean("isSound",isSound);
        editor.commit();
    }
    public boolean isSound(){
        return pref.getBoolean("isSound", true);
    }


    public void setFirebaseToken(String firebaseToken){
        editor.putString("firebaseToken",firebaseToken);
        editor.commit();
    }
    public String getFirebaseToken(){
        return pref.getString("firebaseToken", "nofirebaseToken");
    }


    public void deleteValues() {
        editor.remove("token");
        editor.remove("image");
        editor.remove("username");
        editor.remove("first_name");
        editor.remove("lastName");
        editor.remove("mail");
        editor.remove("offset");
        editor.remove("user_id");
        editor.remove("teamId");
        editor.remove("firebaseToken");
        editor.apply();


    }
    public void setDarkTheme(boolean darkTheme){
        editor.putBoolean("darkTheme",darkTheme);
        editor.commit();
    }
    public boolean isDarkTheme(){
        return pref.getBoolean("darkTheme", false);
    }

    public void setDeviceIdle(boolean b) {
        editor.putBoolean("isIdle",b);
        editor.commit();
    }

    public boolean isDeviceIdle(){
        return pref.getBoolean("isIdle",false);
    }

}