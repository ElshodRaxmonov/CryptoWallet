package com.example.cryptowallet.data.repository

import com.dynamic.sdk.android.Chains.EVM.EthereumTransaction
import com.dynamic.sdk.android.Chains.EVM.convertEthToWei
import com.dynamic.sdk.android.DynamicSDK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor() {

    private var dynamicSDK: DynamicSDK? = null

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun initialize(sdk: DynamicSDK) {
        this.dynamicSDK = sdk
    }

    private fun getSdk(): DynamicSDK {
        return dynamicSDK
            ?: throw IllegalStateException("Dynamic SDK not initialized")
    }

    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            getSdk().auth.email.sendOTP(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(code: String): Result<Unit> {
        return try {
            getSdk().auth.email.verifyOTP(code)
            _isAuthenticated.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

     fun getWalletAddress(): Result<String> {
        return try {
            val wallet = getSdk().wallets.userWallets.firstOrNull {
                it.chain.uppercase() == "EVM"
            } ?: return Result.failure(Exception("No EVM wallet found"))
            
            Result.success(wallet.address)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBalance(walletAddress: String?): Result<String> {
        return try {
            val wallet = getSdk().wallets.userWallets.firstOrNull {
                it.chain.uppercase() == "EVM"
            } ?: return Result.failure(Exception("No EVM wallet found"))

            val balance = getSdk().wallets.getBalance(wallet)
            Result.success(balance)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch balance: ${e.message}", e))
        }
    }

    suspend fun sendTransaction(
        to: String,
        amount: String
    ): Result<String> = withContext(Dispatchers.IO) {

        try {
            val sdk = getSdk()
            val wallet = sdk.wallets.userWallets.firstOrNull { it.chain.uppercase() == "EVM" }
                ?: return@withContext Result.failure(Exception("No EVM wallet found"))

            val chainId = try {
                val network = sdk.wallets.getNetwork(wallet)
                val jsonValue = network.value
                if (jsonValue is JsonPrimitive) {
                    jsonValue.intOrNull ?: jsonValue.contentOrNull?.toIntOrNull() ?: 1
                } else 1
            } catch (e: Exception) {
                1
            }


            val client = sdk.evm.createPublicClient(chainId)
            val gasPrice = client.getGasPrice()
            val maxFeePerGas = gasPrice * BigInteger.valueOf(2)

            // Create transaction
            val transaction = EthereumTransaction(
                from = wallet.address,
                to = to,
                value = convertEthToWei(amount),
                gas = BigInteger.valueOf(21000),
                maxFeePerGas = maxFeePerGas,
                maxPriorityFeePerGas = gasPrice
            )

            val txHash = withTimeoutOrNull(120000L) {
                try {
                    sdk.evm.sendTransaction(transaction, wallet)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    throw e
                }
            } ?: return@withContext Result.failure(Exception("Transaction timed out"))

            if (txHash.isBlank()) {
                return@withContext Result.failure(Exception("No transaction hash returned"))
            }

            Result.success(txHash)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            getSdk().auth.logout()
            _isAuthenticated.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAuthState(): Flow<Boolean> = isAuthenticated
}
