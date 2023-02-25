@file:Suppress("UNCHECKED_CAST")

package app.stacq.plan.ui.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ProfileViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        with(modelClass) {
            when {
                isAssignableFrom(ProfileViewModel::class.java) ->
                    return ProfileViewModel() as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
