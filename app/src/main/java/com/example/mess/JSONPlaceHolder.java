package com.example.mess;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface JSONPlaceHolder {
@POST("/fcm/send")
    public Call<ResponseBody> sendPushMessage(@Body RequestBody jsonObject , @Header("Authorization") String auth);

}
