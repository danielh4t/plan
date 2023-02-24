package app.stacq.plan.di.data.sources.local.task

import app.stacq.plan.data.source.local.task.TaskDao
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TaskLocalDataSourceModule {

    @Provides
    fun provideTaskLocalDataSource(taskDao: TaskDao): TaskLocalDataSource {
        return TaskLocalDataSourceImpl(taskDao)
    }
}
