package app.stacq.plan.ui.editTask

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
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentEditTaskBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class EditTaskFragment : Fragment() {

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: EditTaskViewModelFactory
    private lateinit var viewModel: EditTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        val task: Task = args.task

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource()

        val taskRepository = TaskRepository(localDataSource, remoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = EditTaskViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditTaskViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.editName.setText(task.name)

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categories.map { category ->
                val chip = layoutInflater.inflate(
                    R.layout.chip_layout,
                    binding.editCategoryChipGroup,
                    false
                ) as Chip
                chip.text = category.name
                chip.tag = category.id
                if (category.id == task.categoryId) {
                    chip.isChecked = true
                }
                binding.editCategoryChipGroup.addView(chip)
            }
        }

        binding.editFab.setOnClickListener { clickedView ->
            val name: String = binding.editName.text.toString()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_task_details, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            // get checked chip
            val checkedId: Int = binding.editCategoryChipGroup.checkedChipId
            val checkedChip = binding.editCategoryChipGroup.findViewById<Chip>(checkedId)
            // get category id from chip tag
            val categoryId = checkedChip.tag as String

            viewModel.editTask(task, name, categoryId)

            val action = EditTaskFragmentDirections.actionNavEditToNavTasks()
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}