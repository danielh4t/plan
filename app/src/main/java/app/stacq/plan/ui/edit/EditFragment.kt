package app.stacq.plan.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.PlanApiService
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentEditBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: EditViewModelFactory
    private lateinit var viewModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditBinding.inflate(inflater, container, false)

        val args = EditFragmentArgs.fromBundle(requireArguments())
        val taskId = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val categoryLocalDataSource =
            CategoryLocalDataSource(database.categoryDao(), Dispatchers.Main)

        val remoteDataSource = TasksRemoteDataSource(PlanApiService.planApiService, Dispatchers.Main)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource,Dispatchers.Main)
        val categoryRepository = CategoryRepository(categoryLocalDataSource, Dispatchers.Main)

        viewModelFactory = EditViewModelFactory(tasksRepository, categoryRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.task.observe(viewLifecycleOwner) { task ->
            binding.editTitle.setText(task.title)
            binding.editCategory.setText(task.categoryName)
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                val categories = it.map { category -> category.name }
                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, categories)
                binding.editCategory.setAdapter(arrayAdapter)
            }
        }

        binding.editFab.setOnClickListener { view ->
            val title: String = binding.editTitle.text.toString()
            val categoryName: String = binding.editCategory.text.toString()

            if (title.isEmpty() or categoryName.isEmpty()) {
                Snackbar.make(view, R.string.empty_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(view)
                    .show()
                return@setOnClickListener
            }

            val categories: List<Category>? = viewModel.categories.value
            val category: Category? = categories?.firstOrNull { it.name == categoryName }
            if (category != null) {
                viewModel.editTask(taskId, title, category.id)
            }
            val action = EditFragmentDirections.actionNavEditToNavTasks()
            this.findNavController().navigate(action)

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}