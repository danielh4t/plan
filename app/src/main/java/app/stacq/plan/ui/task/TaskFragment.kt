package app.stacq.plan.ui.task

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTaskBinding
import app.stacq.plan.util.createTimerChannel
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.RoundedCornersTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TaskViewModelFactory
    private lateinit var viewModel: TaskViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = TaskFragmentArgs.fromBundle(requireArguments())
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        viewModelFactory = TaskViewModelFactory(taskRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner

        binding.taskAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    // selected
                    val uid = Firebase.auth.uid
                    if (uid != null) {
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
                                Toast.makeText(
                                    requireContext(),
                                    R.string.task_picture_updated,
                                    Toast.LENGTH_SHORT
                                ).show()
                                imageRef.downloadUrl.addOnSuccessListener { imageUri ->
                                    binding.taskImageView.load(imageUri) {
                                        crossfade(true)
                                        size(ViewSizeResolver(binding.taskImageView))
                                        transformations(RoundedCornersTransformation())
                                    }
                                    binding.taskImageView.setOnClickListener {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(imageUri.toString())
                                        startActivity(intent)
                                    }
                                }
                            }
                            .addOnFailureListener {
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
            if (uid != null) {
                // signed in
                val imageRef = Firebase.storage.reference.child("$uid/$taskId")
                // Create a reference with an initial file path and name
                imageRef.downloadUrl.addOnSuccessListener { imageUri ->

                    binding.taskImageView.load(imageUri) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.taskImageView))
                        transformations(RoundedCornersTransformation())
                    }

                    binding.taskImageView.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(imageUri.toString())
                        startActivity(intent)
                    }
                }
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        binding.taskAppBar.setupWithNavController(navController, appBarConfiguration)

        binding.taskAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_task -> {
                    val action = TaskFragmentDirections.actionNavTaskToNavEdit(taskId)
                    navController.navigate(action)
                    true
                }

                R.id.clone_task -> {
                    viewModel.clone()
                    Toast.makeText(requireContext(), R.string.task_cloned, Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.timer_task -> {
                    when {
                        ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            // Permission is granted
                            // You can use the API that requires the permission.
                            val action = TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
                            navController.navigate(action)
                        }

                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                            // Additional rationale should be displayed
                            // Explain to the user why your app requires this permission
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(resources.getString(R.string.timer_complete_notification))
                                .setMessage(resources.getString(R.string.timer_notification_message))
                                .setNegativeButton(resources.getString(R.string.no_thanks)) { _, _ ->
                                    // Respond to negative button press
                                    Toast.makeText(
                                        requireContext(),
                                        R.string.no_notification,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val action =
                                        TaskFragmentDirections.actionNavTaskToNavTimer(taskId)
                                    navController.navigate(action)
                                }
                                .setPositiveButton(resources.getString(R.string.yes_please)) { _, _ ->
                                    // Create Channel
                                    createTimerChannel(requireNotNull(this.activity).application.applicationContext)
                                    // Respond to positive button press
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                                .show()
                        }

                        else -> {
                            // Create Channel
                            createTimerChannel(requireNotNull(this.activity).application.applicationContext)
                            // Permission has not be requested yet
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                    true
                }

                else -> false
            }
        }

        viewModel.task.observe(viewLifecycleOwner) {
            it?.let {
                binding.task = it
            }
        }

        binding.taskPrioritySlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.updatePriority(slider.value)
            }
        })


        binding.taskAddImageFab.setOnClickListener {
            val uid = Firebase.auth.uid
            if (uid != null) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.sign_in_up_required,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }

    // Handles the user's response to the system permissions dialog.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted.
                viewModel.logPermission(true)
                Snackbar.make(
                    binding.taskAddImageFab,
                    R.string.timer_complete_yes_notification,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.taskAddImageFab)
                    .show()
            } else {
                // Permission denied.
                viewModel.logPermission(false)
                // Explain to the user that the notification feature unavailable.
                Snackbar.make(
                    binding.taskAddImageFab,
                    R.string.timer_complete_no_notification,
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.taskAddImageFab)
                    .show()
            }
        }
}
