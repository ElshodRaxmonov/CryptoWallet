package com.example.cryptowallet.ui.screens.wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptowallet.R
import com.example.cryptowallet.data.model.WalletState
import com.example.cryptowallet.ui.theme.CryptoWalletAppTheme

@Composable
fun WalletDetailsScreen(
    onSendTransaction: () -> Unit,
    onLogout: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val walletState by viewModel.walletState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    WalletDetailsScreenContent(
        walletState = walletState,
        onSendTransaction = onSendTransaction,
        onLogout = onLogout,
        isRefreshing = isRefreshing,
        refresh = { viewModel.loadWalletInfo() },
        logout = { onComplete -> viewModel.logout(onComplete) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletDetailsScreenContent(
    walletState: WalletState,
    onSendTransaction: () -> Unit,
    onLogout: () -> Unit,
    isRefreshing: Boolean,
    refresh: () -> Unit,
    logout: (onComplete: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        text = stringResource(R.string.wallet_details_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { refresh() },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = walletState) {
                is WalletState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is WalletState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WalletInfoCard(state = state)

                        ActionRow(
                            icon = R.drawable.ic_copy,
                            text = stringResource(R.string.copy_address),
                            onClick = { copyToClipboard(context, state.address) }
                        )

                        Button(
                            onClick = onSendTransaction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.send_transaction_button),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }

                        ActionRow(
                            icon = R.drawable.ic_logout,
                            text = stringResource(R.string.logout_button),
                            contentColor = Color(0xFFD93025),
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }

                is WalletState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { refresh() }) { Text("Retry") }
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_title)) },
            text = { Text(stringResource(R.string.logout_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        logout(onLogout)
                    }
                ) {
                    Text(stringResource(R.string.logout_confirm), color = Color(0xFFD93025))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }
}

@Composable
fun WalletInfoCard(state: WalletState.Success) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFEBF2FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.evm_label),
                    color = Color(0xFF4285F4),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            InfoSection(
                label = stringResource(R.string.address_label),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = state.address,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            InfoSection(
                label = stringResource(R.string.current_network_label),
                value = "${state.network} - ${state.chainId}"
            )

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)

            InfoSection(
                label = stringResource(R.string.balance_label),
                value = "${state.balance} ETH",
                valueColor = Color(0xFF4285F4),
                valueStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun InfoSection(
    label: String,
    value: String? = null,
    valueColor: Color = Color.Black,
    valueStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Medium
    ),
    content: @Composable (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )
        if (content != null) {
            content()
        } else if (value != null) {
            Text(
                text = value,
                style = valueStyle,
                color = valueColor
            )
        }
    }
}

@Composable
fun ActionRow(
    icon: Int,
    text: String,
    contentColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Wallet Address", text)
    clipboardManager.setPrimaryClip(clipData)
}

@Preview(showBackground = true)
@Composable
fun WalletDetailsScreenPreview() {
    CryptoWalletAppTheme {
        WalletDetailsScreenContent(
            walletState = WalletState.Success(
                address = "0x6E6F148c27fB49c9760371A9723d08C4d5Af8b4",
                balance = "1.18769409",
                network = "Sepolia",
                chainId = 11155111L
            ),
            onSendTransaction = {},
            onLogout = {},
            isRefreshing = false,
            refresh = {},
            logout = {}
        )
    }
}
