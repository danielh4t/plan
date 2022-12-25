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
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTaskBinding
import app.stacq.plan.ui.timer.cancelAlarm
import app.stacq.plan.util.millisInFuture


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


        val biteCompleteListener = BiteCompleteListener { viewModel.completeBite(it) }
        val biteDeleteListener = BiteDeleteListener { viewModel.deleteBite(it) }

        val bitesAdapter = BitesAdapter(biteCompleteListener, biteDeleteListener)
        binding.bitesList.adapter = bitesAdapter

        viewModel.bites.observe(viewLifecycleOwner) {
            it?.let {
                bitesAdapter.submitList(it)
            }
        }

        binding.editTaskButton.setOnClickListener {
            val action = TaskFragmentDirections.actionNavTaskToNavEdit(taskId)
            this.findNavController().navigate(action)
        }

        binding.cloneTaskButton.setOnClickListener {
            viewModel.clone()
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.deleteTaskButton.setOnClickListener {
            viewModel.delete()
            // cancel alarm
            val hasAlarm = viewModel.hasAlarm()
            if (hasAlarm) {
                // finish_at
                val requestCode: Int = viewModel.taskFinishAt()
                val name = viewModel.taskName()
                cancelAlarm(application, requestCode, name)
            }
            val action = TaskFragmentDirections.actionNavTaskToNavTasks()
            this.findNavController().navigate(action)
        }

        binding.prioritySlider.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            viewModel.updatePriority(value)
        }

        binding.timerFab.setOnClickListener {
            val task: Task = viewModel.task.value!!
            val canPostNotifications: Boolean = hasPostNotificationsPermission(application)
            val isFinishAtInFuture: Boolean = millisInFuture(task.timerFinishAt) > 0L
            if (!canPostNotifications and isFinishAtInFuture) {
                // request permission
                val action = TaskFragmentDirections.actionNavTaskToNavNotification(taskId)
                this.findNavController().navigate(action)
            } else {
                val action = TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
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
    private fun hasPostNotificationsPermission(applicationContext: Context): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Explain to the user why your app requires this permission
                return false
            }
            else -> {
                return false
            }
        }
    }

}