package app.stacq.plan.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.category.CategoryDao
import app.stacq.plan.data.source.local.task.TaskDao

@Database(entities = [Task::class, Category::class], version = 1, exportSchema = false)
abstract class PlanDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

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
                    )
                        .createFromAsset("database/${PLAN_DATABASE}.db")
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }

    }

}

private const val PLAN_DATABASE = "plan_database"