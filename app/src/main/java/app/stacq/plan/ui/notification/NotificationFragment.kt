package app.stacq.plan.ui.notification

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.databinding.FragmentNotificationBinding
import com.google.android.material.snackbar.Snackbar


class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = NotificationFragmentArgs.fromBundle(requireArguments())
        val task: TaskCategory = args.taskCategory

        val viewModel: NotificationViewModel by viewModels()
        binding.lifecycleOwner = viewLifecycleOwner

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted.
                    viewModel.logPermission(true)
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied.
                    Snackbar.make(
                        binding.notifyButton,
                        R.string.no_notification,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.logPermission(false)
                }
                val action =
                    NotificationFragmentDirections.actionNavNotificationToNavTimer(task, isGranted)
                this.findNavController().navigate(action)
            }

        binding.notifyButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        binding.noThanksButton.setOnClickListener {
            Snackbar.make(
                binding.noThanksButton,
                R.string.no_notification,
                Snackbar.LENGTH_SHORT
            ).show()
            val action = NotificationFragmentDirections.actionNavNotificationToNavTimer(task, false)
            this.findNavController().navigate(action)
        }

    }


}