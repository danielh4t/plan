package app.stacq.plan.ui.task

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTaskBinding
import app.stacq.plan.util.createTimerChannel
import app.stacq.plan.util.ui.BottomMarginItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.slider.Slider
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
                R.id.edit_task -> {
                    val action = TaskFragmentDirections.actionNavTaskToNavEdit(taskId)
                    navController.navigate(action)
                    true
                }

                R.id.clone_task -> {
                    viewModel.clone()
                    Toast.makeText(requireContext(), R.string.task_cloned, Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.timer_task -> {
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
                                    val action =
                                        TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
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
                    true
                }

                else -> false
            }
        }

        val biteCompleteListener = BiteCompleteListener { viewModel.completeBite(it) }

        val biteNavigateListener = BiteNavigateListener {
            val action = TaskFragmentDirections.actionNavTaskToNavBiteModify(taskId, it)
            navController.navigate(action)
        }

        val biteDeleteListener = BiteDeleteListener { bite ->
            viewModel.deleteBite(bite)
            Snackbar.make(view, R.string.bite_deleted, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.createBiteFab)
                .setAction(R.string.undo) {
                    viewModel.createBite(bite)
                }
                .show()
        }

        val adapter = BitesAdapter(biteCompleteListener, biteNavigateListener, biteDeleteListener)

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.adapterPosition
                val item = adapter.currentList[position]
                item?.let { bite ->
                    viewModel.deleteBite(bite)
                    Snackbar.make(view, R.string.bite_deleted, Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.createBiteFab)
                        .setAction(R.string.undo) {
                            viewModel.createBite(bite)
                        }
                        .show()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {

                    // Set the background color to red
                    val background = ColorDrawable(Color.RED)

                    // Set the bounds of the background
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                    // Draw the background
                    background.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.taskBitesList)

        binding.taskBitesList.adapter = adapter

        binding.taskBitesList.addItemDecoration(
            BottomMarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin_compact))
        )

        viewModel.task.observe(viewLifecycleOwner) {
            it?.let {
                binding.task = it
            }
        }

        viewModel.bites.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
                binding.bitesCount = it.count()
            }
        }

        binding.taskPrioritySlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.updatePriority(slider.value)
            }
        })


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
                ).setAnchorView(binding.createBiteFab)
                    .show()
            } else {
                // Permission denied.
                viewModel.logPermission(false)
                // Explain to the user that the notification feature unavailable.
                Snackbar.make(
                    binding.createBiteFab,
                    R.string.timer_complete_no_notification,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.createBiteFab)
                    .show()
            }
        }
}