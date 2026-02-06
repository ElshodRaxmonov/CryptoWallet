# 🚀 Crypto Wallet Android App

A modern Android crypto wallet application built with Jetpack Compose, enabling users to authenticate via email OTP, view their wallet balance, and send ETH transactions on the Sepolia testnet using the Dynamic SDK.

---

## 📱 Features

- **Email OTP Authentication** - Secure login via one-time password
- **Wallet Management** - View wallet address, balance, and network info
- **Send Transactions** - Transfer ETH on Sepolia testnet
- **Modern UI** - Built with Jetpack Compose and Material Design 3
- **Real-time Updates** - StateFlow-based reactive architecture

---

## 🏗️ Architecture

### MVVM Pattern

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                   UI Layer                      │
│  (Composables - LoginScreen, WalletScreen, etc)│
└──────────────────┬──────────────────────────────┘
                   │ observes StateFlow
                   ▼
┌─────────────────────────────────────────────────┐
│               ViewModel Layer                   │
│  (LoginViewModel, WalletViewModel, etc)         │
│  - Business logic                               │
│  - State management (StateFlow)                 │
└──────────────────┬──────────────────────────────┘
                   │ calls suspend functions
                   ▼
┌─────────────────────────────────────────────────┐
│              Repository Layer                   │
│         (WalletRepository)                      │
│  - Data operations                              │
│  - Dynamic SDK integration                      │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│              Dynamic SDK                        │
│  - Authentication                               │
│  - Wallet operations                            │
│  - Blockchain transactions                      │
└─────────────────────────────────────────────────┘
```

### Key Components

**1. UI Layer (Compose)**
- `LoginScreen` - Email OTP authentication
- `WalletScreen` - Display balance and wallet info
- `SendTransactionScreen` - Transaction form

**2. ViewModel Layer**
- Manages UI state with `StateFlow`
- Handles user interactions
- Coordinates with Repository

**3. Repository Layer**
- Single source of truth for data
- Abstracts Dynamic SDK operations
- Error handling and mapping

**4. Dependency Injection**
- Hilt for dependency management
- `@Singleton` repository
- `@HiltViewModel` for ViewModels

---

## 🚀 How to Run

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK** API Level 34 (Android 14)
- **Dynamic SDK Account** (free at https://app.dynamic.xyz)

### Setup Steps

#### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd CryptoWalletApp
```

#### 2. Get Dynamic SDK Credentials

1. Visit https://app.dynamic.xyz and sign up
2. Create a new project
3. Copy your **Environment ID** (format: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`)
4. Enable **Email OTP** authentication in your Dynamic dashboard

#### 3. Configure the App

**Option A: Using local.properties (Recommended - More Secure)**

1. Open `local.properties` in the root directory
2. Add your Dynamic Environment ID:

```properties
DYNAMIC_ENVIRONMENT_ID=your_environment_id_here
```

The app will automatically read this during build and inject it into `BuildConfig`.

**Option B: Direct in build.gradle.kts (Not Recommended)**

Only use this for testing. Open `app/build.gradle.kts` and modify:

```kotlin
buildConfigField("String", "DYNAMIC_ENVIRONMENT_ID", "\"your_id_here\"")
```

⚠️ **Security Note:** Never commit your Environment ID to Git. The `local.properties` file is already in `.gitignore`.

#### 4. Build and Run

**Using Android Studio:**
1. Open the project
2. Wait for Gradle sync to complete
3. Click Run ▶️
4. Select your device/emulator (API 26+)

**Using Command Line:**
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 5. Get Test ETH (Sepolia)

To send transactions, you need Sepolia testnet ETH:

**Option A: Google Cloud Faucet (Recommended)**
1. Visit https://cloud.google.com/application/web3/faucet/ethereum/sepolia
2. Paste your wallet address
3. Receive 0.05 SepoliaETH instantly

**Option B: Alchemy Faucet**
1. Visit https://www.alchemy.com/faucets/ethereum-sepolia
2. Sign in and request tokens

**Verify Balance:**
Check on Etherscan: `https://sepolia.etherscan.io/address/YOUR_ADDRESS`

---

## 📸 Screenshots

### 1. Login Screen
```
┌────────────────────────────────┐
│    Crypto Wallet               │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Email                    │  │
│  │  user@example.com        │  │
│  └──────────────────────────┘  │
│                                 │
│  [    Send OTP Code    ]       │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Enter OTP                │  │
│  │  123456                   │  │
│  └──────────────────────────┘  │
│                                 │
│  [      Verify      ]          │
│                                 │
└────────────────────────────────┘
```

**Features:**
- Email input field
- OTP code verification
- Loading states during authentication

---

### 2. Wallet Screen
```
┌────────────────────────────────┐
│  My Wallet          [Logout]   │
├────────────────────────────────┤
│                                 │
│  ┌──────────────────────────┐  │
│  │   Total Balance          │  │
│  │                          │  │
│  │      0.05 ETH            │  │
│  │   (Large, Bold)          │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  ☁️  Network             │  │
│  │     Sepolia              │  │
│  │     Chain ID: 11155111   │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  📍 Address              │  │
│  │  0xaF...5ee  [Copy] 📋   │  │
│  └──────────────────────────┘  │
│                                 │
│  [   Send Transaction   ]      │
│                                 │
└────────────────────────────────┘
```

**Features:**
- Real-time balance display
- Network information (Sepolia, Chain ID)
- Wallet address with copy functionality
- Pull-to-refresh balance
- Logout button

---

### 3. Send Transaction Screen
```
┌────────────────────────────────┐
│  ← Send Transaction            │
├────────────────────────────────┤
│                                 │
│  ┌──────────────────────────┐  │
│  │  Recipient Address        │  │
│  │  0x742d35Cc6634C0...     │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │  Amount (ETH)             │  │
│  │  0.001                    │  │
│  └──────────────────────────┘  │
│                                 │
│  [   Send Transaction   ]      │
│                                 │
│  ───── Success ─────           │
│                                 │
│  ✅ Transaction Sent!           │
│                                 │
│  Transaction Hash:              │
│  0x1a2b3c4d... [Copy]          │
│                                 │
│  [View on Etherscan]           │
│                                 │
└────────────────────────────────┘
```

**Features:**
- Recipient address input (validates 0x format)
- Amount input (decimal supported)
- Transaction status (Loading, Success, Error)
- Transaction hash display
- Copy hash to clipboard
- Direct link to Etherscan

---

## ⚙️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **DI:** Hilt
- **Async:** Coroutines + Flow
- **Navigation:** Compose Navigation
- **Blockchain SDK:** Dynamic SDK
- **Network:** Sepolia Testnet (Chain ID: 11155111)

---

## 🔐 Assumptions

### 1. Network Configuration
- **Hardcoded to Sepolia testnet** (Chain ID: 11155111)
- No network switching functionality
- Assumes users want to test on Sepolia only

### 2. Wallet Management
- **Single EVM wallet** per user
- App assumes first EVM wallet from Dynamic SDK
- No multi-wallet support

### 3. Transaction Gas Settings
- **Fixed gas limit:** 21,000 (standard ETH transfer)
- **Gas price:** Fetched dynamically from network
- **EIP-1559 fees:** maxFeePerGas = 2x current gas price

### 4. Dynamic SDK Integration
- **DynamicUI() component required** - Must be present in MainActivity for transaction signing
- **Embedded wallets** use WebView for user approval
- **120-second timeout** for transaction signing

### 5. Authentication
- **Email OTP only** - No social login options
- Session managed entirely by Dynamic SDK
- No offline mode

### 6. Error Handling
- Basic error messages displayed to user
- Network errors shown as generic messages
- No retry logic for failed operations

### 7. Transaction Limits
- No minimum/maximum amount validation
- Assumes user has sufficient balance (checked by network)
- No transaction history tracking

### 8. UI/UX
- Loading states show spinner only
- No skeleton screens or shimmer effects
- Success/error states timeout after display

---

## 📝 Known Limitations

1. **No Transaction History** - Only current balance is shown
2. **ETH Only** - No ERC-20 token support
3. **No Offline Mode** - Requires active internet connection
4. **Basic Validation** - Address validation could be more robust
5. **No Custom Gas** - Users cannot set custom gas prices
6. **Single Network** - Only Sepolia testnet supported

---

## 🛠️ Troubleshooting

### Common Issues

**"Transaction hangs/times out"**
- Ensure `DynamicUI()` component is present in MainActivity
- Check that Activity context was passed to `DynamicSDK.initialize()`
- Verify you approved the transaction in the popup

**"No balance showing"**
- Verify you're on Sepolia testnet
- Get test ETH from faucets (see "Get Test ETH" section)
- Check wallet address on Etherscan

**"Transaction failed"**
- Ensure sufficient balance (amount + gas fees)
- Verify recipient address is valid (42 characters, starts with 0x)
- Check network connection

**"OTP not arriving"**
- Check spam folder
- Verify email is correct
- Try different email provider
- Check Dynamic dashboard for auth logs

---

## 📄 License

MIT License - See [LICENSE](LICENSE) file for details

---

## 🙏 Acknowledgments

- **Dynamic Labs** - For the embedded wallet SDK
- **Ethereum Foundation** - For Sepolia testnet
- **Google** - For Jetpack Compose framework

---

**Built with ❤️ using Kotlin and Jetpack Compose**
