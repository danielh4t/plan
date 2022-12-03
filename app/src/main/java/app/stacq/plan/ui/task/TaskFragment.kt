package app.stacq.plan.ui.task

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTaskBinding
import app.stacq.plan.util.isFinishAtInFuture


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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = TaskFragmentArgs.fromBundle(requireArguments())
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

        val biteLocalDataSource = BiteLocalDataSource(database.biteDao())
        val biteRemoteDataSource = BiteRemoteDataSource()
        val biteRepository = BiteRepository(biteLocalDataSource, biteRemoteDataSource)

        viewModelFactory = TaskViewModelFactory(taskRepository, biteRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val bitesAdapter = BitesAdapter(viewModel)
        binding.bitesList.adapter = bitesAdapter

        viewModel.bites.observe(viewLifecycleOwner) {
            it?.let {
                bitesAdapter.submitList(it)
            }
        }

        binding.editTaskButton.setOnClickListener {
            val task: Task = viewModel.task.value!!
            val action = TaskFragmentDirections.actionNavTaskToNavEdit(task)
            this.findNavController().navigate(action)
        }

        binding.cloneTaskButton.setOnClickListener {
            viewModel.clone()
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.deleteTaskButton.setOnClickListener {
            val task: Task = viewModel.task.value!!
            viewModel.delete(task)
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.timerFab.setOnClickListener {
            val task: Task = viewModel.task.value!!
            val notify: Boolean = hasPostNotificationsPermission()
            val isFinishAtInFuture: Boolean = isFinishAtInFuture(task.timerFinishAt)
            if (!notify and isFinishAtInFuture) {
                val action = TaskFragmentDirections.actionNavTaskToNavNotification(task)
                this.findNavController().navigate(action)
            } else {
                val action = TaskFragmentDirections.actionNavTaskToNavTimer(task, notify)
                this.findNavController().navigate(action)
            }
        }

        binding.createBiteFab.setOnClickListener {
            val action = TaskFragmentDirections.actionNavTaskToCreateBiteFragment(taskId)
            this.findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Handles request for app post notification permission
     */
    private fun hasPostNotificationsPermission(): Boolean {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.

                return false
            }
            else -> {
                return false
            }
        }
    }

}