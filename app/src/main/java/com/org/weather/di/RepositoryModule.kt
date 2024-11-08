package com.org.weather.di

import com.org.weather.data.remote.api.WeatherApi
import com.org.weather.data.remote.datasource.DataSource
import com.org.weather.data.remote.datasource.RemoteDataSource
import com.org.weather.data.repository.WeatherRepositoryImpl
import com.org.weather.domain.repository.WeatherRepository
import com.org.weather.domain.usecase.UseCase
import com.org.weather.domain.usecase.WeatherUseCase
import com.org.weather.presentation.ui.DefaultDispatcher
import com.org.weather.presentation.ui.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRemoteDataSource(weatherApi: WeatherApi): DataSource {
        return RemoteDataSource(weatherApi)
    }

    @Provides
    fun provideRepository(remoteDataSource: RemoteDataSource): WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource)
    }

    @Provides
    fun provideUseCase(weatherRepository: WeatherRepository): UseCase {
        return WeatherUseCase(weatherRepository)
    }

    @Provides
    fun provideDispatcher(): DispatcherProvider{
        return DefaultDispatcher()
    }
}