package app.stacq.plan.ui.profile

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ProfileViewModelFactory
    private lateinit var viewModel: ProfileViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                binding.yearGrid.visibility = View.VISIBLE
                binding.syncImage.visibility = View.INVISIBLE
                binding.syncText.visibility = View.INVISIBLE
            } else {
                // signed out
                binding.yearGrid.visibility = View.INVISIBLE
                binding.syncImage.visibility = View.VISIBLE
                binding.syncText.visibility = View.VISIBLE
                (binding.syncImage.drawable as Animatable).start()
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        viewModelFactory = ProfileViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }
}