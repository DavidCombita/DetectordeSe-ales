package com.softyoumobileapp.detectorseales.data.datasource.retrofit

import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface ApiPredict {

    @Multipart
    @POST("/predict/")
    suspend fun predictImage(
        @Part filePart: MultipartBody.Part
    ): Response<SignalTransit>

}