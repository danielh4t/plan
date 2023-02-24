package app.stacq.plan.di.data.sources.remote.bite

import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSourceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class BiteRemoteDataSourceModule {

    @Provides
    fun provideBiteRemoteDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): BiteRemoteDataSource {
        return BiteRemoteDataSourceImpl(firebaseAuth, firestore, ioDispatcher)
    }
}
