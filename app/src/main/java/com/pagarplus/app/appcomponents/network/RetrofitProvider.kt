package com.pagarplus.app.appcomponents.network

import com.google.gson.GsonBuilder
import kotlin.String
import com.pagarplus.app.BuildConfig
import com.pagarplus.app.network.BASE_URL
import com.pagarplus.app.network.RetrofitServices
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * The provider class which used to get Retrofit Service
 */
class RetrofitProvider {

    var gson=GsonBuilder().setLenient().create()
    private fun provideRetrofit(serviceBaseUrl: String, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(serviceBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    public fun getService(): RetrofitServices =
        provideRetrofit(BASE_URL,provideOkHttpClient()).create(RetrofitServices::class.java)

    /**
     * method which returns [OkHttpClient] used to build retrofit service
     * @return [OkHttpClient]
     */
    private fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .callTimeout(5, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }
}