package app.stacq.plan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentProfileBinding
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ProfileViewModelFactory
    private lateinit var viewModel: ProfileViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = ProfileViewModelFactory(taskRepository, goalRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        val navController = findNavController()

        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                val profileRef = Firebase.storage.reference.child("${user.uid}/profile")
                // Create a reference with an initial file path and name
                profileRef.downloadUrl.addOnSuccessListener { imageUri ->
                    binding.accountImageView.load(imageUri) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.accountImageView))
                        transformations(CircleCropTransformation())
                    }
                }

                binding.authButton.setText(R.string.sign_out)
                binding.authButton.setOnClickListener {
                    Firebase.auth.signOut()
                }
            } else {
                // signed out
                binding.authButton.setText(R.string.sign_in_sign_up)
                binding.authButton.setOnClickListener {
                    val action = ProfileFragmentDirections.actionNavProfileToNavAuth()
                    navController.navigate(action)
                }
                binding.accountImageView.setImageResource(R.drawable.ic_account_circle)
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    // selected
                    val uid = Firebase.auth.uid
                    if (uid != null) {
                        val profileRef = Firebase.storage.reference.child("$uid/profile")
                        val uploadTask = profileRef.putFile(uri)
                        uploadTask.addOnSuccessListener {
                            // Handle successful upload
                            Toast.makeText(
                                requireContext(),
                                R.string.profile_picture_updated,
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            // Handle failed upload
                            Toast.makeText(
                                requireContext(),
                                R.string.profile_picture_upload_failed,
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



        binding.accountImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }
}

