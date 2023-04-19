package com.fsck.k9.message.apicrypto

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val BASE_URL =
        "http://192.168.1.13:5000/" //Ganti dengan Base URL pada masing-masing device

    val instace: getcypto by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(getcypto::class.java)
    }
}
