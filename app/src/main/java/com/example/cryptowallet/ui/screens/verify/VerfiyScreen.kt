package com.example.cryptowallet.ui.screens.verify

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptowallet.R
import com.example.cryptowallet.data.model.AuthState
import com.example.cryptowallet.ui.screens.login.LoginViewModel
import com.example.cryptowallet.ui.theme.CryptoWalletAppTheme

@Composable
fun VerifyScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val email by viewModel.email.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    VerifyScreenContent(
        email = email,
        authState = authState,
        onBackClick = {
            viewModel.resetAuthState()
            onBackClick()
        },
        onVerifyClick = { viewModel.verifyOtp(it) },
        onResendClick = { viewModel.sendOtp() }
    )
}

@Composable
fun VerifyScreenContent(
    email: String,
    authState: AuthState,
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var otpValue by remember { mutableStateOf("") }
    val isLoading = authState is AuthState.Loading
    val errorMessage = (authState as? AuthState.Error)?.message

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, enabled = !isLoading) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
                Text(
                    text = stringResource(R.string.verify_email_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3C4043)
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE8F0FE), Color(0xFFD2E3FC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mail_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.check_your_email),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3C4043)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.verification_code_sent),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF5F6368)
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF5F6368)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OtpInputRow(
                value = otpValue,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otpValue = it
                    }
                },
                enabled = !isLoading
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onVerifyClick(otpValue) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A89F3)
                ),
                enabled = otpValue.length == 6 && !isLoading,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.verify_code_button),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.didnt_receive_code),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF5F6368)
                )
            )

            TextButton(onClick = onResendClick, enabled = !isLoading) {
                Text(
                    text = stringResource(R.string.resend_code),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A89F3),
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

@Composable
fun OtpInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = enabled,
        decorationBox = {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                repeat(6) { index ->
                    val char = value.getOrNull(index)
                    val isFocused = value.length == index
                    OtpBox(
                        char = char?.toString() ?: "",
                        isFocused = isFocused
                    )
                }
            }
        }
    )
}

@Composable
fun OtpBox(
    char: String,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(45.dp) // Adjusted size for 6 digits
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) Color(0xFF4A89F3) else Color(0xFFDDE2E9),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.ifEmpty { "—" },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Normal,
                color = if (char.isEmpty()) Color(0xFFDDE2E9) else Color(0xFF3C4043)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyScreenPreview() {
    CryptoWalletAppTheme {
        VerifyScreenContent(
            email = "test@example.com",
            authState = AuthState.Idle,
            onBackClick = {},
            onVerifyClick = {},
            onResendClick = {}
        )
    }
}
