package app.stacq.plan.util.constants

object WorkerConstants {
    const val SYNC: String = "sync"
    const val GENERATE_TASK: String = "generate_task"
    const val UPDATE_GOAL_PROGRESS: String = "update_goal_progress"

    object TAG {
        const val CATEGORY_SYNC: String = "category_sync"
        const val GOAL_SYNC: String = "goal_sync"
        const val GOAL_GENERATE_TASK: String = "goal_generate_task"
        const val GOAL_PROGRESS: String = "goal_progress"
    }
}