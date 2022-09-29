package app.stacq.plan.ui.timer

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import app.stacq.plan.R

class PostNotificationsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.post_notifications))
            .setPositiveButton(getString(R.string.allow)) { _, _ -> }
            .setNegativeButton(getString(R.string.deny))
            .create()

    companion object {
        const val TAG = "PostNotificationsPermissionDialog"
    }

}