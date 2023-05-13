package app.stacq.plan.ui.categoryModify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import app.stacq.plan.databinding.FragmentCategoryModifyBinding
import app.stacq.plan.util.defaultColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryModifyFragment : Fragment() {

    private var _binding: FragmentCategoryModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CategoryModifyViewModelFactory
    private lateinit var viewModel: CategoryModifyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CategoryModifyFragmentArgs.fromBundle(requireArguments())
        val categoryId: String? = args.categoryId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CategoryModifyViewModelFactory(categoryRepository, categoryId)
        viewModel = ViewModelProvider(this, viewModelFactory)[CategoryModifyViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.categoryModifyAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.categoryModifyAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.categoryModifyAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_category -> {
                    val name: String = binding.categoryModifyNameEditText.text.toString().trim()
                    if (name.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            R.string.empty_category_details, Toast.LENGTH_SHORT
                        )
                            .show()
                        return@setOnMenuItemClickListener true
                    }

                    val color = getString(defaultColors(name))

                    val id = if (categoryId == null) {
                        viewModel.create(name, color)
                    } else {
                        viewModel.update(name)
                        categoryId
                    }

                    val action =
                        CategoryModifyFragmentDirections.actionNavCategoryModifyToNavCategory(id)
                    navController.navigate(action)

                    true
                }

                else -> false
            }
        }

        viewModel.category.observe(viewLifecycleOwner) { it ->
            it?.let {
                binding.categoryModifyNameEditText.setText(it.name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}