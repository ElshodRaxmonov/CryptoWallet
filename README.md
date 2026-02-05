# 🚀 Crypto Wallet Android App

A modern Android application demonstrating Web3 authentication and crypto wallet functionality using Dynamic SDK on Ethereum Sepolia testnet.

## 📱 Features

- **Email OTP Authentication** via Dynamic SDK
- **Wallet Details** - View address, network, and balance
- **Send Transactions** - Transfer ETH on Sepolia testnet
- **Modern UI** - Built with Jetpack Compose and Material Design 3
- **MVVM Architecture** - Clean, testable, and maintainable code

## 🏗️ Architecture

### MVVM Pattern
```
├── UI Layer (Compose)
│   ├── LoginScreen
│   ├── WalletDetailsScreen
│   └── SendTransactionScreen
├── ViewModel Layer
│   ├── LoginViewModel
│   ├── WalletViewModel
│   └── SendTransactionViewModel
├── Repository Layer
│   └── WalletRepository
└── Data Layer
    └── Dynamic SDK Integration
```

### Key Technologies
- **Kotlin** - 100% Kotlin
- **Jetpack Compose** - Modern declarative UI
- **Coroutines & Flow** - Asynchronous operations
- **StateFlow** - State management
- **Hilt** - Dependency injection
- **Dynamic SDK** - Web3 authentication and wallet operations
- **Material Design 3** - Modern UI components

### Data Flow
1. UI emits user actions to ViewModel
2. ViewModel processes business logic and calls Repository
3. Repository interacts with Dynamic SDK
4. Results flow back through StateFlow to UI

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK API 34
- Git

### Setup Instructions

#### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd CryptoWalletApp
```

#### 2. Configure Dynamic SDK

1. **Create Dynamic Account**
   - Go to https://app.dynamic.xyz
   - Sign up for free
   - Create a new project
   - Copy your Environment ID

2. **Update Configuration**
   - Open `local.properties`
   - Add your Dynamic Environment ID:
   ```properties
   DYNAMIC_ENVIRONMENT_ID=your_environment_id_here
   ```

   Or update directly in `MainActivity.kt`:
   ```kotlin
   environmentId = "YOUR_ENVIRONMENT_ID"
   ```

#### 3. Build and Run
```bash
./gradlew assembleDebug
```

Or use Android Studio:
- Open project
- Wait for Gradle sync
- Run on emulator or device (API 26+)

### Get Test Tokens (Sepolia ETH)

After logging in, you'll need test ETH to send transactions:

**Option A: Google Cloud Faucet** (Recommended)
1. Visit https://cloud.google.com/application/web3/faucet/ethereum/sepolia
2. Sign in with Google
3. Paste your wallet address
4. Receive 0.05 SepoliaETH instantly

**Option B: Alchemy Faucet**
1. Visit https://www.alchemy.com/faucets/ethereum-sepolia
2. Sign in
3. Paste wallet address
4. Get 0.5 SepoliaETH

**Verify Balance:**
Check on Etherscan: `https://sepolia.etherscan.io/address/YOUR_ADDRESS`

## 📸 Screenshots

### Login Screen
- Email input field
- Send OTP button
- OTP verification dialog
- Error handling with user-friendly messages

### Wallet Details Screen
- Wallet address display with copy button
- Current network indicator (Sepolia)
- ETH balance display
- Send Transaction navigation
- Logout functionality

### Send Transaction Screen
- Recipient address input
- Amount input (ETH)
- Transaction confirmation
- Loading states during submission
- Success with transaction hash
- Error handling

## 🛠️ Project Structure

```
app/src/main/java/com/example/cryptowallet/
├── data/
│   ├── model/
│   │   ├── AuthState.kt
│   │   ├── WalletState.kt
│   │   └── TransactionState.kt
│   └── repository/
│       └── WalletRepository.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── components/
│   │   ├── LoadingButton.kt
│   │   ├── WalletCard.kt
│   │   └── TransactionStatusDialog.kt
│   ├── screens/
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   ├── wallet/
│   │   │   ├── WalletDetailsScreen.kt
│   │   │   └── WalletViewModel.kt
│   │   └── send/
│   │       ├── SendTransactionScreen.kt
│   │       └── SendTransactionViewModel.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

## 🔧 Configuration

### Network Settings
- **Network:** Ethereum Sepolia Testnet
- **Chain ID:** 11155111
- **RPC URL:** Automatic via Dynamic SDK
- **Block Explorer:** https://sepolia.etherscan.io

### Dynamic SDK Configuration
```kotlin
ClientProps(
    environmentId = "YOUR_ENV_ID",
    appName = "Crypto Wallet Test",
    redirectUrl = "dynamictest://",
    appOrigin = "https://test.app",
    logLevel = LoggerLevel.DEBUG
)
```

## 🧪 Testing

### Manual Testing Checklist
- [ ] Email OTP sends successfully
- [ ] OTP verification works
- [ ] Wallet address displays correctly
- [ ] Balance loads from Sepolia
- [ ] Copy address to clipboard
- [ ] Send transaction with valid inputs
- [ ] Error handling for invalid inputs
- [ ] Logout clears session
- [ ] Network status shows "Sepolia"

### Test Accounts
Use any valid email for OTP testing. No pre-registration needed.

## ⚠️ Known Limitations & Assumptions

### Assumptions
1. **Single EVM Wallet** - App assumes user has one EVM wallet
2. **Sepolia Network** - Hardcoded to Sepolia testnet (Chain ID: 11155111)
3. **Standard Gas** - Uses fixed gas limit of 21,000 for ETH transfers
4. **Email OTP Only** - Only email authentication implemented (no social logins)

### Limitations
1. **No Transaction History** - Doesn't display past transactions
2. **No Token Support** - ETH only, no ERC-20 tokens
3. **Basic Validation** - Address validation could be more robust
4. **No Offline Mode** - Requires internet connection
5. **No Gas Estimation** - Uses default gas values

### Security Notes
- Never commit `local.properties` with real API keys
- This is a test app - not production-ready
- Private keys managed entirely by Dynamic SDK
- Always verify transaction details before sending

## 🐛 Troubleshooting

### Common Issues

**"SDK not initialized"**
- Ensure `DynamicSDK.initialize()` is called in `MainActivity.onCreate()`
- Check Environment ID is correct

**"No balance showing"**
- Verify you're on Sepolia testnet
- Get test ETH from faucets
- Wait 1-2 minutes for balance to update

**"Transaction failed"**
- Ensure sufficient balance (amount + gas)
- Check recipient address is valid
- Verify you have test ETH

**Build errors**
- Clean and rebuild: `./gradlew clean build`
- Invalidate caches in Android Studio
- Check Gradle sync completed successfully

## 📚 Resources

- [Dynamic SDK Documentation](https://github.com/dynamic-labs/android-sdk-and-sample-app)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Sepolia Testnet](https://sepolia.dev/)
- [Etherscan Sepolia](https://sepolia.etherscan.io/)

## 🤝 Contributing

This is a test assignment submission. For improvements or issues:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## 📄 License

This project is created as a test assignment and is provided as-is for evaluation purposes.

## 👤 Author

[Your Name]
- Email: [Your Email]
- GitHub: [Your GitHub]

## 🙏 Acknowledgments

- Dynamic Labs for the excellent Web3 SDK
- Ethereum Foundation for Sepolia testnet
- Material Design team for design guidelines

---

**Time Spent:** ~4 days
**Last Updated:** February 2026
