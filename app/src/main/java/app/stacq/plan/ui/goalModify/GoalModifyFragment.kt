package app.stacq.plan.ui.goalModify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import app.stacq.plan.util.ui.CategoryMenuAdapter
import com.google.android.material.shape.MaterialShapeDrawable
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
            val adapter = CategoryMenuAdapter(requireContext(), categories)
            binding.goalModifyCategoryAutocomplete.setAdapter(adapter)
            binding.goalModifyCategoryAutocomplete.setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let { category ->
                    binding.goalModifyCategoryAutocomplete.setText(category.name, false)
                    viewModel.setSelectedCategoryId(category.id)
                }
            }
        }

        viewModel.goal.observe(viewLifecycleOwner) {
            binding.goal = it
            it?.let {
                viewModel.setSelectedCategoryId(it.categoryId)
                binding.goalModifyCategoryAutocomplete.setText(it.categoryName, false)
            }
        }

        binding.goalModifySaveButton.setOnClickListener {
            val name: String = binding.goalModifyNameEditText.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.goal_name_required,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val measure: String = binding.goalModifyMeasureEditText.text.toString().trim()
            if (measure.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.goal_measure_required,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val result: String = binding.goalModifyResultEditText.text.toString().trim()
            if (result.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.goal_result_required,
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val categoryId = viewModel.selectedCategoryId.value
            if (categoryId == null) {
                Toast.makeText(
                    requireContext(),
                    R.string.goal_category_required,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val daysText = binding.goalModifyDaysEditText.text.toString()
            val days = daysText.toIntOrNull()
            if (days == null) {
                Toast.makeText(
                    requireContext(),
                    R.string.goal_days_required,
                    Toast.LENGTH_SHORT
                )

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