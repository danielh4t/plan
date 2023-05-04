package app.stacq.plan.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentAuthBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase


class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        binding.signUpButton.setOnClickListener {
            val email = binding.authEmailEditText.text.toString()
            val password = binding.authPasswordEditText.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                // Handle the error here
                Toast.makeText(
                    requireContext(),
                    R.string.email_password_required,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "createUserWithEmail")
                            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                            navController.popBackStack()
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(
                                requireContext(),
                                R.string.sign_up_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Firebase.crashlytics.recordException(e)
                        Toast.makeText(
                            requireContext(),
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        binding.signInButton.setOnClickListener {
            val email = binding.authEmailEditText.text.toString()
            val password = binding.authPasswordEditText.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                // Handle the error here
                Toast.makeText(
                    requireContext(),
                    R.string.email_password_required,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "signInWithEmail")
                            Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                            navController.popBackStack()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                requireContext(),
                                R.string.sign_in_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Firebase.crashlytics.recordException(e)
                        Toast.makeText(
                            requireContext(),
                            e.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

