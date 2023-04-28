package app.stacq.plan.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentCategoryBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CategoryViewModelFactory
    private lateinit var viewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CategoryFragmentArgs.fromBundle(requireArguments())
        val categoryId: String = args.categoryId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CategoryViewModelFactory(categoryRepository, categoryId)
        viewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

        binding.categoryAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.categoryAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.categoryAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_category -> {
                    val action =
                        CategoryFragmentDirections.actionNavCategoryToNavCategoryModify(categoryId)
                    navController.navigate(action)
                    true
                }
                else -> false
            }
        }

        viewModel.category.observe(viewLifecycleOwner) {
            it?.let {
                binding.category = it
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}