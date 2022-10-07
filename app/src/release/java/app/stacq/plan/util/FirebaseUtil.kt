package app.stacq.plan.util

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

fun installCheckProviderFactory(firebaseAppCheck: FirebaseAppCheck) {
    firebaseAppCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance()
    );
}
