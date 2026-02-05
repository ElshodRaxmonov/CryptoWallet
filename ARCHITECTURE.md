# Architecture Documentation

## Overview

This document describes the architecture and design decisions for the Crypto Wallet Android application.

## Architecture Pattern: MVVM (Model-View-ViewModel)

### Why MVVM?

- **Separation of Concerns**: Clear separation between UI and business logic
- **Testability**: ViewModels can be unit tested independently
- **Lifecycle Awareness**: Survives configuration changes
- **Reactive**: State flows naturally from ViewModel to UI
- **Android Recommended**: Official Google recommendation for Android apps

### Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ LoginScreen  │  │WalletDetails │  │SendTransaction│  │
│  │              │  │    Screen    │  │    Screen     │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬────────┘  │
│         │                 │                  │           │
└─────────┼─────────────────┼──────────────────┼───────────┘
          │                 │                  │
          ▼                 ▼                  ▼
┌─────────────────────────────────────────────────────────┐
│                    ViewModel Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │LoginViewModel│  │WalletViewModel│  │SendTxViewModel│  │
│  │              │  │              │  │              │  │
│  │ StateFlow    │  │ StateFlow    │  │ StateFlow    │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬────────┘  │
│         │                 │                  │           │
└─────────┼─────────────────┼──────────────────┼───────────┘
          │                 │                  │
          └─────────────────┼──────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│                 ┌──────────────────┐                     │
│                 │ WalletRepository │                     │
│                 │                  │                     │
│                 │  Business Logic  │                     │
│                 └────────┬─────────┘                     │
│                          │                               │
└──────────────────────────┼───────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│                   ┌──────────────┐                       │
│                   │ Dynamic SDK  │                       │
│                   │              │                       │
│                   │   Web3 API   │                       │
│                   └──────────────┘                       │
└─────────────────────────────────────────────────────────┘
```

## Layer Breakdown

### 1. UI Layer (Jetpack Compose)

**Responsibility**: Display data and capture user interactions

**Components**:
- **Screens**: Full-screen composables
- **Components**: Reusable UI pieces
- **Theme**: Colors, typography, shapes

**Key Files**:
```
ui/
├── screens/
│   ├── login/
│   │   └── LoginScreen.kt          # Email OTP login UI
│   ├── wallet/
│   │   └── WalletDetailsScreen.kt  # Display wallet info
│   └── send/
│       └── SendTransactionScreen.kt # Transaction form
├── navigation/
│   └── NavGraph.kt                 # App navigation
└── theme/
    ├── Color.kt                    # Color palette
    ├── Type.kt                     # Typography
    └── Theme.kt                    # Material theme
```

**State Management**:
```kotlin
// UI observes ViewModel state
val walletState by viewModel.walletState.collectAsState()

when (walletState) {
    is WalletState.Loading -> LoadingIndicator()
    is WalletState.Success -> DisplayWallet(walletState.data)
    is WalletState.Error -> ErrorMessage(walletState.message)
}
```

### 2. ViewModel Layer

**Responsibility**: Hold and manage UI state, handle user actions

**Characteristics**:
- Lifecycle-aware
- Survives configuration changes
- Exposes immutable state via StateFlow
- Uses Kotlin Coroutines for async operations

**Pattern**:
```kotlin
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel() {
    
    // Private mutable state
    private val _state = MutableStateFlow<WalletState>(WalletState.Loading)
    
    // Public immutable state
    val state: StateFlow<WalletState> = _state.asStateFlow()
    
    // User action
    fun loadWallet() {
        viewModelScope.launch {
            _state.value = WalletState.Loading
            
            repository.getWalletInfo()
                .onSuccess { info ->
                    _state.value = WalletState.Success(info)
                }
                .onFailure { error ->
                    _state.value = WalletState.Error(error.message)
                }
        }
    }
}
```

**ViewModels**:

1. **LoginViewModel**
   - Manages authentication state
   - Handles OTP send/verify
   - Email validation

2. **WalletViewModel**
   - Loads wallet information
   - Manages refresh state
   - Handles logout

3. **SendTransactionViewModel**
   - Form validation
   - Transaction submission
   - Success/error handling

### 3. Repository Layer

**Responsibility**: Single source of truth for data operations

**Characteristics**:
- Abstracts data sources
- Handles business logic
- Provides clean API to ViewModels
- Error handling and mapping

**Pattern**:
```kotlin
@Singleton
class WalletRepository @Inject constructor() {
    
    private lateinit var sdk: DynamicSDK
    
    suspend fun sendTransaction(
        to: String,
        amount: String
    ): Result<String> {
        return try {
            // Business logic
            val wallet = getEVMWallet()
            val valueInWei = convertEthToWei(amount)
            val gasPrice = sdk.evm.getGasPrice()
            
            // Create and send transaction
            val transaction = createTransaction(wallet, to, valueInWei, gasPrice)
            val txHash = sdk.evm.sendTransaction(transaction, wallet)
            
            Result.success(txHash)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 4. Data Layer (Dynamic SDK)

**Responsibility**: Interact with blockchain and authentication services

**Operations**:
- Email OTP authentication
- Wallet management
- Balance queries
- Transaction broadcasting

## Data Flow

### Authentication Flow

```
User enters email
      │
      ▼
LoginScreen captures input
      │
      ▼
LoginViewModel.sendOtp()
      │
      ▼
WalletRepository.sendOtp(email)
      │
      ▼
Dynamic SDK sends OTP
      │
      ▼
User receives email
      │
      ▼
User enters OTP
      │
      ▼
LoginViewModel.verifyOtp(code)
      │
      ▼
WalletRepository.verifyOtp(code)
      │
      ▼
Dynamic SDK verifies
      │
      ▼
StateFlow emits Authenticated
      │
      ▼
UI navigates to WalletScreen
```

### Transaction Flow

```
User fills form
      │
      ▼
SendTransactionScreen captures input
      │
      ▼
SendTransactionViewModel validates
      │
      ▼
SendTransactionViewModel.sendTransaction()
      │
      ▼
WalletRepository.sendTransaction(to, amount)
      │
      ├─> Convert ETH to Wei
      ├─> Get gas prices
      ├─> Create EthereumTransaction
      └─> Call Dynamic SDK
      │
      ▼
Dynamic SDK broadcasts to Sepolia
      │
      ▼
Transaction mined
      │
      ▼
StateFlow emits Success(txHash)
      │
      ▼
UI shows success dialog
```

## State Management

### State Pattern

Each screen has associated states representing possible UI conditions:

```kotlin
// Authentication
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

// Wallet
sealed class WalletState {
    object Loading : WalletState()
    data class Success(
        val address: String,
        val balance: String,
        val network: String,
        val chainId: Long
    ) : WalletState()
    data class Error(val message: String) : WalletState()
}

// Transaction
sealed class TransactionState {
    object Idle : TransactionState()
    object Loading : TransactionState()
    data class Success(val txHash: String) : TransactionState()
    data class Error(val message: String) : TransactionState()
}
```

### Benefits of Sealed Classes

1. **Exhaustive when expressions**: Compiler ensures all cases handled
2. **Type safety**: Each state can carry specific data
3. **Clear intent**: Explicit possible states
4. **No null checks**: State is always valid

## Dependency Injection (Hilt)

### Why Hilt?

- **Official Google recommendation** for DI in Android
- **Less boilerplate** than Dagger
- **Compile-time safety**
- **Integration with Jetpack** (ViewModel, Navigation)

### DI Setup

```kotlin
// Application class
@HiltAndroidApp
class CryptoWalletApplication : Application()

// Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// ViewModel
@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository
) : ViewModel()

// Repository
@Singleton
class WalletRepository @Inject constructor()
```

### Dependency Graph

```
Application
    │
    ├─> WalletRepository (Singleton)
    │       │
    │       └─> Dynamic SDK
    │
    └─> MainActivity
            │
            └─> ViewModels (via Hilt)
                    │
                    └─> WalletRepository
```

## Navigation Architecture

### Navigation Graph

```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object WalletDetails : Screen("wallet_details")
    object SendTransaction : Screen("send_transaction")
}

NavHost(navController, startDestination = Screen.Login.route) {
    composable(Screen.Login.route) { LoginScreen(...) }
    composable(Screen.WalletDetails.route) { WalletDetailsScreen(...) }
    composable(Screen.SendTransaction.route) { SendTransactionScreen(...) }
}
```

### Navigation Flow

```
┌──────────┐
│  Login   │ ──authenticated──> ┌────────────────┐
└──────────┘                    │ WalletDetails  │
                                └────────┬───────┘
                                         │
                                send transaction
                                         │
                                         ▼
                               ┌──────────────────┐
                               │ SendTransaction  │
                               └──────────────────┘
```

## Error Handling Strategy

### Layered Error Handling

1. **Data Layer**: Catch exceptions, return Result
```kotlin
suspend fun getData(): Result<Data> {
    return try {
        Result.success(api.getData())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

2. **Repository Layer**: Map errors to domain errors
```kotlin
.onFailure { error ->
    when (error) {
        is NetworkException -> DomainError.Network
        is AuthException -> DomainError.Unauthorized
        else -> DomainError.Unknown
    }
}
```

3. **ViewModel Layer**: Convert to UI state
```kotlin
.onFailure { error ->
    _state.value = State.Error(error.toUserMessage())
}
```

4. **UI Layer**: Display user-friendly messages
```kotlin
if (state is Error) {
    Text("Failed to load wallet. Please try again.")
}
```

## Threading Model

### Coroutines and Dispatchers

```kotlin
// Repository operations (I/O bound)
viewModelScope.launch {
    withContext(Dispatchers.IO) {
        val data = api.getData()
    }
}

// Default dispatcher for CPU-intensive work
withContext(Dispatchers.Default) {
    val result = complexCalculation()
}

// Main dispatcher for UI updates (default in viewModelScope)
_state.value = State.Success(data)
```

## Testing Strategy

### Unit Tests

**ViewModels**:
```kotlin
@Test
fun `sendOtp with valid email updates state to OtpSent`() = runTest {
    // Given
    val email = "test@example.com"
    coEvery { repository.sendOtp(email) } returns Result.success(Unit)
    
    // When
    viewModel.updateEmail(email)
    viewModel.sendOtp()
    
    // Then
    assertEquals(AuthState.OtpSent, viewModel.authState.value)
}
```

**Repository**:
```kotlin
@Test
fun `getBalance returns formatted ETH string`() = runTest {
    // Given
    val wei = "50000000000000000" // 0.05 ETH
    
    // When
    val result = repository.convertWeiToEth(wei)
    
    // Then
    assertEquals("0.05", result)
}
```

### Integration Tests

```kotlin
@Test
fun `complete authentication flow`() = runTest {
    // 1. Send OTP
    viewModel.sendOtp("test@example.com")
    assertEquals(AuthState.OtpSent, viewModel.authState.value)
    
    // 2. Verify OTP
    viewModel.verifyOtp("123456")
    assertEquals(AuthState.Authenticated, viewModel.authState.value)
}
```

## Performance Considerations

### Optimization Strategies

1. **State Flow vs LiveData**: StateFlow is more efficient
2. **Immutability**: Prevents unnecessary recompositions
3. **Remember**: Cache expensive calculations
4. **LazyColumn**: For efficient lists
5. **Derivers StateFlow**: Avoid duplicate computations

```kotlin
// Efficient state transformation
val isLoading = walletState.map { it is WalletState.Loading }
    .stateIn(viewModelScope, SharingStarted.Lazily, false)
```

## Security Considerations

### Best Practices Implemented

1. **No Private Key Storage**: All handled by Dynamic SDK
2. **Network Security**: HTTPS only
3. **Input Validation**: All user inputs validated
4. **Error Messages**: No sensitive info in errors
5. **Backup Rules**: Exclude sensitive SharedPreferences

```xml
<!-- backup_rules.xml -->
<full-backup-content>
    <exclude domain="sharedpref" path="." />
</full-backup-content>
```

## Future Enhancements

### Potential Improvements

1. **Offline Support**
   - Cache wallet info
   - Queue transactions

2. **Transaction History**
   - Fetch from Etherscan API
   - Local database with Room

3. **Multi-Chain Support**
   - Polygon, BSC, etc.
   - Chain switcher

4. **Token Support**
   - ERC-20 tokens
   - NFTs (ERC-721)

5. **Advanced Features**
   - QR code scanning
   - Contact book
   - Gas estimation
   - Transaction tracking

## Conclusion

This architecture provides:
- ✅ **Separation of Concerns**: Clear layer boundaries
- ✅ **Testability**: Each layer independently testable
- ✅ **Scalability**: Easy to add new features
- ✅ **Maintainability**: Well-organized, documented code
- ✅ **Modern**: Uses latest Android best practices

The MVVM pattern combined with Jetpack Compose, Coroutines, and Hilt creates a robust, maintainable foundation for the crypto wallet application.
