package com.example.letterbox.Fragments;

import com.example.letterbox.Notifications.MyResponse;
import com.example.letterbox.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA8M8Qvy0:APA91bEySLoV3dbJkQVe1TW6se1XyV1hAlKh4GSiOnuX4ZJ98H6dajiNqitQJe773BO8XHqFGewLAfp9ErGFVzRiZ-D2_TcWy5K-mYeXQEd8GknIfI8VA_oZE9SVNqdV1D0BixwgCnG5"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
