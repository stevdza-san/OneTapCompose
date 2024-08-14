package com.stevdzasan.onetapcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.stevdzasan.onetap.GoogleUser
import com.stevdzasan.onetap.OneTapGoogleButton
import com.stevdzasan.onetap.getUserFromTokenId
import com.stevdzasan.onetapcompose.ui.theme.OneTapComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneTapComposeTheme {
                val scope = rememberCoroutineScope()
                var user: GoogleUser? by remember { mutableStateOf(null) }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    OneTapGoogleButton(
                        clientId = "CLIENT_ID",
                        rememberAccount = false,
                        context = LocalContext.current,
                        scope = scope,
                        onTokenIdReceived = { token ->
                            user = getUserFromTokenId(token)
                            Log.d("MainActivity", user.toString())
                        },
                        onDialogDismissed = { message ->
                            Log.d("MainActivity", message)
                        }
                    )
                }
            }
        }
    }
}