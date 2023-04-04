package app.stacq.plan.ui.task

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTaskBinding
import app.stacq.plan.ui.timer.cancelAlarm
import app.stacq.plan.util.createTimerChannel
import app.stacq.plan.util.ui.MarginItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val biteLocalDataSource = BiteLocalDataSourceImpl(database.biteDao())
        val biteRemoteDataSource = BiteRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val biteRepository = BiteRepositoryImpl(biteLocalDataSource, biteRemoteDataSource)

        viewModelFactory = TaskViewModelFactory(taskRepository, biteRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner

        binding.taskAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.taskAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.taskAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    val action = TaskFragmentDirections.actionNavTaskToNavEdit(taskId)
                    navController.navigate(action)
                    true
                }
                R.id.clone -> {
                    viewModel.clone()
                    Snackbar.make(view, R.string.task_cloned, Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.timerFab)
                        .show()

                    true
                }
                R.id.delete -> {
                    viewModel.delete()
                    // cancel alarm
                    val hasAlarm = viewModel.hasAlarm()
                    if (hasAlarm) {
                        // finish_at
                        viewModel.task.value?.let {
                            val name = it.name
                            val requestCode: Int = it.timerFinishAt.toInt()
                            cancelAlarm(application, requestCode, name)
                        }
                    }

                    Snackbar.make(view, R.string.task_deleted, Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.timerFab)
                        .setAction(R.string.undo) {
                            viewModel.undoDelete()
                        }
                        .show()

                    val action = TaskFragmentDirections.actionNavTaskToNavTasks()
                    navController.navigate(action)
                    true
                }
                else -> false
            }
        }

        val biteCompleteListener = BiteCompleteListener { viewModel.completeBite(it) }
        val biteDeleteListener = BiteDeleteListener {
            viewModel.deleteBite(it)
            true
        }
        val biteNavigateListener = BiteNavigateListener {
            val action = TaskFragmentDirections.actionNavTaskToNavBiteModify(taskId, it)
            navController.navigate(action)
        }

        val bitesAdapter =
            BitesAdapter(biteCompleteListener, biteDeleteListener, biteNavigateListener)
        binding.taskBitesList.adapter = bitesAdapter
        binding.taskBitesList.addItemDecoration(
            MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin_compact))
        )

        viewModel.task.observe(viewLifecycleOwner) {
            it?.let {
                binding.task = it
            }
        }

        viewModel.bites.observe(viewLifecycleOwner) {
            it?.let {
                bitesAdapter.submitList(it)
            }
        }

        binding.taskPrioritySlider.addOnChangeListener { _, value, _ ->
            viewModel.updatePriority(value)
        }

        binding.timerFab.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted
                    // You can use the API that requires the permission.
                    val action = TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
                    navController.navigate(action)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Additional rationale should be displayed
                    // Explain to the user why your app requires this permission
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.timer_complete_notification))
                        .setMessage(resources.getString(R.string.timer_notification_message))
                        .setNegativeButton(resources.getString(R.string.no_thanks)) { _, _ ->
                            // Respond to negative button press
                            Toast.makeText(
                                requireContext(),
                                R.string.no_notification,
                                Toast.LENGTH_SHORT
                            ).show()
                            val action = TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
                            navController.navigate(action)
                        }
                        .setPositiveButton(resources.getString(R.string.yes_please)) { _, _ ->
                            // Create Channel
                            createTimerChannel(requireNotNull(this.activity).application.applicationContext)
                            // Respond to positive button press
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        .show()
                }
                else -> {
                    // Create Channel
                    createTimerChannel(requireNotNull(this.activity).application.applicationContext)
                    // Permission has not be requested yet
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }

        binding.createBiteFab.setOnClickListener {
            val action = TaskFragmentDirections.actionNavTaskToNavBiteModify(taskId, null)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Handles the user's response to the system permissions dialog.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted.
                viewModel.logPermission(true)
                Snackbar.make(
                    binding.createBiteFab,
                    R.string.timer_complete_yes_notification,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.timerFab)
                    .show()
            } else {
                // Permission denied.
                viewModel.logPermission(false)
                // Explain to the user that the notification feature unavailable.
                Snackbar.make(
                    binding.createBiteFab,
                    R.string.timer_complete_no_notification,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.timerFab)
                    .show()
            }
        }
}