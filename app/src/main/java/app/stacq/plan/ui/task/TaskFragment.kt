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
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTaskBinding
import kotlinx.coroutines.Dispatchers

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null

    private val binding get() = _binding!!

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
        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val taskViewModelFactory = TaskViewModelFactory(tasksRepository, taskId)
        val taskViewModel = ViewModelProvider(this, taskViewModelFactory)[TaskViewModel::class.java]
        binding.viewmodel = taskViewModel
        binding.lifecycleOwner = this

        taskViewModel.task.observe(viewLifecycleOwner) { task ->
            // task is deleted navigate to tasks
            task ?: run {
                val action = TaskFragmentDirections.actionNavTaskToNavTasks()
                this.findNavController().navigate(action)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}