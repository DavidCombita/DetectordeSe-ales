package com.softyoumobileapp.detectorseales.data.datasource.retrofit

import com.softyoumobileapp.detectorseales.data.datasource.IDataSourceNetwork
import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RetrofitSignalDataSource @Inject constructor(
        private val apiPredict: ApiPredict
    ): IDataSourceNetwork {
    override suspend fun predictImage(imagePart: File): SignalTransit? {
        // Construir el cuerpo de la solicitud
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imagePart)

        // Crear la parte del formulario con el archivo
        val filePart = MultipartBody.Part.createFormData("file", imagePart.name, requestBody)
        return apiPredict.predictImage(filePart).body()
    }

}