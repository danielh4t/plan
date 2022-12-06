package app.stacq.plan.ui.profile

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentProfileBinding
import app.stacq.plan.util.REQ_ONE_TAP
import app.stacq.plan.util.beginSignIn
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
            beginSignIn(requireActivity(), oneTapClient)
                .addOnSuccessListener { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
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

    // The user's response to the One Tap sign-in prompt will be reported here
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google.
                            Log.d(logTag, "Got ID token.")
                            // Use it to authenticate with your backend.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(logTag, "signInWithCredential:success")
                                        val user = firebaseAuth.currentUser
                                        viewModel.currentUser.postValue(user)
                                        //updateUI(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(
                                            logTag,
                                            "signInWithCredential:failure",
                                            task.exception
                                        )
                                        //updateUI(null)
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(logTag, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(logTag, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            // showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(logTag, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(
                                logTag, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }

}