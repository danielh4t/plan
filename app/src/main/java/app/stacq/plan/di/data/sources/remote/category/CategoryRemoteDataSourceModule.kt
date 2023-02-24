package app.stacq.plan.di.data.sources.remote.category

import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
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
class CategoryRemoteDataSourceModule {

    @Provides
    fun provideCategoryRemoteDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): CategoryRemoteDataSource {
        return CategoryRemoteDataSourceImpl(firebaseAuth, firestore, ioDispatcher)
    }
}
