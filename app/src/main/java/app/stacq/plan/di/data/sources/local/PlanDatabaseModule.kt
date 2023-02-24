package app.stacq.plan.di.data.sources.local

import android.content.Context
import androidx.room.Room
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteDao
import app.stacq.plan.data.source.local.category.CategoryDao
import app.stacq.plan.data.source.local.task.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlanDatabaseModule {

    @Singleton
    @Provides
    fun providePlanDatabase(@ApplicationContext context: Context): PlanDatabase {
        return Room.databaseBuilder(
            context,
            PlanDatabase::class.java,
            "plan_database"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: PlanDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideBiteDao(database: PlanDatabase): BiteDao {
        return database.biteDao()
    }

    @Provides
    fun provideCategoryDao(database: PlanDatabase): CategoryDao {
        return database.categoryDao()
    }
}
