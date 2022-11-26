package com.stevdzasan.onetapcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.stevdzasan.onetapcompose.ui.theme.OneTapComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneTapComposeTheme {
                val state = rememberOneTapSignInState()
                OneTapSignInWithGoogle(
                    state = state,
                    clientId = "YOUR_CLIENT_ID",
                    onTokenIdReceived = {
                        Log.d("MainActivity", it)
                    },
                    onDialogDismissed = {
                        Log.d("MainActivity", it)
                    }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { state.open() }, enabled = !state.opened) {
                        Text(text = "Sign in")
                    }
                }
            }
        }
    }
}