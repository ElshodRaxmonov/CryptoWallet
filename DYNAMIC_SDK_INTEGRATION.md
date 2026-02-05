# Dynamic SDK Integration Guide

This document explains how to integrate the actual Dynamic SDK into the app. Currently, the app uses mock implementations for demonstration purposes.

## Current State

The app is structured with:
- ✅ Complete UI implementation
- ✅ MVVM architecture
- ✅ Navigation flow
- ✅ State management
- ⚠️ Mock repository methods (needs real Dynamic SDK)

## Integration Steps

### 1. Add Dynamic SDK Dependency

**Update `app/build.gradle.kts`:**

```kotlin
dependencies {
    // ... existing dependencies

    // Dynamic SDK - Check latest version at:
    // https://github.com/dynamic-labs/android-sdk-and-sample-app
    implementation("xyz.dynamic:android-sdk:VERSION")
    
    // Additional dependencies that might be required
    implementation("org.web3j:core:4.10.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
```

### 2. Update WalletRepository

Replace the mock implementation in `WalletRepository.kt`:

```kotlin
package com.example.cryptowallet.data.repository

import xyz.dynamic.sdk.DynamicSDK
import xyz.dynamic.sdk.models.ClientProps
import xyz.dynamic.sdk.auth.email.EmailAuth
import xyz.dynamic.sdk.wallets.WalletService
import xyz.dynamic.sdk.evm.EvmService
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor() {
    
    private lateinit var sdk: DynamicSDK
    
    fun initialize(sdk: DynamicSDK) {
        this.sdk = sdk
    }
    
    // Send OTP
    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            sdk.auth.email.sendOTP(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Verify OTP
    suspend fun verifyOtp(code: String): Result<Unit> {
        return try {
            sdk.auth.email.verifyOTP(code)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get wallet address
    suspend fun getWalletAddress(): Result<String> {
        return try {
            val wallet = sdk.wallets.userWallets.firstOrNull { 
                it.chain.uppercase() == "EVM" 
            }
            Result.success(wallet?.address ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get balance
    suspend fun getBalance(walletAddress: String): Result<String> {
        return try {
            val wallet = sdk.wallets.userWallets.firstOrNull { 
                it.chain.uppercase() == "EVM" 
            }
            
            if (wallet == null) {
                return Result.failure(Exception("No EVM wallet found"))
            }
            
            // Get balance in Wei
            val balanceWei = sdk.wallets.getBalance(wallet)
            
            // Convert Wei to ETH
            val balanceEth = convertWeiToEth(balanceWei)
            
            Result.success(balanceEth)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Send transaction
    suspend fun sendTransaction(to: String, amount: String): Result<String> {
        return try {
            val wallet = sdk.wallets.userWallets.firstOrNull { 
                it.chain.uppercase() == "EVM" 
            }
            
            if (wallet == null) {
                return Result.failure(Exception("No EVM wallet found"))
            }
            
            // Convert ETH to Wei
            val valueInWei = convertEthToWei(amount)
            
            // Get current gas prices
            val gasPrice = sdk.evm.getGasPrice()
            
            // Create transaction
            val transaction = EthereumTransaction(
                from = wallet.address,
                to = to,
                value = valueInWei,
                gas = BigInteger.valueOf(21000), // Standard gas for ETH transfer
                maxFeePerGas = gasPrice.maxFeePerGas,
                maxPriorityFeePerGas = gasPrice.maxPriorityFeePerGas
            )
            
            // Send transaction
            val txHash = sdk.evm.sendTransaction(transaction, wallet)
            
            Result.success(txHash)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Logout
    suspend fun logout(): Result<Unit> {
        return try {
            sdk.auth.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Observe auth state
    fun observeAuthState(): Flow<Boolean> {
        return sdk.auth.isAuthenticatedFlow
    }
    
    // Helper: Convert Wei to ETH
    private fun convertWeiToEth(wei: String): String {
        val weiValue = BigInteger(wei)
        val ethValue = weiValue.toBigDecimal().divide(
            BigInteger("1000000000000000000").toBigDecimal()
        )
        return String.format("%.6f", ethValue.toDouble())
    }
    
    // Helper: Convert ETH to Wei
    private fun convertEthToWei(eth: String): BigInteger {
        val ethValue = eth.toBigDecimal()
        return ethValue.multiply(
            BigInteger("1000000000000000000").toBigDecimal()
        ).toBigInteger()
    }
}
```

### 3. Update MainActivity

Initialize the Dynamic SDK in `MainActivity.kt`:

```kotlin
package com.example.cryptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cryptowallet.data.repository.WalletRepository
import com.example.cryptowallet.ui.navigation.NavGraph
import com.example.cryptowallet.ui.theme.CryptoWalletAppTheme
import dagger.hilt.android.AndroidEntryPoint
import xyz.dynamic.sdk.DynamicSDK
import xyz.dynamic.sdk.models.ClientProps
import xyz.dynamic.sdk.models.LoggerLevel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var walletRepository: WalletRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Dynamic SDK
        val props = ClientProps(
            environmentId = BuildConfig.DYNAMIC_ENVIRONMENT_ID, // From local.properties
            appName = "Crypto Wallet Test",
            redirectUrl = "dynamictest://",
            appOrigin = "https://test.app",
            logLevel = LoggerLevel.DEBUG
        )
        
        val sdk = DynamicSDK.initialize(props, applicationContext, this)
        
        // Inject SDK into repository
        walletRepository.initialize(sdk)
        
        setContent {
            CryptoWalletAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
```

### 4. Update Build Config

To use the Environment ID from `local.properties`:

**Add to `app/build.gradle.kts`:**

```kotlin
android {
    // ... existing config
    
    defaultConfig {
        // ... existing config
        
        // Read from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        
        buildConfigField(
            "String",
            "DYNAMIC_ENVIRONMENT_ID",
            "\"${properties.getProperty("DYNAMIC_ENVIRONMENT_ID", "")}\""
        )
    }
    
    buildFeatures {
        buildConfig = true
        compose = true
    }
}
```

### 5. Network Configuration

Ensure the app connects to Sepolia testnet:

```kotlin
// The Dynamic SDK should automatically handle network switching
// But you can verify/force Sepolia network:

suspend fun ensureSepoliaNetwork() {
    val currentChainId = sdk.evm.getChainId()
    val sepoliaChainId = 11155111L
    
    if (currentChainId != sepoliaChainId) {
        // Request network switch to Sepolia
        sdk.evm.switchChain(sepoliaChainId)
    }
}
```

## Testing with Real SDK

### 1. Authentication Flow

```kotlin
// In LoginViewModel
fun sendOtp() {
    viewModelScope.launch {
        _authState.value = AuthState.Loading
        
        walletRepository.sendOtp(email.value)
            .onSuccess {
                _authState.value = AuthState.OtpSent
            }
            .onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "Failed to send OTP"
                )
            }
    }
}

fun verifyOtp(code: String) {
    viewModelScope.launch {
        _authState.value = AuthState.Loading
        
        walletRepository.verifyOtp(code)
            .onSuccess {
                _authState.value = AuthState.Authenticated
            }
            .onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "Invalid OTP"
                )
            }
    }
}
```

### 2. Wallet Operations

```kotlin
// Get wallet info
val address = walletRepository.getWalletAddress().getOrNull()
val balance = walletRepository.getBalance(address!!).getOrNull()

// Send transaction
val txHash = walletRepository.sendTransaction(
    to = "0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb",
    amount = "0.001"
).getOrNull()
```

## Error Handling

Implement proper error handling for common scenarios:

```kotlin
sealed class DynamicError : Exception() {
    data class NetworkError(override val message: String) : DynamicError()
    data class AuthError(override val message: String) : DynamicError()
    data class TransactionError(override val message: String) : DynamicError()
    data class InsufficientBalance(override val message: String) : DynamicError()
}

// Usage
try {
    val result = sdk.evm.sendTransaction(transaction, wallet)
} catch (e: Exception) {
    when {
        e.message?.contains("insufficient funds") == true -> 
            throw DynamicError.InsufficientBalance("Not enough ETH for transaction")
        e.message?.contains("user rejected") == true ->
            throw DynamicError.TransactionError("Transaction cancelled by user")
        else ->
            throw DynamicError.TransactionError(e.message ?: "Transaction failed")
    }
}
```

## Additional Features to Implement

### 1. Transaction History

```kotlin
suspend fun getTransactionHistory(address: String): List<Transaction> {
    // Use Etherscan API or similar
    val url = "https://api-sepolia.etherscan.io/api?" +
            "module=account&action=txlist&address=$address" +
            "&startblock=0&endblock=99999999" +
            "&sort=desc&apikey=YOUR_API_KEY"
    
    // Fetch and parse
}
```

### 2. Gas Estimation

```kotlin
suspend fun estimateGas(transaction: Transaction): BigInteger {
    return sdk.evm.estimateGas(transaction)
}
```

### 3. Token Support (ERC-20)

```kotlin
suspend fun getTokenBalance(
    walletAddress: String,
    tokenAddress: String
): String {
    // Implement ERC-20 balance check
    val contract = sdk.evm.getContract(tokenAddress, ERC20_ABI)
    return contract.balanceOf(walletAddress).toString()
}
```

## References

- [Dynamic SDK GitHub](https://github.com/dynamic-labs/android-sdk-and-sample-app)
- [Dynamic Documentation](https://docs.dynamic.xyz/)
- [Ethereum JSON-RPC](https://ethereum.org/en/developers/docs/apis/json-rpc/)
- [Web3j Documentation](https://docs.web3j.io/)

## Common Issues

### SDK Not Initializing
- Verify Environment ID is correct
- Check internet connection
- Ensure all permissions are granted

### Transaction Failures
- Check sufficient balance
- Verify correct network (Sepolia)
- Ensure gas prices are set correctly

### OTP Not Received
- Check email configuration in Dynamic dashboard
- Verify email provider allows automated emails
- Check spam folder

---

**Note:** This integration guide should be followed after setting up your Dynamic account and obtaining valid credentials.
