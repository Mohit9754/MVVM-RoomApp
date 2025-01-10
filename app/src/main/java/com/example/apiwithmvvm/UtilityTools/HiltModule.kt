package com.example.apiwithmvvm.UtilityTools

import android.content.Context
import com.example.apiwithmvvm.Retrofit.ApiService
import com.example.apiwithmvvm.Retrofit.RetrofitClient
import com.example.apiwithmvvm.activity.login.LoginRepository
import com.example.apiwithmvvm.activity.otp.OtpRepository
import com.example.apiwithmvvm.activity.signup.SignUpRepository
import com.example.apiwithmvvm.activity.splash.SplashRepository
import com.example.apiwithmvvm.database.UserDao
import com.example.apiwithmvvm.database.UserDatabase
import com.example.apiwithmvvm.database.UserRepository
import com.example.apiwithmvvm.fragment.more.MoreRepository
import com.example.apiwithmvvm.fragment.profile.ProfileRepository
import com.example.apiwithmvvm.validation.Validation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return UserDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(userDatabase: UserDatabase): UserDao {
        return userDatabase.getUserDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.client
    }

    @Provides
    @Singleton
    fun provideLoginRepository(
        userRepository: UserRepository,
        apiService: ApiService
    ): LoginRepository {
        return LoginRepository(userRepository, apiService)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(apiService: ApiService): SignUpRepository {
        return SignUpRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideOtpRepository(apiService: ApiService): OtpRepository {
        return OtpRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSplashRepository(userRepository: UserRepository): SplashRepository {
        return SplashRepository(userRepository)
    }

    @Provides
    @Singleton
    fun provideMoreRepository(
        userRepository: UserRepository,
        apiService: ApiService
    ): MoreRepository {
        return MoreRepository(userRepository, apiService)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        userRepository: UserRepository,
        apiService: ApiService
    ): ProfileRepository {
        return ProfileRepository(userRepository, apiService)
    }

    @Provides
    @Singleton
    fun provideValidation(@ApplicationContext context: Context): Validation {
        return Validation(context)
    }

}