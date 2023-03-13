package app.stacq.plan.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.databinding.FragmentCategoriesBinding
import app.stacq.plan.util.ui.MarginItemDecoration
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CategoriesViewModelFactory
    private lateinit var viewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CategoriesViewModelFactory(categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CategoriesViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.categoriesAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()

        val categoryEnableListener = CategoryEnableListener { viewModel.updateEnabled(it) }

        val categoryNavigateListener = CategoryNavigateListener {
            val action = CategoriesFragmentDirections.actionNavCategoriesToNavCategory(it)
            navController.navigate(action)
        }

        val adapter = CategoriesAdapter(categoryEnableListener, categoryNavigateListener)

        binding.categoriesList.adapter = adapter
        binding.categoriesList.addItemDecoration(
            MarginItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.list_margin
                )
            )
        )

        binding.addCategoryFab.setOnClickListener {
            val action = CategoriesFragmentDirections.actionNavCategoriesToNavCategoryModify(null)
            navController.navigate(action)
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}