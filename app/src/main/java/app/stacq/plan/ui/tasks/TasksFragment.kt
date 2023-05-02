package app.stacq.plan.ui.tasks

import android.app.Activity
import android.content.IntentSender
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.stacq.plan.MainNavDirections
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.ui.timer.cancelAlarm
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.launchSignIn
import app.stacq.plan.util.time.TimeUtil
import app.stacq.plan.util.ui.BottomMarginItemDecoration
import app.stacq.plan.worker.CategorySyncWorker
import app.stacq.plan.worker.GenerateTaskWorker
import app.stacq.plan.worker.GoalProgressWorker
import app.stacq.plan.worker.GoalSyncWorker
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TasksViewModelFactory
    private lateinit var viewModel: TasksViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var oneTapClient: SignInClient
    private var showOneTapUI: Boolean = true
    private var hasCategories: Boolean = false

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

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = TasksViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]

        val navController = findNavController()

        oneTapClient = Identity.getSignInClient(requireActivity())
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                if (user.photoUrl !== null) {
                    binding.tasksAccountImageView.load(user.photoUrl) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.tasksAccountImageView))
                        transformations(CircleCropTransformation())
                    }
                }
            } else {
                // signed out
                binding.tasksAccountImageView.setImageResource(R.drawable.ic_account_circle)
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.tasksAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        binding.tasksAccountImageView.setOnClickListener {
            val message = if (Firebase.auth.currentUser == null) {
                resources.getString(R.string.sign_in_sync)
            } else {
                resources.getString(R.string.sign_out_sync)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.backup_sync))
                .setMessage(message)
                .setNegativeButton(resources.getString(R.string.no_thanks)) { _, _ ->
                    // Respond to negative button press
                    Toast.makeText(requireContext(), R.string.no_backup, Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton(resources.getString(R.string.yes_please)) { _, _ ->
                    // Respond to positive button press
                    handleAuthentication()
                }
                .show()
        }

        val taskNavigateListener = TaskNavigateListener {
            val action = TasksFragmentDirections.actionNavTasksToNavTask(it)
            navController.navigate(action)
        }

        val taskCompleteListener = TaskCompleteListener { viewModel.complete(it) }

        val adapter = TasksAdapter(taskNavigateListener, taskCompleteListener)

        binding.tasksList.adapter = adapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.getTask(position)
                if (task.timerAlarm && task.timerFinishAt > TimeUtil().nowInSeconds()){
                    val name = task.name
                    val requestCode: Int = task.timerFinishAt.toInt()
                    cancelAlarm(application, requestCode, name)
                }
                viewModel.archive(task)
                Snackbar.make(view, R.string.task_archived, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addTaskFab)
                    .setAction(R.string.undo) {
                        viewModel.unarchive(task)
                    }
                    .show()
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
        itemTouchHelper.attachToRecyclerView(binding.tasksList)


        binding.tasksList.addItemDecoration(
            BottomMarginItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.vertica_list_margin
                )
            )
        )

        binding.addTaskFab.setOnClickListener {
            if (hasCategories) {
                // Navigate to create task
                val action = TasksFragmentDirections.actionNavTasksToNavTaskModify(null)
                navController.navigate(action)
            } else {
                Snackbar.make(it, R.string.no_categories, Snackbar.LENGTH_LONG)
                    .setAnchorView(it)
                    .setAction(R.string.create) {
                        val action = MainNavDirections.actionGlobalNavCategories()
                        navController.navigate(action)
                    }
                    .show()
            }
        }

        viewModel.tasksCategory.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                hasCategories = it.isNotEmpty()
            }
        }

        // sync worker
        val user = Firebase.auth.currentUser
        if (user !== null) {
            handleSync()
        }

        handlePeriodicWork()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }

    private fun handleSync() {
        val workManager = WorkManager.getInstance(requireContext())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncCategory = OneTimeWorkRequestBuilder<CategorySyncWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAG.CATEGORY_SYNC)
            .build()

        val syncGoal = OneTimeWorkRequestBuilder<GoalSyncWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAG.GOAL_SYNC)
            .build()

        val continuation = workManager.beginUniqueWork(
            WorkerConstants.SYNC,
            ExistingWorkPolicy.KEEP,
            syncCategory
        ).then(syncGoal)

        continuation.enqueue()
    }

    private fun handlePeriodicWork() {
        val workManager = WorkManager.getInstance(requireContext())

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val updateGoalProgress =
            PeriodicWorkRequestBuilder<GoalProgressWorker>(3, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        val generateTask =
            PeriodicWorkRequestBuilder<GenerateTaskWorker>(3, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            WorkerConstants.UPDATE_GOAL_PROGRESS,
            ExistingPeriodicWorkPolicy.UPDATE,
            updateGoalProgress
        )

        workManager.enqueueUniquePeriodicWork(
            WorkerConstants.GENERATE_TASK,
            ExistingPeriodicWorkPolicy.UPDATE,
            generateTask
        )
    }

    private fun handleAuthentication() {
        // if user isn't signed in and hasn't already declined to use One Tap sign-in
        if (showOneTapUI && Firebase.auth.currentUser == null) {
            val clientId = getString(R.string.default_web_client_id)
            oneTapClient.launchSignIn(clientId)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)

                    } catch (e: IntentSender.SendIntentException) {
                        Firebase.crashlytics.log("One Tap Failure: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Firebase.crashlytics.log("Auth Failure: ${e.localizedMessage}")
                }
            // don't
            showOneTapUI = false
        } else {
            // sign out
            Firebase.auth.signOut()
            oneTapClient.signOut()
            showOneTapUI = true
        }
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                oneTapClient.handleSignInWithFirebase(it.data)
            }
        }
}