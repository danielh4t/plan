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
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.material.chip.Chip
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
                        uploadTask.addOnSuccessListener {
                            // Handle successful upload
                            Toast.makeText(
                                requireContext(),
                                R.string.task_picture_updated,
                                Toast.LENGTH_SHORT
                            ).show()
                            imageRef.downloadUrl.addOnSuccessListener { imageUri ->
                                binding.taskModifyImageView.load(imageUri) {
                                    crossfade(true)
                                    size(ViewSizeResolver(binding.taskModifyImageView))
                                    transformations(CircleCropTransformation())
                                }
                            }
                        }.addOnFailureListener {
                            // Handle failed upload
                            Toast.makeText(
                                requireContext(),
                                R.string.task_picture_upload_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.no_media_selected,
                        Toast.LENGTH_SHORT
                    ).show()
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

        // creating a new task
        if (taskId == null) {
            binding.taskModifyDateTimeLayout.visibility = View.GONE
            binding.taskModifyDateTimeImage.visibility = View.GONE
        }

        viewModel.task.observe(viewLifecycleOwner) { it ->
            it?.let {
                binding.task = it
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
                .setEnd(CalendarUtil().getTodayTimeInMillis())
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
                .setHour(CalendarUtil().localHour())
                .setMinute(CalendarUtil().localMinute())
                .setTitleText(getString(R.string.select_completed_time))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()

        timePicker.addOnPositiveButtonClickListener {

            viewModel.calendar.setLocalHour(timePicker.hour)
            viewModel.calendar.setLocalMinute(timePicker.minute)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val time = viewModel.calendar.localTime()
            val dateTimeString = sdf.format(time)
            binding.taskModifyDateTimeEditText.setText(dateTimeString)
        }

        datePicker.addOnPositiveButtonClickListener {
            it?.let {
                viewModel.calendar.setLocalTime(it)
                if (!timePicker.isAdded) {
                    timePicker.show(requireActivity().supportFragmentManager, "time_picker")
                }
            }
        }

        binding.taskModifyImageFab.setOnClickListener {
            // only select picture for
            val uid = Firebase.auth.uid
            if (uid != null && taskId != null) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.sign_in_up_required,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.taskModifyDateTimeEditText.setOnClickListener {
            if (!datePicker.isAdded) {
                datePicker.show(requireActivity().supportFragmentManager, "date_picker")
            }
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
                completedAt = viewModel.calendar.getLocalTimeUTC()
            }

            val notes: String = binding.taskNotesEditText.text.toString().trim()

            val id = if (taskId == null) {
                viewModel.create(name, categoryId, notes)
            } else {
                viewModel.update(name, categoryId, completedAt, notes)
                taskId
            }

            val action = TaskModifyFragmentDirections.actionNavEditToNavTask(id)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }
}