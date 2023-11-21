package com.softyoumobileapp.detectorseales.data.datasource

import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import java.io.File

interface IDataSourceNetwork {
    suspend fun predictImage(imagePart: File): SignalTransit?

}