package com.softyoumobileapp.detectorseales.data.repository

import com.softyoumobileapp.detectorseales.data.datasource.IDataSourceNetwork
import com.softyoumobileapp.detectorseales.data.models.SignalTransit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class RepositoryImple @Inject constructor(
    val dataSource: IDataSourceNetwork
): IRepository {
    override fun predict(array: File): Flow<SignalTransit> = flow {
        dataSource.predictImage(array)?.let { emit(it) }
    }.flowOn(Dispatchers.IO)

}