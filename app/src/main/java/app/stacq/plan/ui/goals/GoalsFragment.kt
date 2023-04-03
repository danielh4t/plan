package app.stacq.plan.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.*
import app.stacq.plan.MainNavDirections
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentGoalsBinding
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.util.ui.MarginItemDecoration
import app.stacq.plan.worker.GoalSyncWorker
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: GoalsViewModelFactory
    private lateinit var viewModel: GoalsViewModel

    private var hasCategories: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = GoalsViewModelFactory(goalRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GoalsViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.goalsAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()

        val goalNavigateListener = GoalNavigateListener {
            val action = GoalsFragmentDirections.actionNavGoalsToNavGoal(it)
            navController.navigate(action)
        }

        val goalCompletedListener = GoalCompletedListener { viewModel.complete(it) }

        val adapter = GoalsAdapter(goalNavigateListener, goalCompletedListener)

        binding.goalsList.adapter = adapter
        binding.goalsList.addItemDecoration(
            MarginItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.list_margin
                )
            )
        )

        binding.addGoalFab.setOnClickListener {
            if (hasCategories) {
                // Navigate to create goal
                val action = GoalsFragmentDirections.actionNavGoalsToNavGoalModify(null)
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

        viewModel.goals.observe(viewLifecycleOwner) {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleSync() {
        val workManager = WorkManager.getInstance(requireNotNull(this.activity).application)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncGoal = OneTimeWorkRequestBuilder<GoalSyncWorker>()
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAG.GOAL)
            .build()

        workManager.beginUniqueWork(
            WorkerConstants.SYNC_CATEGORY,
            ExistingWorkPolicy.KEEP,
            syncGoal
        ).enqueue()
    }
}