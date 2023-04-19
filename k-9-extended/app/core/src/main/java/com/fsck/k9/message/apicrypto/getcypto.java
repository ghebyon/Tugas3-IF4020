package com.fsck.k9.message.apicrypto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface getcypto {

    @GET("encrypt/{message}")
    Call<encrypt> getEncrypt(@Path("message") String message, @Query("key") String key);

    @GET("decrypt/{message}")
    Call<decrypt> getDecrypt(@Path("message") String message, @Query("key") String key);
}
