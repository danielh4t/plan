package app.stacq.plan.di.data.sources.local.bite

import app.stacq.plan.data.source.local.bite.BiteDao
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class BiteLocalDataSourceModule {

    @Provides
    fun provideBiteLocalDataSource(biteDao: BiteDao): BiteLocalDataSource {
        return BiteLocalDataSourceImpl(biteDao)
    }
}
