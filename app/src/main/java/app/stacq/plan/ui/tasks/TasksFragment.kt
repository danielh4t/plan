package app.stacq.plan.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource

import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.ui.MarginItemDecoration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)
        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource()
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource)

        viewModelFactory = TasksViewModelFactory(tasksRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val tasksAdapter = TasksAdapter(viewModel)
        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))
        ItemTouchHelper(taskItemTouchHelperCallback).attachToRecyclerView(binding.tasksList)

        binding.createFab.setOnClickListener {
            val action = TasksFragmentDirections.actionNavTasksToNavCreate()
            this.findNavController().navigate(action)
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            it?.let {
                tasksAdapter.submitList(it)
            }
        }

        viewModel.navigateTask.observe(viewLifecycleOwner) { taskId ->
            taskId?.let {
                val action = TasksFragmentDirections.actionNavTasksToNavTask(taskId)
                this.findNavController().navigate(action)
                viewModel.closeTask()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}