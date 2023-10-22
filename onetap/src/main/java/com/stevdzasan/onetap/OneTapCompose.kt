package com.stevdzasan.onetap

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

@Composable
fun rememberOneTapSignInState(): OneTapSignInState {
    return remember { OneTapSignInState() }
}

const val TAG = "OneTapCompose"

/**
 * Composable that allows you to easily integrate One-Tap Sign in with Google
 * in your project.
 * @param state - One-Tap Sign in State.
 * @param clientId - CLIENT ID (Web) of your project, that you can obtain from
 * a Google Cloud Platform.
 * @param rememberAccount - Remember a selected account to sign in with, for an easier
 * and quicker sign in process. Set this value to false, if you always want be prompted
 * to select from multiple available accounts.
 * @param nonce - Optional nonce that can be used when generating a Google Token ID.
 * @param onTokenIdReceived - Lambda that will be triggered after a successful
 * authentication. Returns a Token ID.
 * @param onDialogDismissed - Lambda that will be triggered when One-Tap dialog
 * disappears. Returns a message in a form of a string.
 * */
@Composable
fun OneTapSignInWithGoogle(
    state: OneTapSignInState,
    clientId: String,
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
) {
    val activity = LocalContext.current as Activity
    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            if (result.resultCode == Activity.RESULT_OK) {
                val oneTapClient = Identity.getSignInClient(activity)
                val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                val tokenId = credentials.googleIdToken
                if (tokenId != null) {
                    onTokenIdReceived(tokenId)
                    state.close()
                }
            } else {
                onDialogDismissed("Dialog Closed.")
                state.close()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "${e.message}")
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    onDialogDismissed("Dialog Canceled.")
                    state.close()
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    onDialogDismissed("Network Error.")
                    state.close()
                }
                else -> {
                    onDialogDismissed(e.message.toString())
                    state.close()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.opened) {
        if (state.opened) {
            signIn(
                activity = activity,
                clientId = clientId,
                rememberAccount = rememberAccount,
                nonce = nonce,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                onError = {
                    onDialogDismissed(it)
                    state.close()
                }
            )
        }
    }
}

private fun signIn(
    activity: Activity,
    clientId: String,
    rememberAccount: Boolean,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(rememberAccount)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            signUp(
                activity = activity,
                clientId = clientId,
                nonce = nonce,
                launchActivityResult = launchActivityResult,
                onError = onError
            )
            Log.e(TAG, "${it.message}")
        }
}

private fun signUp(
    activity: Activity,
    clientId: String,
    nonce: String?,
    launchActivityResult: (IntentSenderRequest) -> Unit,
    onError: (String) -> Unit
) {
    val oneTapClient = Identity.getSignInClient(activity)
    val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setNonce(nonce)
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener { result ->
            try {
                launchActivityResult(
                    IntentSenderRequest.Builder(
                        result.pendingIntent.intentSender
                    ).build()
                )
            } catch (e: Exception) {
                onError(e.message.toString())
                Log.e(TAG, "${e.message}")
            }
        }
        .addOnFailureListener {
            onError("Google Account not Found.")
            Log.e(TAG, "${it.message}")
        }
}