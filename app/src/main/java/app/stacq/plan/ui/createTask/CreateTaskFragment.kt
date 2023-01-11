package app.stacq.plan.ui.createTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentCreateTaskBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CreateTaskViewModelFactory
    private lateinit var viewModel: CreateTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CreateTaskViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.createCategoryChipGroup.removeAllViews()
            categories?.let {
                it.map { category ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        binding.createCategoryChipGroup,
                        false
                    ) as Chip
                    chip.text = category.name
                    chip.contentDescription = getString(R.string.content_category_chip, category.name)
                    chip.tag = category.id
                    binding.createCategoryChipGroup.addView(chip)
                }
            }
        }

        binding.createFab.setOnClickListener { clickedView ->
            val name: String = binding.name.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.task_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedId: Int = binding.createCategoryChipGroup.checkedChipId
            if (checkedId == View.NO_ID) {
                Snackbar.make(clickedView, R.string.empty_category_details, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedChip = binding.createCategoryChipGroup.findViewById<Chip>(checkedId)
            val categoryId = checkedChip.tag as String

            viewModel.create(name, categoryId)

            val action = CreateTaskFragmentDirections.actionNavCreateTaskToNavTasks()
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}