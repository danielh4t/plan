package app.stacq.plan.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.stacq.plan.data.source.local.bite.BiteDao
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.local.category.CategoryDao
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.goal.GoalDao
import app.stacq.plan.data.source.local.goal.GoalEntity
import app.stacq.plan.data.source.local.task.TaskDao
import app.stacq.plan.data.source.local.task.TaskEntity


@Database(
    entities = [TaskEntity::class, CategoryEntity::class, BiteEntity::class, GoalEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PlanDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

    abstract fun biteDao(): BiteDao

    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: PlanDatabase? = null

        fun getDatabase(context: Context): PlanDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlanDatabase::class.java,
                        PLAN_DATABASE
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

private const val PLAN_DATABASE = "plan_database"