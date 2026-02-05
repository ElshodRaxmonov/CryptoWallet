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

            // Re-fetch wallets to get the latest SDK wallet object
            val wallets = getSdk().wallets.userWallets

            val wallet = getSdk().wallets.userWallets.firstOrNull { it.chain.uppercase() == "EVM" }

            if (wallet == null) {
                return Result.failure(Exception("No EVM wallet found for balance check"))
            }

            // Use getBalance with a forced network sync if possible
            val balance = getSdk().wallets.getBalance(wallet)
            Log.d("WalletRepository", "Balance successfully converted: $balance ETH")
            Result.success(balance)
        } catch (e: Exception) {
            Log.e("WalletRepository", "Fatal error in getBalance", e)
            Result.failure(e)
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
            val wallets = getSdk().wallets.userWallets
            val wallet = wallets.firstOrNull {
                it.chain.uppercase() == "EVM"
            } ?: return Result.failure(Exception("No EVM wallet found"))

            Log.d("WalletRepository", "Sending transaction: from=${wallet.address}, to=$to, amount=$amount")

            val transaction = EthereumTransaction(
                from = wallet.address,
                to = to,
                value = convertEthToWei(amount),
                gas = BigInteger.valueOf(21000)
//                maxFeePerGas = maxFeePerGas,
//                maxPriorityFeePerGas = maxPriorityFeePerGas
            )

            // Dynamic SDK sendTransaction is often a suspend function
            val txHash = getSdk().evm.sendTransaction(transaction, wallet)
            Log.d("WalletRepository", "Transaction successful. Hash: $txHash")
            
            if (txHash.isNullOrBlank()) {
                return Result.failure(Exception("Transaction failed: No hash returned from SDK"))
            }
            
            Result.success(txHash)
        } catch (e: Exception) {
            Log.e("WalletRepository", "Transaction failed in repository", e)
            // Handle "Job was cancelled" or other coroutine issues by wrapping it properly
            Result.failure(e)
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
