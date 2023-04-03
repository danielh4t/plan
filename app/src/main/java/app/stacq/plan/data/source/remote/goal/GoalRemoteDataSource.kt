package app.stacq.plan.data.source.remote.goal

interface GoalRemoteDataSource {

    suspend fun create(goalDocument: GoalDocument)

    suspend fun update(goalDocument: GoalDocument)

    suspend fun updateCategory(goalDocument: GoalDocument, previousCategoryId: String)

    suspend fun getGoalDocuments(): List<GoalDocument>
}