package app.stacq.plan.ui.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentNotificationBinding
import com.google.android.material.snackbar.Snackbar


class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String?>

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
        val taskId: String = args.taskId

        val viewModel: NotificationViewModel by viewModels()
        binding.lifecycleOwner = viewLifecycleOwner

        val hasPermission: Boolean = hasPostNotificationsPermission(requireActivity())
        if (hasPermission) {
            // navigate to timer
            val action = NotificationFragmentDirections.actionNavNotificationToNavTimer(taskId)
            this.findNavController().navigate(action)
        }

        // Handles the user's response to the system permissions dialog.
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted.
                    viewModel.logPermission(true)
                } else {
                    // Permission denied.
                    viewModel.logPermission(false)
                    // Explain to the user that the notification feature unavailable.
                    Snackbar.make(
                        binding.notifyButton,
                        R.string.no_notification,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                // Navigate to timer.
                val action = NotificationFragmentDirections.actionNavNotificationToNavTimer(taskId)
                this.findNavController().navigate(action)
            }

        (binding.notificationImage.drawable as Animatable).start()

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
            val action = NotificationFragmentDirections.actionNavNotificationToNavTimer(taskId)
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Handles request for app post notification permission
     */
    private fun hasPostNotificationsPermission(context: Context): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // Explain to the user why your app requires this permission
                return false
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                return false
            }
        }
    }
}