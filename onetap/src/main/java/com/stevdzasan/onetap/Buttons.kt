package com.stevdzasan.onetap

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composable that allows you to easily integrate One-Tap Sign in with Google in your project while
 * following the official Google Sign in button design guidelines.
 *
 * @see <a href="https://developers.google.com/identity/branding-guidelines#font">Sign in with
 * Google Branding Guidelines</a>
 *
 * @param clientId CLIENT ID (Web) of your project, that you can obtain from a Google Cloud Platform.
 * @param state One-Tap Sign in State.
 * @param rememberAccount Remember a selected account to sign in with, for an easier
 * and quicker sign in process. Set this value to false, if you always want be prompted
 * to select from multiple available accounts.
 * @param nonce Optional nonce that can be used when generating a Google Token ID
 * @param onTokenIdReceived Lambda that will be triggered after a successful
 * authentication. Returns a Token ID.
 * @param onUserReceived This function returns a [GoogleUser] object using the received tokenId.
 * @param onDialogDismissed Lambda that will be triggered when One-Tap dialog.
 * disappears. Returns a message in a form of a string.
 * @param iconOnly Whether the button should only show the Google logo.
 * @param theme Sets the button style to either be Light, Dark, or Neutral which is in accordance
 * with the official Google design guidelines.
 * @param colors [ButtonColors] that will be used to resolve the colors for this button in different
 * states.
 * @param border the border to draw around the container of this button
 * @param onClick called when this button is clicked
 */
@Composable
fun OneTapSignInWithGoogleButton(
    clientId: String,
    state: OneTapSignInState = rememberOneTapSignInState(),
    rememberAccount: Boolean = true,
    nonce: String? = null,
    onTokenIdReceived: ((String) -> Unit)? = null,
    onUserReceived: ((GoogleUser) -> Unit)? = null,
    onDialogDismissed: ((String) -> Unit)? = null,
    iconOnly: Boolean = false,
    theme: GoogleButtonTheme = if (isSystemInDarkTheme()) GoogleButtonTheme.Dark
    else GoogleButtonTheme.Light,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = when (theme) {
            GoogleButtonTheme.Light -> Color.White
            GoogleButtonTheme.Dark -> Color(0xFF131314)
            GoogleButtonTheme.Neutral -> Color(0xFFF2F2F2)
        },
        contentColor = when (theme) {
            GoogleButtonTheme.Dark -> Color(0xFFE3E3E3)
            else -> Color(0xFF1F1F1F)
        },
    ),
    border: BorderStroke? = when (theme) {
        GoogleButtonTheme.Light -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF747775),
        )

        GoogleButtonTheme.Dark -> BorderStroke(
            width = 1.dp,
            color = Color(0xFF8E918F),
        )

        GoogleButtonTheme.Neutral -> null
    },
    onClick: (() -> Unit)? = null,
) {
    OneTapSignInWithGoogle(
        state = state,
        clientId = clientId,
        rememberAccount = rememberAccount,
        nonce = nonce,
        onTokenIdReceived = { tokenId ->
            Log.d(ButtonTAG, "onTokenIdReceived: $tokenId")
            onTokenIdReceived?.invoke(tokenId)
            getUserFromTokenId(tokenId = tokenId)?.let { googleUser ->
                onUserReceived?.invoke(googleUser)
            }
        },
        onDialogDismissed = { message ->
            Log.d(ButtonTAG, message)
            onDialogDismissed?.invoke(message)
        }
    )

    Button(
        modifier = Modifier.width(if (iconOnly) 40.dp else Dp.Unspecified),
        onClick = {
            state.open()
            onClick?.invoke()
        },
        colors = colors,
        contentPadding = PaddingValues(horizontal = if (iconOnly) 9.5.dp else 12.dp),
        border = border,
    ) {
        Box(
            modifier = Modifier
                .padding(end = if (iconOnly) 0.dp else 10.dp)
                .paint(painter = painterResource(id = R.drawable.google_logo))
        )
        if (!iconOnly) {
            Text(
                text = "Sign in with Google",
                maxLines = 1,
                fontFamily = RobotoFontFamily,
            )
        }
    }
}

enum class GoogleButtonTheme { Light, Dark, Neutral }

private val RobotoFontFamily = FontFamily(
    Font(
        resId = R.font.roboto_medium,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    )
)

private const val ButtonTAG = "OneTapSignInWithGoogle"

@Preview
@Composable
private fun LightIconButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Light,
        iconOnly = true,
    )
}

@Preview
@Composable
private fun DarkIconButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Dark,
        iconOnly = true,
    )
}

@Preview
@Composable
private fun NeutralIconButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Neutral,
        iconOnly = true,
    )
}

@Preview
@Composable
private fun LightFullButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Light,
    )
}

@Preview
@Composable
private fun DarkFullButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Dark,
    )
}

@Preview
@Composable
private fun NeutralFullButtonPreview() {
    OneTapSignInWithGoogleButton(
        clientId = "test_id",
        theme = GoogleButtonTheme.Neutral,
    )
}
