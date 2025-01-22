package com.gamay.chatapp;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ImgurService {
    @Multipart
    @POST("image")
    Call<ImgurResponse> uploadImage(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image
    );
}
