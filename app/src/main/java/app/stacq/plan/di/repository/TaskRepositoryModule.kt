package app.stacq.plan.di.repository

import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TaskRepositoryModule {

    @Provides
    fun provideTaskRepository(
        taskLocalDataSource: TaskLocalDataSource,
        taskRemoteDataSource: TaskRemoteDataSource
    ): TaskRepository {
        return TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)
    }
}
