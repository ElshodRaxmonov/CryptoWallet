package com.example.cryptowallet.data.model


sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object OtpSent : AuthState()
    data object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}


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

sealed class TransactionState {
    data object Idle : TransactionState()
    data object Loading : TransactionState()
    data class Success(val txHash: String) : TransactionState()
    data class Error(val message: String) : TransactionState()
}
