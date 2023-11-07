package app.stacq.plan.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.stacq.plan.data.source.local.category.CategoryDao
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.goal.GoalDao
import app.stacq.plan.data.source.local.goal.GoalEntity
import app.stacq.plan.data.source.local.task.TaskDao
import app.stacq.plan.data.source.local.task.TaskEntity


@Database(
    entities = [TaskEntity::class, CategoryEntity::class, GoalEntity::class],
    version = 6,
    exportSchema = false
)
abstract class PlanDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

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
                    )
                        .addMigrations(MIGRATION_2_3)
                        .addMigrations(MIGRATION_3_4)
                        .addMigrations(MIGRATION_4_5)
                        .addMigrations(MIGRATION_5_6)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

private const val PLAN_DATABASE = "plan_database"

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE task ADD COLUMN notes TEXT DEFAULT ''")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS bite")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE task ADD COLUMN started_at INTEGER DEFAULT 0")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE task DROP COLUMN completed")
        database.execSQL("ALTER TABLE goal DROP COLUMN completed")
        database.execSQL("ALTER TABLE goal RENAME COLUMN completedAt TO completed_at")
    }
}
