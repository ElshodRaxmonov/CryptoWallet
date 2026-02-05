package com.example.cryptowallet.ui.screens.send

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptowallet.R
import com.example.cryptowallet.data.model.TransactionState
import com.example.cryptowallet.ui.theme.CryptoWalletAppTheme

@Composable
fun SendTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SendTransactionViewModel = hiltViewModel()
) {
    val transactionState by viewModel.transactionState.collectAsState()
    val recipientAddress by viewModel.recipientAddress.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    SendTransactionScreenContent(
        recipientAddress = recipientAddress,
        amount = amount,
        transactionState = transactionState,
        onRecipientAddressChange = viewModel::updateRecipientAddress,
        onAmountChange = viewModel::updateAmount,
        onSendClick = viewModel::sendTransaction,
        onNavigateBack = onNavigateBack,
        onCopyHash = { hash -> clipboardManager.setText(AnnotatedString(hash)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendTransactionScreenContent(
    recipientAddress: String,
    amount: String,
    transactionState: TransactionState,
    onRecipientAddressChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onCopyHash: (String) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.send_transaction_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputBox(
                label = stringResource(R.string.recipient_address_hint),
                value = recipientAddress,
                onValueChange = onRecipientAddressChange,
                enabled = transactionState !is TransactionState.Loading
            )

            InputBox(
                label = stringResource(R.string.amount_hint),
                value = amount,
                onValueChange = onAmountChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = transactionState !is TransactionState.Loading
            )

            Button(
                onClick = onSendClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4)
                ),
                enabled = transactionState !is TransactionState.Loading &&
                        recipientAddress.isNotEmpty() &&
                        amount.isNotEmpty()
            ) {
                if (transactionState is TransactionState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.send_button),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            if (transactionState is TransactionState.Success) {
                TransactionSuccessCard(
                    txHash = transactionState.txHash,
                    onCopyHash = onCopyHash
                )
            }

            if (transactionState is TransactionState.Error) {
                Text(
                    text = transactionState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InputBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.DarkGray)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun TransactionSuccessCard(
    txHash: String,
    onCopyHash: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(16.dp)),
        color = Color(0xFFF1F8E9),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.transaction_success),
                    color = Color(0xFF43A047),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.transaction_hash),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE8ECEF), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (txHash.length > 20) "${txHash.take(10)}...${txHash.takeLast(6)}" else txHash,
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                            color = Color.DarkGray
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onCopyHash(txHash) },
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_copy),
                                contentDescription = "Copy",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.clickable { /* Handle link */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.view_on_etherscan),
                    color = Color(0xFF4285F4),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_external_link),
                    contentDescription = null,
                    tint = Color(0xFF4285F4),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SendTransactionScreenPreview() {
    CryptoWalletAppTheme {
        SendTransactionScreenContent(
            recipientAddress = "0x742d355cc634C0032925a3b844Bc9e755595f0bDd7",
            amount = "0.001",
            transactionState = TransactionState.Idle,
            onRecipientAddressChange = {},
            onAmountChange = {},
            onSendClick = {},
            onNavigateBack = {},
            onCopyHash = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SendTransactionSuccessPreview() {
    CryptoWalletAppTheme {
        SendTransactionScreenContent(
            recipientAddress = "0x742d355cc634C0032925a3b844Bc9e755595f0bDd7",
            amount = "0.001",
            transactionState = TransactionState.Success("0xabc123...def456"),
            onRecipientAddressChange = {},
            onAmountChange = {},
            onSendClick = {},
            onNavigateBack = {},
            onCopyHash = {}
        )
    }
}
