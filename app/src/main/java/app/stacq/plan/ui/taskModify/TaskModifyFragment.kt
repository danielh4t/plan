package app.stacq.plan.ui.taskModify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import app.stacq.plan.util.ui.CategoryMenuAdapter
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Locale


class TaskModifyFragment : Fragment() {

    private var _binding: FragmentTaskModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TaskModifyViewModelFactory
    private lateinit var viewModel: TaskModifyViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

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

        val navController = findNavController()

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    // selected
                    val uid = Firebase.auth.uid
                    if (uid != null && taskId != null) {
                        val imageRef = Firebase.storage.reference.child("$uid/$taskId")
                        val uploadTask = imageRef.putFile(uri)
                        Toast.makeText(
                            requireContext(),
                            R.string.uploading_image,
                            Toast.LENGTH_SHORT
                        ).show()
                        uploadTask
                            .addOnSuccessListener {
                                // Handle successful upload
                                Snackbar.make(
                                    view,
                                    R.string.task_picture_updated,
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setAnchorView(binding.taskModifyAddImageFab)
                                    .show()
                                imageRef.downloadUrl.addOnSuccessListener { imageUri ->
                                    binding.taskModifyImageView.load(imageUri) {
                                        crossfade(true)
                                        size(ViewSizeResolver(binding.taskModifyImageView))
                                        transformations(RoundedCornersTransformation())
                                    }
                                    binding.taskModifyImageView.setOnClickListener {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(imageUri.toString())
                                        startActivity(intent)
                                    }
                                }
                            }
                            .addOnFailureListener {
                                // Handle failed upload
                                Snackbar.make(
                                    view,
                                    R.string.task_picture_upload_failed,
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setAnchorView(binding.taskModifyAddImageFab)
                                    .show()
                            }
                    }
                } else {
                    Snackbar.make(view, R.string.no_media_selected, Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.taskModifyAddImageFab)
                        .show()
                }
            }

        authStateListener = FirebaseAuth.AuthStateListener {
            val uid = Firebase.auth.uid
            if (uid != null && taskId != null) {
                // signed in
                val imageRef = Firebase.storage.reference.child("$uid/$taskId")
                // Create a reference with an initial file path and name
                imageRef.downloadUrl.addOnSuccessListener { imageUri ->

                    binding.taskModifyImageView.load(imageUri) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.taskModifyImageView))
                        transformations(RoundedCornersTransformation())
                    }

                    binding.taskModifyImageView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(imageUri.toString())
                        startActivity(intent)
                    }
                }
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory =
            TaskModifyViewModelFactory(taskRepository, categoryRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskModifyViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner

        binding.taskModifyAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.taskModifyAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.taskModifyStartEditText.setOnClickListener {
            val startDatePicker = startDatePicker()
            if (!startDatePicker.isAdded) {
                startDatePicker.show(
                    requireActivity().supportFragmentManager,
                    "start_date_picker"
                )
            }
        }

        binding.taskModifyCompletionEditText.setOnClickListener {
            if (viewModel.startCalendar.getLocalTimeInMillis() == 0L) {
                Snackbar.make(it, R.string.start_not_set_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.set_start) {
                        val startDatePicker = startDatePicker()
                        if (!startDatePicker.isAdded) {
                            startDatePicker.show(
                                requireActivity().supportFragmentManager,
                                "start_date_picker"
                            )
                        }
                    }
                    .setAnchorView(binding.taskModifyAddImageFab)
                    .show()
            } else {
                val completionDatePicker = completionDatePicker()
                if (!completionDatePicker.isAdded) {
                    completionDatePicker.show(
                        requireActivity().supportFragmentManager,
                        "completion_date_picker"
                    )
                }
            }
        }

        binding.taskModifySaveButton.setOnClickListener {
            // name
            val name: String = binding.taskNameEditText.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(it, R.string.task_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.taskModifyAddImageFab)
                    .show()
                return@setOnClickListener
            }

            val categoryId = viewModel.selectedCategoryId.value
            if (categoryId == null) {
                Snackbar.make(it, R.string.task_category_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.taskModifyAddImageFab)
                    .show()
                return@setOnClickListener
            }

            var startedAt: Long = 0
            val startText = binding.taskModifyStartEditText.text.toString()
            if (startText.isNotEmpty()) {
                startedAt = viewModel.startCalendar.getLocalTimeUTCInSeconds()
            }

            var completedAt: Long = 0
            val completionText = binding.taskModifyCompletionEditText.text.toString()
            if (completionText.isNotEmpty()) {
                completedAt = viewModel.completionCalendar.getLocalTimeUTCInSeconds()
            }

            val notes: String = binding.taskNotesEditText.text.toString().trim()

            val id = if (taskId == null) {
                viewModel.create(name, categoryId, notes)
            } else {
                viewModel.update(name, categoryId, startedAt, completedAt, notes)
                taskId
            }

            val action = TaskModifyFragmentDirections.actionNavEditToNavTask(id)
            navController.navigate(action)
        }

        binding.taskModifyAddImageFab.setOnClickListener {
            val uid = Firebase.auth.uid
            if (uid != null) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                Snackbar.make(it, R.string.sign_in_up_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.taskModifyAddImageFab)
                    .show()
            }
        }

        viewModel.task.observe(viewLifecycleOwner) {
            binding.task = it
            it?.let {
                viewModel.setSelectedCategoryId(it.categoryId)
                viewModel.startCalendar.setLocalTimeSeconds(it.startedAt)
                viewModel.completionCalendar.setLocalTimeSeconds(it.completedAt)
                binding.taskModifyCategoryAutocomplete.setText(it.categoryName, false)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val adapter = CategoryMenuAdapter(requireContext(), categories)
            binding.taskModifyCategoryAutocomplete.setAdapter(adapter)
            binding.taskModifyCategoryAutocomplete.setOnItemClickListener { _, _, position, _ ->
                adapter.getItem(position)?.let { category ->
                    binding.taskModifyCategoryAutocomplete.setText(category.name, false)
                    viewModel.setSelectedCategoryId(category.id)
                }
            }
        }
    }

    private fun startDatePicker(): MaterialDatePicker<Long> {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(CalendarUtil().getTodayTimeInMillis())
                .setValidator(DateValidatorPointBackward.now())

        if (viewModel.startCalendar.getLocalTimeInMillis() == 0L)
            viewModel.startCalendar.setTodayTimeInMillis()

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_start_date))
                .setSelection(viewModel.startCalendar.getLocalTimeInMillis())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        val isSystem24Hour = DateFormat.is24HourFormat(context)
        val clockFormat =
            if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(viewModel.startCalendar.localHour())
                .setMinute(viewModel.startCalendar.localMinute())
                .setTitleText(getString(R.string.select_start_time))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            // store current time
            val hour = viewModel.startCalendar.localHour()
            val minute = viewModel.startCalendar.localMinute()

            // set start
            viewModel.startCalendar.setLocalHour(timePicker.hour)
            viewModel.startCalendar.setLocalMinute(timePicker.minute)

            val completion = if (viewModel.completionCalendar.getLocalTimeInMillis() == 0L) {
                viewModel.completionCalendar.getUTCCurrentTimeInMillis()
            } else {
                viewModel.completionCalendar.getLocalTimeInMillis()
            }
            // check if start time is less than completion
            if (viewModel.startCalendar.getLocalTimeInMillis() <= completion) {
                val time = viewModel.startCalendar.localTime()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time)
                binding.taskModifyStartEditText.setText(sdf)
            } else {
                // reset start to previous state
                viewModel.startCalendar.setLocalHour(hour)
                viewModel.startCalendar.setLocalMinute(minute)
                Toast.makeText(
                    requireContext(),
                    R.string.start_after_completion_error,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        datePicker.addOnPositiveButtonClickListener {
            it?.let {
                val completion = if (viewModel.completionCalendar.getLocalTimeInMillis() == 0L) {
                    viewModel.completionCalendar.getUTCStartOfDayInMillis()
                } else {
                    viewModel.completionCalendar.getLocalTimeInMillis()
                }
                // check start date is less than or equal to completion
                if (it <= completion) {
                    viewModel.startCalendar.setLocalDate(it)
                    if (!timePicker.isAdded) {
                        timePicker.show(
                            requireActivity().supportFragmentManager,
                            "start_time_picker"
                        )
                    }
                } else {
                    // start is greater than completion
                    Toast.makeText(
                        requireContext(), R.string.start_after_completion_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    datePicker.dismiss()
                }
            }
        }

        datePicker.addOnCancelListener {
            viewModel.startCalendar.reset()
        }

        datePicker.addOnNegativeButtonClickListener {
            viewModel.startCalendar.reset()
        }

        return datePicker
    }

    private fun completionDatePicker(): MaterialDatePicker<Long> {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(viewModel.completionCalendar.getLocalTimeUTCInSeconds())
                .setEnd(CalendarUtil().getTodayTimeInMillis())
                .setValidator(DateValidatorPointBackward.now())

        if (viewModel.completionCalendar.getLocalTimeInMillis() == 0L)
            viewModel.completionCalendar.setTodayTimeInMillis()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_completed_date))
            .setSelection(viewModel.completionCalendar.getLocalTimeInMillis())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        val isSystem24Hour = DateFormat.is24HourFormat(context)
        val clockFormat =
            if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .setHour(viewModel.completionCalendar.localHour())
                .setMinute(viewModel.completionCalendar.localMinute())
                .setTitleText(getString(R.string.select_completed_time))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = viewModel.completionCalendar.localHour()
            val minute = viewModel.completionCalendar.localMinute()

            // set completion
            viewModel.completionCalendar.setLocalHour(timePicker.hour)
            viewModel.completionCalendar.setLocalMinute(timePicker.minute)

            // check if completion time is greater than start and less than local time
            if (viewModel.completionCalendar.getLocalTimeInMillis() >
                viewModel.startCalendar.getLocalTimeInMillis() &&
                viewModel.completionCalendar.getLocalTimeInMillis() <=
                viewModel.completionCalendar.getLocalCurrentTimeInMillis()
            ) {
                val time = viewModel.completionCalendar.localTime()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time)
                binding.taskModifyCompletionEditText.setText(sdf)
            } else {
                // reset calendar to previous state
                viewModel.completionCalendar.setLocalHour(hour)
                viewModel.completionCalendar.setLocalMinute(minute)
                Toast.makeText(
                    requireContext(), R.string.completion_before_start_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        datePicker.addOnPositiveButtonClickListener {
            it?.let {
                // check start is set
                if (viewModel.startCalendar.getLocalTimeInMillis() == 0L) {
                    Toast.makeText(
                        requireContext(), R.string.start_not_set_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    datePicker.dismiss()
                }
                // check if completion date is greater or equal to start and start is set
                else if (it >= viewModel.startCalendar.getUTCStartOfDayInMillis()) {
                    if (!timePicker.isAdded) {
                        timePicker.show(
                            requireActivity().supportFragmentManager,
                            "completion_time_picker"
                        )
                    }
                } else {
                    // completion is less than start
                    Toast.makeText(
                        requireContext(), R.string.completion_before_start_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    datePicker.dismiss()
                }
            }
        }
        return datePicker
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }
}