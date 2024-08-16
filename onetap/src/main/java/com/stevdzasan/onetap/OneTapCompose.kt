package com.stevdzasan.onetap

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable that allows you to easily integrate One-Tap Sign in with Google
 * in your project.
 * @param clientId - CLIENT ID (Web) of your project, that you can obtain from
 * a Google Cloud Platform.
 * @param rememberAccount - Remember a selected account to sign in with, for an easier
 * and quicker sign in process. Set this value to false, if you always want be prompted
 * to select from multiple available accounts. You will be able to automatically sign in
 * with a remembered account, only if you have selected a single account from the list.
 * @param context - The context in which the composable is being called. This is typically
 *  the activity or application context and is used for accessing system resources
 *  or services.
 * @param scope - The CoroutineScope in which asynchronous operations are executed. This allows
 *  for managing the lifecycle of coroutines, ensuring they are canceled appropriately
 *  when the composable is no longer in use.
 * @param nonce - Optional nonce that can be used when generating a Google Token ID.
 * @param onTokenIdReceived - Lambda that will be triggered after a successful
 * authentication. Returns a Token ID.
 * @param onDialogDismissed - Lambda that will be triggered when One-Tap dialog
 * disappears. Returns a message in a form of a string.
 * */
class OneTapCompose(
    private val clientId: String,
    private val rememberAccount: Boolean = true,
    private val context: Context,
    private val scope: CoroutineScope,
    private val nonce: String? = null,
    private val onTokenIdReceived: (String) -> Unit,
    private val onDialogDismissed: (String) -> Unit,
) {

    private companion object {
        const val TAG_ONE_TAP_COMPOSE = "OneTapCompose"
        const val GET_CREDENTIAL_EXCEPTION = "GetCredentialException"
        const val DIALOG_CLOSED = "Dialog Closed"
        const val UNKNOWN_ERROR = "Unknown Error"
        const val TEMPORARILY_BLOCKED = "Temporarily Blocked due to too many Closed Prompts"
        const val NO_CREDENTIALS = "No credentials available"
        const val SELECTOR_CANCELLED = "User cancelled the selector"
        const val ACTIVITY_CANCELLED = "activity is cancelled by the user"
        const val CALLER_BLOCKED = "Caller has been temporarily blocked"
        const val INVALID_TOKEN_GOOGLE = "Invalid Google tokenId response"
        const val UNEXPECTED_TYPE_CREDENTIAL = "Unexpected Type of Credential"
        const val CANCELLED_USER = "Cancelled by user"
    }

    fun oneTapSignInWithGoogle() = run {
        val request = createCredentialRequest()
        val credentialManager = CredentialManager.create(context)

        scope.launch {
            val credentialResponse = try {
                credentialManager.getCredential(context, request)
            } catch (exception: GetCredentialException) {
                return@launch handleFailure(exception)
            }

            handleSignIn(credentialResponse)
        }
    }

    private fun handleSignIn(credentialResponse: GetCredentialResponse) {
        val credential = credentialResponse.credential

        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return logAndDismiss(Log.ERROR, UNEXPECTED_TYPE_CREDENTIAL)
        }

        val googleIdTokenCredential = try {
            GoogleIdTokenCredential.createFrom(credential.data)
        } catch (exception: GoogleIdTokenParsingException) {
            return logAndDismiss(Log.ERROR, "$INVALID_TOKEN_GOOGLE: ${exception.message}")
        }

        onTokenIdReceived(googleIdTokenCredential.idToken)
    }

    private fun handleFailure(exception: GetCredentialException) {
        val errorMessage = exception.message ?: UNKNOWN_ERROR

        when {
            errorMessage.contains(SELECTOR_CANCELLED, ignoreCase = true) || errorMessage.contains(
                ACTIVITY_CANCELLED, ignoreCase = true
            ) || errorMessage.contains(CANCELLED_USER, ignoreCase = true) -> {
                logAndDismiss(Log.DEBUG, "$GET_CREDENTIAL_EXCEPTION: $DIALOG_CLOSED")
            }

            errorMessage.contains(CALLER_BLOCKED, ignoreCase = true) -> {
                logAndDismiss(Log.ERROR, "$GET_CREDENTIAL_EXCEPTION: $TEMPORARILY_BLOCKED")
            }

            errorMessage.contains(NO_CREDENTIALS, ignoreCase = true) -> {
                logAndDismiss(Log.ERROR, "$GET_CREDENTIAL_EXCEPTION: $NO_CREDENTIALS")
                openGoogleAccountSettings()
            }
            
            else -> logAndDismiss(Log.ERROR, "$GET_CREDENTIAL_EXCEPTION: $errorMessage")
        }
    }

    private fun createCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(rememberAccount)
            .setServerClientId(clientId)
            .setNonce(nonce)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun openGoogleAccountSettings() {
        val addAccountIntent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
        context.startActivity(addAccountIntent)
    }

    private fun logAndDismiss(logLevel: Int, message: String) {
        Log.println(logLevel, TAG_ONE_TAP_COMPOSE, message)
        onDialogDismissed(message)
    }
}