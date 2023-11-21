package com.softyoumobileapp.detectorseales.di

import com.softyoumobileapp.detectorseales.data.datasource.IDataSourceNetwork
import com.softyoumobileapp.detectorseales.data.repository.IRepository
import com.softyoumobileapp.detectorseales.data.repository.RepositoryImple
import com.softyoumobileapp.detectorseales.domain.PredictUseCase
import com.softyoumobileapp.detectorseales.view.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideUseCase(repository: IRepository): PredictUseCase = PredictUseCase(repository)

    @Provides
    @Singleton
    fun provideViewModel(usecase: PredictUseCase): MainViewModel = MainViewModel(usecase)

}