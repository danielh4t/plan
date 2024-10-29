package app.stacq.plan.ui.auth


import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import app.stacq.plan.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Preview
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    handleSignIn: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUIState.Initial -> {
                // check if user is authenticated
                viewModel.isAuthenticated()
            }

            is AuthUIState.Authenticated -> {
                handleSignIn()
            }

            is AuthUIState.Error -> {
                val message = (uiState as AuthUIState.Error).errorMessage
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            when (uiState) {
                is AuthUIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is AuthUIState.Unauthenticated -> {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(32.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SignInWithGoogleButton(
                        modifier = Modifier.padding(32.dp),
                        onClick = {
                            triggerSignIn(context, coroutineScope, viewModel)
                        })
                    automaticSignIn(context, coroutineScope, viewModel)
                }

                else -> {}
            }
        }
    }
}

fun automaticSignIn(
    context: Context,
    coroutineScope: CoroutineScope,
    authViewModel: AuthViewModel
) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false) // filters accounts used to sign in before
        .setServerClientId(getString(context, R.string.default_web_client_id))
        .setAutoSelectEnabled(false) // enables automatic sign in for returning users
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    platformSignIn(context, request, coroutineScope, authViewModel)
}

fun triggerSignIn(context: Context, coroutineScope: CoroutineScope, authViewModel: AuthViewModel) {
    val signInWithGoogleOption: GetSignInWithGoogleOption =
        GetSignInWithGoogleOption.Builder(getString(context, R.string.default_web_client_id))
            .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    platformSignIn(context, request, coroutineScope, authViewModel)
}

fun platformSignIn(
    context: Context,
    request: GetCredentialRequest,
    coroutineScope: CoroutineScope,
    authViewModel: AuthViewModel,
) {
    val credentialManager = CredentialManager.create(context)
    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request,
            )
            authViewModel.handleSignIn(result)
        } catch (e: GetCredentialException) {
            authViewModel.handleFailure(e)
        }
    }
}
