package app.stacq.plan.ui.task

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.data.model.TaskCategory

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
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val remoteDataSource =
            TasksRemoteDataSource(PlanApiService.planApiService, Dispatchers.Main)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource, Dispatchers.Main)

        viewModelFactory = TaskViewModelFactory(tasksRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.editTaskButton.setOnClickListener {
            val action = TaskFragmentDirections.actionNavTaskToNavEdit(taskId)
            this.findNavController().navigate(action)
        }

        binding.completeTaskButton.setOnClickListener {
            viewModel.completed()
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.deleteTaskButton.setOnClickListener {
            viewModel.delete()
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.taskTimerFab.setOnClickListener {
            this.context?.let { it1 -> handleScheduleExactAlarmPermission(it1) }
            val task: TaskCategory = viewModel.task.value!!
            val action = TaskFragmentDirections.actionNavTaskToNavTimer(task)
            this.findNavController().navigate(action)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun handleScheduleExactAlarmPermission(context: Context) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return
            }
            shouldShowRequestPermissionRationale(Manifest.permission.SCHEDULE_EXACT_ALARM) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextDialog()
        }
        }
    }

    private fun showInContextDialog() {
        TODO("Not yet implemented")
    }
}