package app.stacq.plan.di.repository

import app.stacq.plan.data.repository.bite.BiteRepository
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class BiteRepositoryModule {

    @Provides
    fun provideBiteRepository(
        biteLocalDataSource: BiteLocalDataSource,
        biteRemoteDataSource: BiteRemoteDataSource
    ): BiteRepository {
        return BiteRepositoryImpl(biteLocalDataSource, biteRemoteDataSource)
    }
}
