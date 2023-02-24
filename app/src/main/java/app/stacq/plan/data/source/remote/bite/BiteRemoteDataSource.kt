package app.stacq.plan.data.source.remote.bite


interface BiteRemoteDataSource {

    suspend fun create(biteDocument: BiteDocument)

    suspend fun update(biteDocument: BiteDocument)
}