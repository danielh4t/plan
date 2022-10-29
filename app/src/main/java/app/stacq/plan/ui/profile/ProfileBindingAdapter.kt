package app.stacq.plan.ui.profile

import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseUser


@BindingAdapter("authState")
fun MaterialButton.setAuthText(user: FirebaseUser?) {
    if (user == null) {
        setText(R.string.sign_in)
    } else {
        setText(R.string.sign_out)
    }
}

