package app.stacq.plan.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.ui.MarginItemDecoration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TasksViewModelFactory
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTasksBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val remoteDataSource = TasksRemoteDataSource(Firebase.firestore, Dispatchers.IO)

        val tasksRepository = TasksRepository(localDataSource, remoteDataSource, Dispatchers.IO)


        viewModelFactory = TasksViewModelFactory(tasksRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val taskAdapter = TaskAdapter(viewModel)
        binding.tasksList.adapter = taskAdapter
        binding.tasksList.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin))
        )

        binding.createFab.setOnClickListener {
            val action = TasksFragmentDirections.actionNavTasksToNavCreate()
            this.findNavController().navigate(action)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            it?.let {
                taskAdapter.submitList(it)
            }
        }

        viewModel.navigateTask.observe(viewLifecycleOwner) { taskId ->
            taskId?.let {
                val action = TasksFragmentDirections.actionNavTasksToNavTask(taskId)
                this.findNavController().navigate(action)
                viewModel.closeTask()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}