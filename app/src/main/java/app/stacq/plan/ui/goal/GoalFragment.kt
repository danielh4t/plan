package app.stacq.plan.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import app.stacq.plan.R
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentGoalBinding
import app.stacq.plan.domain.Goal
import app.stacq.plan.util.constants.WorkerConstants
import app.stacq.plan.worker.TaskGenerateFromGoalWorker
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class GoalFragment : Fragment() {

    private var _binding: FragmentGoalBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: GoalViewModelFactory
    private lateinit var viewModel: GoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = GoalFragmentArgs.fromBundle(requireArguments())
        val goalId: String = args.goalId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        viewModelFactory = GoalViewModelFactory(goalRepository, taskRepository,goalId)
        viewModel = ViewModelProvider(this, viewModelFactory)[GoalViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.goalAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.goalAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.goalAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_goal -> {
                    val action = GoalFragmentDirections.actionNavGoalToNavGoalModify(goalId)
                    navController.navigate(action)
                    true
                }
                R.id.generate_goal_task -> {
                    viewModel.generateTask()
                    Toast.makeText(requireContext(), R.string.task_generated, Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.delete_goal -> {
                    viewModel.delete()
                    Snackbar.make(view, R.string.goal_deleted, Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.root)
                        .setAction(R.string.undo) {
                            viewModel.undoDelete()
                        }
                        .show()
                    val action = GoalFragmentDirections.actionNavGoalToNavGoals()
                    navController.navigate(action)
                    true
                }
                else -> false
            }
        }

        binding.goalGenerateSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Responds to switch being checked/unchecked
            viewModel.updateGenerate(isChecked)
            val workManager = WorkManager.getInstance(requireContext())
            if (isChecked) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()

                val workRequest =
                    PeriodicWorkRequestBuilder<TaskGenerateFromGoalWorker>(1, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .addTag(WorkerConstants.TAG.GOAL_GENERATE_TASK)
                        .build()

                workManager.enqueueUniquePeriodicWork(
                    WorkerConstants.GENERATE_TASK, ExistingPeriodicWorkPolicy.UPDATE, workRequest
                )
            } else {
                workManager.cancelUniqueWork(WorkerConstants.GENERATE_TASK)
            }
        }

        viewModel.goal.observe(viewLifecycleOwner) {
            it?.let {
                binding.goal = it
                progress(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun progress(goal: Goal) {
        binding.goalDaysGrid.removeAllViews()
        for (day in 1..goal.days) {
            val color = if (day <= goal.progress) {
                R.color.color_plan_green_85
            } else {
                R.color.color_plan_green_75
            }

            val image = if (day <= goal.progress) {
                R.drawable.ic_circle
            } else {
                R.drawable.ic_circle_outline
            }

            val imageView = ImageView(context)
            imageView.setImageResource(image)
            imageView.setColorFilter(
                ContextCompat.getColor(requireContext(), color),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            val layoutParams = GridLayout.LayoutParams().apply {
                width = resources.getDimensionPixelSize(R.dimen.goal_image)
                height = resources.getDimensionPixelSize(R.dimen.goal_image)
                bottomMargin = (resources.getDimensionPixelSize(R.dimen.goal_image_bottom_margin))
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            }

            imageView.layoutParams = layoutParams
            binding.goalDaysGrid.addView(imageView)
        }
    }
}