package com.apogee.surveydemo.retrofit


import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class ApiClient {
   var BASE_URL = "http://120.138.10.251:8080/meter_survey/api/";
   //var BASE_URL = "http://120.138.10.251:8086/api/"
    var retrofit: Retrofit? = null
    var okHttpClient: OkHttpClient = OkHttpClient()

    var gson = GsonBuilder()
            .setLenient()
            .create()

    fun getClient(): Retrofit {

        okHttpClient =  OkHttpClient.Builder()
                .connectTimeout(240, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(240, TimeUnit.SECONDS) // write timeout
                .readTimeout(240, TimeUnit.SECONDS) // read timeout
                .build()
           if(retrofit == null){
               retrofit = Retrofit.Builder()
                   .baseUrl(BASE_URL)
                   .client(okHttpClient)
                       .addConverterFactory(ScalarsConverterFactory.create())
                   .addConverterFactory(GsonConverterFactory.create())
                   .build()
           }


        return retrofit!!
    }

    val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }
}
