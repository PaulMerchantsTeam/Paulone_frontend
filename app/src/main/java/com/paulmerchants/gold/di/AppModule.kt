package com.paulmerchants.gold.di


import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.paulmerchants.gold.BuildConfig
import com.paulmerchants.gold.remote.ApiParams
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun cache(): Cache {
        val httpCacheDirectory =
            File(ExternalPreferredCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR, "coin-cache")
        return Cache(
            httpCacheDirectory,
            ExternalPreferredCacheDiskCacheFactory.DEFAULT_DISK_CACHE_SIZE.toLong()
        )
    }
   /* var trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls<X509Certificate>(0)
        }
    }
    )*/
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache
    ): OkHttpClient = if (BuildConfig.DEBUG) {
       /* val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())*/
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
       /* .sslSocketFactory(
            sslContext.getSocketFactory(),
            trustAllCerts.get(0) as X509TrustManager
        )
            .hostnameVerifier { hostname, session -> true }*/
    .cache(cache)
            .build()
    } else OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.MINUTES)
        .connectTimeout(10, TimeUnit.MINUTES)
        .cache(cache)
        .build()


    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder().setLenient().create()


    @Provides
    @Singleton
    fun providesRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiParams =
         retrofit.create(ApiParams::class.java)

}