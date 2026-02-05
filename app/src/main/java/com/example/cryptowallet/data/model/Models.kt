package com.example.cryptowallet.data.model

/**
 * Represents the authentication state of the user
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object OtpSent : AuthState()
    data object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Represents the wallet state
 */
sealed class WalletState {
    data object Loading : WalletState()
    data class Success(
        val address: String,
        val balance: String,
        val network: String = "Sepolia",
        val chainId: Long = 11155111
    ) : WalletState()
    data class Error(val message: String) : WalletState()
}

/**
 * Represents a transaction state
 */
sealed class TransactionState {
    data object Idle : TransactionState()
    data object Loading : TransactionState()
    data class Success(val txHash: String) : TransactionState()
    data class Error(val message: String) : TransactionState()
}

/**
 * Wallet information data class
 */
data class WalletInfo(
    val address: String,
    val balance: String,
    val network: String,
    val chainId: Long
)

/**
 * Transaction data class
 */
data class Transaction(
    val from: String,
    val to: String,
    val value: String,
    val gasLimit: Long = 21000L
)
