package app.stacq.plan.ui.goalModify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentGoalModifyBinding
import com.google.android.material.chip.Chip
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalModifyFragment : Fragment() {

    private var _binding: FragmentGoalModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: GoalModifyViewModelFactory
    private lateinit var viewModel: GoalModifyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = GoalModifyFragmentArgs.fromBundle(requireArguments())
        val goalId: String? = args.goalId

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = GoalModifyViewModelFactory(goalRepository, categoryRepository, goalId)
        viewModel = ViewModelProvider(this, viewModelFactory)[GoalModifyViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.goalModifyAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.goalModifyAppBar.setupWithNavController(navController, appBarConfiguration)

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.goalModifyCategoryChipGroup.removeAllViews()
            categories?.let {
                it.map { category ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        binding.goalModifyCategoryChipGroup,
                        false
                    ) as Chip
                    chip.text = category.name
                    chip.contentDescription =
                        getString(R.string.content_category_chip, category.name)
                    chip.tag = category.id
                    binding.goalModifyCategoryChipGroup.addView(chip)
                }
            }
        }

        viewModel.goal.observe(viewLifecycleOwner) { it ->
            it?.let {
                binding.goalModifyNameEditText.setText(it.name)
                binding.goalModifyMeasureEditText.setText(it.measure)
                binding.goalModifyResultEditText.setText(it.result)
                binding.goalModifyDaysEditText.setText(it.days.toString())
                val categoryChip =
                    binding.goalModifyCategoryChipGroup.findViewWithTag(it.categoryId) as Chip?
                categoryChip?.isChecked = true
            }
        }

        binding.goalModifyFab.setOnClickListener { clickedView ->
            val name: String = binding.goalModifyNameEditText.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.goal_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val measure: String = binding.goalModifyMeasureEditText.text.toString().trim()
            if (measure.isEmpty()) {
                Snackbar.make(clickedView, R.string.goal_measure_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val result: String = binding.goalModifyResultEditText.text.toString().trim()
            if (result.isEmpty()) {
                Snackbar.make(clickedView, R.string.goal_result_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedId: Int = binding.goalModifyCategoryChipGroup.checkedChipId
            if (checkedId == View.NO_ID) {
                Snackbar.make(clickedView, R.string.empty_category_details, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedChip = binding.goalModifyCategoryChipGroup.findViewById<Chip>(checkedId)
            val categoryId = checkedChip.tag as String

            val daysText = binding.goalModifyDaysEditText.text.toString()
            val days = daysText.toIntOrNull()
            if (days == null) {
                Snackbar.make(clickedView, R.string.goal_days_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val id = if (goalId == null) {
                viewModel.create(name, measure, result, categoryId, days)
            } else {
                viewModel.update(name, measure, result, categoryId, days)
                goalId
            }

            val action = GoalModifyFragmentDirections.actionNavGoalModifyToNavGoal(id)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}