package app.stacq.plan.ui.profile

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentProfileBinding
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())

        firebaseAuth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                binding.authButton.setText(R.string.sign_in)
                binding.profileImageView.setImageResource(R.mipmap.ic_launcher)
            } else {
                binding.authButton.setText(R.string.sign_out)
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

        binding.authButton.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                try {
                    this@ProfileFragment.context?.let { context ->
                        AuthUI.getInstance().signOut(context)
                        Toast.makeText(context, R.string.sign_out_success, Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.sign_out_failure, Toast.LENGTH_SHORT).show()
                }
            } else {
                val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
                // Create and launch sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.mipmap.ic_launcher)
                    .setTheme(R.style.Theme_Plan)
                    .build()
                signInLauncher.launch(signInIntent)
            }
        }

        if (firebaseAuth.currentUser == null) {
            binding.syncButton.visibility = View.GONE
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

        (binding.profileImageView.drawable as Animatable).start()

    }

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            onSignInResult(res)
        }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            Toast.makeText(context, R.string.sign_in_success, Toast.LENGTH_SHORT).show()
            binding.syncButton.visibility = View.VISIBLE
        } else {
            // Sign in failed
            // If response is null the user canceled the sign-in flow using the back button.
            if (response == null) {
                Toast.makeText(context, R.string.sign_in_dismiss, Toast.LENGTH_SHORT).show()
            } else {
                response.error?.let {
                    viewModel.logAuthentication(it.errorCode)
                }
                Toast.makeText(context, R.string.sign_in_failure, Toast.LENGTH_SHORT).show()
            }
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

}