package app.stacq.plan.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.PlanApiService
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTaskBinding
import kotlinx.coroutines.Dispatchers

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TaskViewModelFactory
    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        val args = TaskFragmentArgs.fromBundle(requireArguments())
        val taskId = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val remoteDataSource = TasksRemoteDataSource(PlanApiService.planApiService, Dispatchers.Main)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource,Dispatchers.Main)

        viewModelFactory = TaskViewModelFactory(tasksRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.task.observe(viewLifecycleOwner) { task ->
            // task is deleted navigate to tasks
            task ?: run {
                val action = TaskFragmentDirections.actionNavTaskToNavTasks()
                this.findNavController().navigate(action)
            }
        }

        binding.editButton.setOnClickListener {
            val action = TaskFragmentDirections.actionNavTaskToEditFragment(taskId)
            this.findNavController().navigate(action)
        }

        binding.completedButton.setOnClickListener {
            viewModel.complete()
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}