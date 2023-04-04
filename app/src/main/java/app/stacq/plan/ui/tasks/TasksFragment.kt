package app.stacq.plan.ui.tasks

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.*
import app.stacq.plan.MainNavDirections
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.launchSignIn
import app.stacq.plan.util.ui.MarginItemDecoration
import app.stacq.plan.worker.CategorySyncWorker
import app.stacq.plan.worker.GoalProgressWorker
import app.stacq.plan.worker.GoalSyncWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
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

        val tasksAdapter = TasksAdapter(taskNavigateListener, taskCompleteListener)

        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))

        binding.createTaskFab.setOnClickListener {
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
                tasksAdapter.submitList(it)
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
        handleGoalProgress()
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

    private fun handleGoalProgress() {
        val workManager = WorkManager.getInstance(requireContext())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<GoalProgressWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WorkerConstants.TAG.GOAL_PROGRESS)
                .build()

        workManager.enqueueUniquePeriodicWork(
            WorkerConstants.UPDATE_GOAL_PROGRESS, ExistingPeriodicWorkPolicy.UPDATE, workRequest
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
                        Log.e("Plan", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("Plan", "Failure: ${e.localizedMessage}")
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