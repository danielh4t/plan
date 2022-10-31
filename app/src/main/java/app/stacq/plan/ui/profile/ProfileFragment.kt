package app.stacq.plan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
        firebaseAnalytics = Firebase.analytics

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.user.observe(viewLifecycleOwner) { currentUser ->
            if (currentUser == null) {
                binding.authButton.setText(R.string.sign_in)
            } else {
                binding.authButton.setText(R.string.sign_out)
            }
        }

        binding.authButton.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                try {
                    this@ProfileFragment.context?.let { context ->
                        AuthUI.getInstance().signOut(context)
                    }
                    Snackbar.make(
                        binding.authButton,
                        R.string.sign_out_success,
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(binding.authButton)
                        .show()
                } catch (e: Exception) {
                    Snackbar.make(
                        binding.authButton,
                        R.string.sign_out_failure,
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(binding.authButton)
                        .show()
                }
            } else {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                )
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
    }

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            onSignInResult(res)
        }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            viewModel.updateUser()
            Snackbar.make(binding.authButton, R.string.sign_in_success, Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.authButton)
                .show()
        } else {
            // Sign in failed
            // If response is null the user canceled the sign-in flow using the back button.
            if (response == null) {
                Snackbar.make(binding.authButton, R.string.sign_in_dismiss, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.authButton)
                    .show()
            } else {
                response.error?.let {
                    val params = Bundle()
                    params.putInt("login_error", it.errorCode)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
                }
                Snackbar.make(binding.authButton, R.string.sign_in_failure, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.authButton)
                    .show()
            }
        }

    }

}