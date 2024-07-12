package app.stacq.plan.data.source.remote.category

interface CategoryRemoteDataSource {
    suspend fun create(categoryDocument: CategoryDocument)

    suspend fun update(categoryDocument: CategoryDocument)

    suspend fun delete(categoryDocument: CategoryDocument)

    suspend fun getCategoryDocuments(): List<CategoryDocument>
}