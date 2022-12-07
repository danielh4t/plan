package app.stacq.plan.ui.profile

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentProfileBinding
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.launchSignIn
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private lateinit var oneTapClient: SignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val logTag = "Profile"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oneTapClient = Identity.getSignInClient(requireActivity())
        firebaseAuth = Firebase.auth

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())

        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                binding.profileImageView.setImageResource(R.mipmap.ic_launcher)
            } else {
                auth.currentUser.let { user ->
                    if (user != null) {
                        if (user.photoUrl !== null) {
                            binding.profileImageView.load(user.photoUrl) {
                                crossfade(true)
                                size(ViewSizeResolver(binding.profileImageView))
                                transformations(CircleCropTransformation())
                            }
                        }
                    }
                }
            }
        }

        binding.signInButton.setOnClickListener {
            val clientId = getString(R.string.default_web_client_id)
            oneTapClient.launchSignIn(clientId)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(logTag, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(logTag, "Failure: ${e.localizedMessage}")
                }

        }

        binding.signOutButton.setOnClickListener {
            oneTapClient.signOut()
            firebaseAuth.signOut()
            viewModel.currentUser.postValue(null)
        }

        viewModel.taskAnalysis.observe(viewLifecycleOwner) { tasks ->
            binding.monthGrid.removeAllViews()
            if (tasks != null) {
                val daysMap = tasks.associate { it.day to it.completed }
                for (day in 0..viewModel.days) {

                    val params = GridLayout.LayoutParams()
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT
                    params.marginStart = 16
                    params.marginEnd = 16
                    params.topMargin = 8
                    params.bottomMargin = 8
                    val imageView = ImageView(context)
                    imageView.layoutParams = params
                    imageView.setImageResource(R.drawable.ic_circle)
                    imageView.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.green_20),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    // check if in list
                    if (daysMap.containsKey(day)) {
                        val completed = daysMap[day]
                        if (completed != null) {
                            val color = when (completed) {
                                in 1..4 -> R.color.green_60
                                in 5..9 -> R.color.green_80
                                else -> R.color.green
                            }
                            imageView.setColorFilter(
                                ContextCompat.getColor(requireContext(), color),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    binding.monthGrid.addView(imageView)

                    binding.percentageText.text = viewModel.calculatePercentage(daysMap.size)
                }
            }
        }

        binding.syncButton.setOnClickListener {
            viewModel.sync()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]
            if (workInfo.state.isFinished) {
                Toast.makeText(context, R.string.sync_complete, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                oneTapClient.handleSignInWithFirebase(it.data, firebaseAuth)
            }
        }
}