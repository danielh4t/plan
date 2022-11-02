package app.stacq.plan.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.databinding.FragmentCategoryBinding
import com.google.android.material.snackbar.Snackbar

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

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CategoryViewModelFactory(categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CategoryViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.createFab.setOnClickListener { clickedView ->
            val categoryName: String = binding.name.text.toString()
            if (categoryName.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_category_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            viewModel.create(categoryName)


            val previous = this.findNavController().previousBackStackEntry?.destination?.id
            if (previous == R.id.nav_categories) {
                val action = CategoryFragmentDirections.actionNavCategoryToNavCategories()
                this.findNavController().navigate(action)
            } else {
                val action = CategoryFragmentDirections.actionNavCategoryToNavCreate()
                this.findNavController().navigate(action)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}