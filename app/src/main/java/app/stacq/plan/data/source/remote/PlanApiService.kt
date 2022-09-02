package app.stacq.plan.data.source.remote

import app.stacq.plan.BuildConfig
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.remote.task.PlanApiService
import com.google.firebase.inject.Deferred
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = BuildConfig.planApi

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


object PlanApiService {
    val planApiService: PlanApiService by lazy { retrofit.create(PlanApiService::class.java) }
}

