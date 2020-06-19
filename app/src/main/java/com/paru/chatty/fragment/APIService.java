package com.paru.chatty.fragment;

import com.paru.chatty.Notifications.MyResponse;
import com.paru.chatty.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA54G6GN0:APA91bHc1IYm9EQnQ0yNtRrSbPvFVbKAZRe1nzz18G0EKlQGvli7F-0lk6EsUZbEOhSpOkPyDtPoZj9QFkFACJ7O9RlE8SuWROOw7Hd8gt7FMvV0U2v7cFVkbNt03ueEIdVpNTjNfd7s"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
