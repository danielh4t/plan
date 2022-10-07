package app.stacq.plan.util

import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory


fun installCheckProviderFactory(firebaseAppCheck: FirebaseAppCheck) {
    firebaseAppCheck.installAppCheckProviderFactory(
        PlayIntegrityAppCheckProviderFactory.getInstance()
    );
}
