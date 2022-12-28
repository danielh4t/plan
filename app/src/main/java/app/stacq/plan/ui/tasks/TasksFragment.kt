package app.stacq.plan.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.ui.MarginItemDecoration
import kotlinx.coroutines.launch


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TasksViewModelFactory
    private lateinit var viewModel: TasksViewModel

    private var hasCategories: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
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

        viewModelFactory = TasksViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val taskNavigateListener = TaskNavigateListener {
            val action = TasksFragmentDirections.actionNavTasksToNavTask(it)
            this.findNavController().navigate(action)
        }

        val taskCompleteListener = TaskCompleteListener { viewModel.complete(it) }

        val tasksAdapter = TasksAdapter(taskNavigateListener, taskCompleteListener)

        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))

        binding.createTaskFab.setOnClickListener {
            if (hasCategories) {
                // Navigate to create task
                val action = TasksFragmentDirections.actionNavTasksToNavCreate()
                this.findNavController().navigate(action)
            } else {
                // Navigate to create category first
                val action = TasksFragmentDirections.actionNavTasksToNavCreateCategory()
                this.findNavController().navigate(action)
            }
        }

        viewModel.tasksCategory.observe(viewLifecycleOwner) {
            it?.let {
                tasksAdapter.submitList(it)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                hasCategories = it.isNotEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}