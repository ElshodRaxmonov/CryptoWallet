package com.example.cryptowallet.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptowallet.R
import com.example.cryptowallet.data.model.AuthState


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onOtpSent: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()

    LoginScreenContent(
        onLoginSuccess = onLoginSuccess,
        onOtpSent = onOtpSent,
        authState = authState,
        email = email,
        updateEmail = { viewModel.updateEmail(it) },
        sendOtp = { viewModel.sendOtp() }
    )
}

@Composable
fun LoginScreenContent(
    onLoginSuccess: () -> Unit,
    onOtpSent: () -> Unit = {},
    authState: AuthState,
    email: String,
    updateEmail: (String) -> Unit,
    sendOtp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.OtpSent -> onOtpSent()
            is AuthState.Authenticated -> onLoginSuccess()
            else -> {}
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, end = 32.dp, start = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(id = R.drawable.main_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Crypto Wallet",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF202124)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please sign in to continue",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp
                ),
                color = Color(0xFF5F6368)
            )

            Spacer(modifier = Modifier.height(48.dp))

            TextField(
                value = email,
                onValueChange = updateEmail,
                label = {
                    Text(
                        text = "Email",
                        color = Color(0xFF5F6368),
                        fontSize = 14.sp
                    )
                },
                placeholder = {
                    Text(
                        text = "you@example.com",
                        color = Color(0xFF9AA0A6)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                enabled = authState !is AuthState.Loading,
                isError = authState is AuthState.Error,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F3F4),
                    unfocusedContainerColor = Color(0xFFF1F3F4),
                    disabledContainerColor = Color(0xFFF1F3F4),
                    errorContainerColor = Color(0xFFF1F3F4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF4285F4)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { sendOtp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4),
                    contentColor = Color.White
                ),
                enabled = authState !is AuthState.Loading,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Send Email OTP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        onLoginSuccess = {},
        onOtpSent = {},
        authState = AuthState.Idle,
        email = "",
        updateEmail = {},
        sendOtp = {}
    )
}
