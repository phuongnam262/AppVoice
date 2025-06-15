package gmo.demo.voidtask.data.network

import gmo.demo.voidtask.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppAPI {
    /*Retrofit setting base*/
    companion object {
        operator fun invoke(
            retrofit: Retrofit
        ): AppAPI {
            return retrofit.create(AppAPI::class.java)
        }

        fun createRetrofit(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): Retrofit {

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .addInterceptor(AuthInterceptor(true))
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

}