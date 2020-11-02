package com.andrstudy.lecture.fcmtest.myfriend.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NetworkService {
    @POST("/android/")
    Call<Data> talk(@Body Data data);
}
