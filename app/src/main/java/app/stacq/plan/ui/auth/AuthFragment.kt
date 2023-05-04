package app.stacq.plan.ui.auth

import android.os.Bundle
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
            val email: String = binding.authEmailEditText.text.toString().trim()
            val password: String = binding.authPasswordEditText.text.toString().trim()

            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "createUserWithEmail")
                        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                    } else {
                        // If sign in fails, display a message to the user.
                        Firebase.crashlytics.log("createUserWithEmail: ${task.exception?.stackTrace}")
                        Toast.makeText(
                            requireContext(),
                            R.string.sign_up_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val action = AuthFragmentDirections.actionNavAuthToNavProfile()
                    navController.navigate(action)
                }
        }

        binding.signInButton.setOnClickListener {
            val email: String = binding.authEmailEditText.text.toString().trim()
            val password: String = binding.authPasswordEditText.text.toString().trim()

            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "signInWithEmail")
                        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                        val action = AuthFragmentDirections.actionNavAuthToNavProfile()
                        navController.navigate(action)
                    } else {
                        // If sign in fails, display a message to the user.
                        Firebase.crashlytics.log("signInWithEmail: ${task.exception?.stackTrace}")
                        Toast.makeText(
                            requireContext(),
                            R.string.sign_in_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val action = AuthFragmentDirections.actionNavAuthToNavProfile()
                    navController.navigate(action)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

