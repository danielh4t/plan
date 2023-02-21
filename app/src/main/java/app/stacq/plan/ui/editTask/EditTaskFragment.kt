package app.stacq.plan.ui.editTask


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.databinding.FragmentEditTaskBinding
import app.stacq.plan.util.CalendarUtil
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


class EditTaskFragment : Fragment() {

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: EditTaskViewModelFactory
    private lateinit var viewModel: EditTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSourceImpl = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSourceImpl = TaskRemoteDataSourceImpl()
        val taskRepositoryImpl = TaskRepositoryImpl(taskLocalDataSourceImpl, taskRemoteDataSourceImpl)

        val categoryLocalDataSourceImpl = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepositoryImpl =
            CategoryRepositoryImpl(categoryLocalDataSourceImpl, categoryRemoteDataSource)

        viewModelFactory = EditTaskViewModelFactory(taskRepositoryImpl, categoryRepositoryImpl, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditTaskViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.editCategoryChipGroup.removeAllViews()
            categories?.let {
                it.map { category ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        binding.editCategoryChipGroup,
                        false
                    ) as Chip
                    chip.text = category.name
                    chip.tag = category.id
                    binding.editCategoryChipGroup.addView(chip)
                }
            }

            viewModel.task.observe(viewLifecycleOwner) { it ->
                it?.let {
                    val categoryChip =
                        binding.editCategoryChipGroup.findViewWithTag(it.categoryId) as Chip?
                    categoryChip?.isChecked = true
                }
            }
        }

        binding.dateButton.setOnClickListener {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setStart(CalendarUtil().yearStartAtMillis())
                    .setEnd(CalendarUtil().todayStartAtMillis())
                    .setValidator(DateValidatorPointBackward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_completed_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds() - TimeUnit.DAYS.toMillis(1))
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()

            datePicker.show(requireActivity().supportFragmentManager, "Test")

            datePicker.addOnPositiveButtonClickListener {
                it?.let {
                    viewModel.updateCompletion(true, it / 1000L)
                }
            }
        }

        binding.editFab.setOnClickListener { clickedView ->

            val name: String = binding.editName.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.task_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            // get checked chip
            val checkedId: Int = binding.editCategoryChipGroup.checkedChipId
            if (checkedId == View.NO_ID) {
                Snackbar.make(
                    clickedView,
                    R.string.empty_category_details,
                    Snackbar.LENGTH_SHORT
                )
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }
            val checkedChip = binding.editCategoryChipGroup.findViewById<Chip>(checkedId)
            // get category id from chip tag
            val categoryId = checkedChip.tag as String


            viewModel.edit(name, categoryId)
            val action = EditTaskFragmentDirections.actionNavEditToNavTask(taskId)
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}