package app.stacq.plan.ui.profile

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.stacq.plan.R
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseUser

@BindingAdapter("signedInVisible")
fun Button.signedInVisible(currentUser: FirebaseUser?) {
    visibility = if (currentUser == null) View.INVISIBLE // signed out
    else View.VISIBLE
}

@BindingAdapter("signInVisible")
fun SignInButton.signInVisible(currentUser: FirebaseUser?) {
    visibility = if (currentUser == null) View.VISIBLE // signed out
    else View.INVISIBLE
}

@BindingAdapter("displayText")
fun TextView.setDisplayText(currentUser: FirebaseUser?) {
    if (currentUser == null) setText(R.string.sign_in_sync)
    else text = currentUser.displayName
}