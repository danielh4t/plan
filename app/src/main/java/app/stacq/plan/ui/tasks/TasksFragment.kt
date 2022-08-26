package app.stacq.plan.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.MarginItemDecoration
import kotlinx.coroutines.Dispatchers

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tasks, container, false)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val taskViewModelFactory = TasksViewModelFactory(tasksRepository)
        val tasksViewModel =
            ViewModelProvider(this, taskViewModelFactory)[TasksViewModel::class.java]
        binding.viewmodel = tasksViewModel
        binding.lifecycleOwner = this

        val taskAdapter = TaskAdapter(tasksViewModel)
        binding.taskList.adapter = taskAdapter
        binding.taskList.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin))
        )

        tasksViewModel.tasks.observe(viewLifecycleOwner) {
            it?.let {
                taskAdapter.submitList(it)
            }
        }

        tasksViewModel.navigateTask.observe(viewLifecycleOwner) { taskId ->
            taskId?.let {
                val action = TasksFragmentDirections.actionNavTasksToNavTask(taskId)
                this.findNavController().navigate(action)
                tasksViewModel.closeTask()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}