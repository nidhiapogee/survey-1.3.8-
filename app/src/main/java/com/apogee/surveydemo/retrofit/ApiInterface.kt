package com.apogee.surveydemo.retrofit



import com.apogee.surveydemo.retrofitModel.HelloImageModel
import com.apogee.surveydemo.retrofitModel.HelloModel
import com.apogee.surveydemo.retrofitModel.TelegramModel
import com.apogee.surveydemo.retrofitModel.TelegramResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {
    @POST("hello")
    fun hello(@Body body: HelloModel): Call<String>

    @POST("helloImage")
    fun helloImage(@Body body: HelloImageModel) : Call<String>

    @Headers(
        "X-WM-CLIENT-ID: chandansingh23396@gmail.com",
        "X-WM-CLIENT-SECRET: 3ae49e46cfe748bb83c39b5a47594353")
   @POST("v1/telegram/single/message/5")
   fun sendTelegarm(@Body body: TelegramModel) : Call<TelegramResponse>


}