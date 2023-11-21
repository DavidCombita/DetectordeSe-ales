package com.softyoumobileapp.detectorseales.domain

import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import com.softyoumobileapp.detectorseales.data.repository.IRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class PredictUseCase @Inject constructor(
    private var repository: IRepository
) {
    operator fun invoke(array: File): Flow<SignalTransit> {
        return repository.predict(array)
    }
}