package edu.ucne.wilmercastillo_ap2_p2.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import edu.ucne.wilmercastillo_ap2_p2.data.remote.RepositoryingApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL_Repository = "https://api.github.com/"

    @Provides
    @Singleton
    fun providesMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesRepositoryingApi(moshi: Moshi) : RepositoryingApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_Repository)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RepositoryingApi::class.java)

    }
}