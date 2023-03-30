package app.stacq.plan.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.stacq.plan.R
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentGoalBinding
import app.stacq.plan.worker.TASK_GENERATE_NAME
import app.stacq.plan.worker.TASK_GENERATE_TAG
import app.stacq.plan.worker.TaskGenerateWorker
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

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        viewModelFactory = GoalViewModelFactory(goalRepository, goalId)
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
                R.id.edit -> {
                    val action = GoalFragmentDirections.actionNavGoalToNavGoalModify(goalId)
                    navController.navigate(action)
                    true
                }
                R.id.delete -> {
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


        viewModel.goal.observe(viewLifecycleOwner) {
            it?.let {
                binding.goal = it
            }
        }

        viewModel.completedDays.observe(viewLifecycleOwner) {
            it?.let {
                binding.completedDays = it
                binding.goalDaysGrid.removeAllViews()
                viewModel.goal.value?.days?.let { days ->
                    for (day in 1..days) {
                        if (day <= it) {
                            progress(true)
                        } else {
                            progress(false)
                        }
                    }
                }
            }
        }

        worker()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun progress(completedDay: Boolean) {
        val color = if (completedDay) {
            R.color.color_plan_green_85
        } else {
            R.color.color_plan_green_75
        }

        val image = if (completedDay) {
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

    private fun worker() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<TaskGenerateWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(TASK_GENERATE_TAG)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            TASK_GENERATE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, workRequest
        )
    }
}