package com.example.cryptowallet.data.repository

import android.util.Log
import com.dynamic.sdk.android.Chains.EVM.EthereumTransaction
import com.dynamic.sdk.android.Chains.EVM.convertEthToWei
import com.dynamic.sdk.android.DynamicSDK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor() {

    private var dynamicSDK: DynamicSDK? = null

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    /**
     * Initialize Dynamic SDK
     */
    fun initialize(sdk: DynamicSDK) {
        this.dynamicSDK = sdk
    }

    private fun getSdk(): DynamicSDK {
        return dynamicSDK
            ?: throw IllegalStateException("Dynamic SDK not initialized. Call initialize() first.")
    }

    /**
     * Send OTP to email
     */
    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            getSdk().auth.email.sendOTP(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verify OTP code
     */
    suspend fun verifyOtp(code: String): Result<Unit> {
        return try {
            getSdk().auth.email.verifyOTP(code)
            _isAuthenticated.value = true

            // Log available wallets after authentication
            val wallets = getSdk().wallets.userWallets
            Log.d("WalletRepository", "Authenticated. Available wallets: ${wallets.size}")
            wallets.forEach {
                Log.d("WalletRepository", "Wallet: address=${it.address}, chain=${it.chain}")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get wallet address
     */
    suspend fun getWalletAddress(): Result<String> {
        return try {
            val wallets = getSdk().wallets.userWallets
            val wallet = wallets.firstOrNull {
                it.chain.uppercase() == "EVM"
            }

            if (wallet == null) {
                return Result.failure(Exception("No EVM wallet found"))
            }

            Result.success(wallet.address)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get wallet balance in ETH
     */
    suspend fun getBalance(walletAddress: String?): Result<String> {
        return try {
            Log.d("WalletRepository", "Fetching balance for address: $walletAddress")

            // Get the EVM wallet
            val wallet = getSdk().wallets.userWallets.firstOrNull { 
                it.chain.uppercase() == "EVM" 
            }

            if (wallet == null) {
                Log.e("WalletRepository", "No EVM wallet found")
                return Result.failure(Exception("No EVM wallet found. Please ensure you're authenticated and have an EVM wallet."))
            }

            // Fetch balance from the SDK
            val balance = getSdk().wallets.getBalance(wallet)
            Log.d("WalletRepository", "Balance retrieved: $balance ETH")
            Result.success(balance)
        } catch (e: Exception) {
            Log.e("WalletRepository", "Error fetching balance: ${e.message}", e)
            Result.failure(Exception("Failed to fetch balance: ${e.message}", e))
        }
    }

    /**
     * Send ETH transaction
     */
    suspend fun sendTransaction(
        to: String,
        amount: String
    ): Result<String> {
        return try {
            val wallet = getSdk().wallets.userWallets.firstOrNull {
                it.chain.uppercase() == "EVM"
            }

            if (wallet == null) {
                Log.e("WalletRepository", "No EVM wallet found")
                return Result.failure(Exception("No EVM wallet found. Please ensure you're authenticated."))
            }

            Log.d("WalletRepository", "Preparing transaction: from=${wallet.address}, to=$to, amount=$amount ETH")

            // Fetch current gas prices from the network
            val gasPrice = try {
                getSdk().evm.getGasPrice()
            } catch (e: Exception) {
                Log.w("WalletRepository", "Failed to fetch gas price, using fallback: ${e.message}")
                // Use fallback gas prices if network call fails
                null
            }

            // Build the transaction with proper gas parameters
            val transaction = EthereumTransaction(
                from = wallet.address,
                to = to,
                value = convertEthToWei(amount),
                gas = BigInteger.valueOf(21000), // Standard gas limit for ETH transfer
                maxFeePerGas = gasPrice?.maxFeePerGas,
                maxPriorityFeePerGas = gasPrice?.maxPriorityFeePerGas
            )

            Log.d("WalletRepository", "Sending transaction with gas: maxFee=${gasPrice?.maxFeePerGas} wei, maxPriority=${gasPrice?.maxPriorityFeePerGas} wei")

            // Send the transaction via Dynamic SDK
            val txHash = getSdk().evm.sendTransaction(transaction, wallet)
            
            if (txHash.isNullOrBlank()) {
                Log.e("WalletRepository", "Transaction failed: No hash returned")
                return Result.failure(Exception("Transaction failed: No transaction hash returned from the network"))
            }
            
            Log.d("WalletRepository", "Transaction successful. Hash: $txHash")
            Result.success(txHash)
        } catch (e: Exception) {
            Log.e("WalletRepository", "Transaction failed: ${e.message}", e)
            
            // Provide more specific error messages based on the exception
            val errorMessage = when {
                e.message?.contains("insufficient", ignoreCase = true) == true -> 
                    "Insufficient funds. Please check your balance and try again."
                e.message?.contains("rejected", ignoreCase = true) == true -> 
                    "Transaction rejected. Please try again."
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Network error. Please check your connection and try again."
                else -> 
                    "Transaction failed: ${e.message ?: "Unknown error"}"
            }
            
            Result.failure(Exception(errorMessage, e))
        }
    }

    /**
     * Logout user
     */
    suspend fun logout(): Result<Unit> {
        return try {
            getSdk().auth.logout()
            _isAuthenticated.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if user is authenticated
     */
    fun observeAuthState(): Flow<Boolean> {
        return isAuthenticated
    }
}
