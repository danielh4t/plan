package app.stacq.plan.di.data.sources.local.category

import app.stacq.plan.data.source.local.bite.BiteDao
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.local.category.CategoryDao
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CategoryLocalDataSourceModule {

    @Provides
    fun provideCategoryLocalDataSource(categoryDao: CategoryDao): CategoryLocalDataSource {
        return CategoryLocalDataSourceImpl(categoryDao)
    }
}
