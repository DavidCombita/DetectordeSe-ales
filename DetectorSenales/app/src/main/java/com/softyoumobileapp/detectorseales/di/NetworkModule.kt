package com.softyoumobileapp.detectorseales.di

import com.softyoumobileapp.detectorseales.data.datasource.IDataSourceNetwork
import com.softyoumobileapp.detectorseales.data.datasource.retrofit.ApiPredict
import com.softyoumobileapp.detectorseales.data.datasource.retrofit.RetrofitSignalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = "http://20.38.42.108:8000/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiPredict::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiPredict: ApiPredict): IDataSourceNetwork = RetrofitSignalDataSource(apiPredict)

}