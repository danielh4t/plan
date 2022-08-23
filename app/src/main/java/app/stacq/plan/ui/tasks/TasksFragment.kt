package app.stacq.plan.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.ViewModelFactory
import kotlinx.coroutines.Dispatchers

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val viewModelFactory = ViewModelFactory(tasksRepository)
        val tasksViewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        val taskAdapter = TaskAdapter(tasksViewModel)
        tasksViewModel.tasks.observe(viewLifecycleOwner) {
            it?.let {
                taskAdapter.submitList(it)
            }
        }

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasks, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = tasksViewModel
        binding.taskList.adapter = taskAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}