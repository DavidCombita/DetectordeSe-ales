package com.softyoumobileapp.detectorseales.di

import com.softyoumobileapp.detectorseales.data.datasource.IDataSourceNetwork
import com.softyoumobileapp.detectorseales.data.repository.IRepository
import com.softyoumobileapp.detectorseales.data.repository.RepositoryImple
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(datasource: IDataSourceNetwork): IRepository = RepositoryImple(datasource)
}