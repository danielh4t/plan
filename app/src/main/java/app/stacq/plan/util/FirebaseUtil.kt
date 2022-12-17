package app.stacq.plan.util

import app.stacq.plan.BuildConfig
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory


fun FirebaseAppCheck.installCheckProviderFactory() {

    if (BuildConfig.DEBUG) {
        // do something for a debug build
        installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    } else {
        installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        );
    }

}
