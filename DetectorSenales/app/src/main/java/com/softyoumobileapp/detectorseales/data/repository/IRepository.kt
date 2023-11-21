package com.softyoumobileapp.detectorseales.data.repository

import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import kotlinx.coroutines.flow.Flow
import java.io.File

interface IRepository {
    fun predict(array: File): Flow<SignalTransit>
}