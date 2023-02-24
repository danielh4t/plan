package app.stacq.plan.ui.createCategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.databinding.FragmentCreateCategoryBinding
import app.stacq.plan.util.defaultColors
import com.google.android.material.snackbar.Snackbar

class CreateCategoryFragment : Fragment() {

    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CreateCategoryViewModelFactory
    private lateinit var viewModel: CreateCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val categoryLocalDataSourceImpl = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSourceImpl()
        val categoryRepositoryImpl =
            CategoryRepositoryImpl(categoryLocalDataSourceImpl, categoryRemoteDataSource)

        viewModelFactory = CreateCategoryViewModelFactory(categoryRepositoryImpl)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateCategoryViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.createFab.setOnClickListener { clickedView ->
            val name: String = binding.name.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_category_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val color = getString(defaultColors(name))
            viewModel.create(name, color)

            val action = CreateCategoryFragmentDirections.actionNavCategoryToNavCategories()
            this.findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}