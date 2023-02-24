package app.stacq.plan.di.repository

import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CategoryRepositoryModule {

    @Provides
    fun provideCategoryRepository(
        categoryLocalDataSource: CategoryLocalDataSource,
        categoryRemoteDataSource: CategoryRemoteDataSource
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)
    }
}
