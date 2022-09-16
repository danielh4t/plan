package app.stacq.plan.data.source.remote

import app.stacq.plan.BuildConfig
import app.stacq.plan.data.source.remote.network.NetworkResultCallAdapterFactory
import app.stacq.plan.data.source.remote.task.PlanApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


private var BASE_URL = if (BuildConfig.DEBUG) BuildConfig.plandebugapi else BuildConfig.planapi

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
    .build()


object PlanApiService {
    val planApiService: PlanApiService by lazy { retrofit.create(PlanApiService::class.java) }
}


