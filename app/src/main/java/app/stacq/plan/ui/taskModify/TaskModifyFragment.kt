package app.stacq.plan.ui.taskModify

import android.os.Bundle
import android.text.format.DateFormat
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
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTaskModifyBinding
import app.stacq.plan.util.CalendarUtil
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class TaskModifyFragment : Fragment() {

    private var _binding: FragmentTaskModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TaskModifyViewModelFactory
    private lateinit var viewModel: TaskModifyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = TaskModifyFragmentArgs.fromBundle(requireArguments())
        val taskId: String? = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository = CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory =
            TaskModifyViewModelFactory(taskRepository, categoryRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskModifyViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

        binding.taskModifyAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.taskModifyAppBar.setupWithNavController(navController, appBarConfiguration)

        // creating a new task
        if(taskId == null) {
            binding.taskModifyDateTimeLayout.visibility = View.GONE
            binding.taskModifyDateTimeImage.visibility = View.GONE
        }

        viewModel.task.observe(viewLifecycleOwner) { it ->
            it?.let {
                binding.taskNameEditText.setText(it.name)
                val categoryChip =
                    binding.taskCategoryChipGroup.findViewWithTag(it.categoryId) as Chip?
                categoryChip?.isChecked = true
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.taskCategoryChipGroup.removeAllViews()
            categories?.let {
                it.map { category ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        binding.taskCategoryChipGroup,
                        false
                    ) as Chip
                    chip.text = category.name
                    chip.tag = category.id
                    binding.taskCategoryChipGroup.addView(chip)
                }
            }
        }

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(CalendarUtil().yearStartAtMillis())
                .setEnd(CalendarUtil().todayStartAtMillis())
                .setValidator(DateValidatorPointBackward.now())

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_completed_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        val isSystem24Hour = DateFormat.is24HourFormat(context)
        val clockFormat =
            if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(CalendarUtil().hour())
                .setMinute(CalendarUtil().minute())
                .setTitleText(getString(R.string.select_completed_time))
                .build()

        timePicker.addOnPositiveButtonClickListener {
            viewModel.calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            viewModel.calendar.set(Calendar.MINUTE, timePicker.minute)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dateTimeString = sdf.format(viewModel.calendar.time)
            binding.taskModifyDateTimeEditText.setText(dateTimeString)
        }

        datePicker.addOnPositiveButtonClickListener {
            it?.let {
                viewModel.calendar.timeInMillis = it
                timePicker.show(requireActivity().supportFragmentManager, "time_picker")
            }
        }


        binding.taskModifyDateTimeEditText.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "date_picker")
        }

        binding.taskModifyFab.setOnClickListener { clickedView ->

            val name: String = binding.taskNameEditText.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.task_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            // get checked chip
            val checkedId: Int = binding.taskCategoryChipGroup.checkedChipId
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
            val checkedChip = binding.taskCategoryChipGroup.findViewById<Chip>(checkedId)
            // get category id from chip tag
            val categoryId = checkedChip.tag as String

            var completedAt: Long? = null
            val dateTimeText = binding.taskModifyDateTimeEditText.text.toString()
            if (dateTimeText.isNotEmpty()) {
                val dateTime = LocalDateTime.parse(
                    dateTimeText,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                )
                // Convert the LocalDateTime to a ZonedDateTime in UTC
                val dateTimeUtc = dateTime.atZone(ZoneOffset.UTC)
                // Get the Unix time in seconds
                completedAt = dateTimeUtc.toEpochSecond()
            }

            val id = if (taskId == null) {
                viewModel.create(name, categoryId)
            } else {
                viewModel.update(name, categoryId, completedAt)
                taskId
            }

            val action = TaskModifyFragmentDirections.actionNavEditToNavTask(id)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}