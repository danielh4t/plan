package app.stacq.plan.ui.timer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import app.stacq.plan.R

class PostNotificationsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.post_notifications))
            .setPositiveButton(getString(R.string.notify)) { _, _ ->
                setFragmentResult("requestKey", bundleOf("bundleKey" to true))
            }
            .setNegativeButton(getString(R.string.no_thanks)) { _, _ ->
                setFragmentResult("requestKey", bundleOf("bundleKey" to false))
            }
            .create()

    companion object {
        const val TAG = "PostNotificationsPermissionDialog"
    }

}