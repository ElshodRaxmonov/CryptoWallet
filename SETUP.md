# 📋 Setup Instructions

This guide will walk you through setting up and running the Crypto Wallet Android app.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Dynamic SDK Setup](#dynamic-sdk-setup)
3. [Project Configuration](#project-configuration)
4. [Building the App](#building-the-app)
5. [Testing the App](#testing-the-app)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or newer
  - Download from: https://developer.android.com/studio
- **Java Development Kit (JDK)**: Version 17
- **Android SDK**: API Level 34 (Android 14)
- **Git**: For version control

### Verify Installation
```bash
# Check Java version
java -version
# Should show: java version "17.x.x"

# Check Android SDK
android --version
```

---

## Dynamic SDK Setup

### 1. Create Dynamic Account

1. Visit https://app.dynamic.xyz
2. Click "Sign Up" (it's free)
3. Complete registration
4. Verify your email

### 2. Create New Project

1. Once logged in, click "Create New Project"
2. Choose a project name (e.g., "Crypto Wallet Test")
3. Select "Blockchain" or "Web3" as the project type
4. Click "Create"

### 3. Get Environment ID

1. Go to your project dashboard
2. Navigate to "SDK" or "Settings"
3. Find and copy your **Environment ID**
   - It looks like: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`
4. Save this ID - you'll need it in the next step

### 4. Configure Email Authentication

1. In your Dynamic dashboard, go to "Authentication"
2. Enable "Email OTP" authentication
3. Configure email settings if needed
4. Save changes

---

## Project Configuration

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd CryptoWalletApp
```

### 2. Configure Dynamic Environment ID

**Option A: Using local.properties (Recommended)**

1. Open `local.properties` file in the root directory
2. Add your Environment ID:
```properties
DYNAMIC_ENVIRONMENT_ID=your_environment_id_here
```

**Option B: Directly in MainActivity**

1. Open `app/src/main/java/com/example/cryptowallet/MainActivity.kt`
2. Find the commented Dynamic SDK initialization code
3. Replace `YOUR_ENVIRONMENT_ID` with your actual ID:
```kotlin
val props = ClientProps(
    environmentId = "your_environment_id_here", // ← Replace this
    appName = "Crypto Wallet Test",
    redirectUrl = "dynamictest://",
    appOrigin = "https://test.app",
    logLevel = LoggerLevel.DEBUG
)
```

### 3. Add Dynamic SDK Dependency

The project is configured with a placeholder for the Dynamic SDK. You'll need to add the actual dependency:

1. Open `app/build.gradle.kts`
2. Find the Dynamic SDK dependency line:
```kotlin
implementation("xyz.dynamic:sdk:1.0.0") // Replace with actual version
```
3. Replace with the actual Dynamic SDK version from their documentation:
   - Visit: https://github.com/dynamic-labs/android-sdk-and-sample-app
   - Check the latest release version
   - Update the dependency accordingly

**Example** (check documentation for exact version):
```kotlin
implementation("xyz.dynamic:android-sdk:1.2.3")
```

### 4. Sync Gradle

1. In Android Studio, click **File → Sync Project with Gradle Files**
2. Wait for sync to complete
3. Resolve any dependency issues if they appear

---

## Building the App

### Using Android Studio

1. **Open Project**
   - Launch Android Studio
   - Click "Open" and select the `CryptoWalletApp` folder

2. **Wait for Indexing**
   - Android Studio will index the project
   - This may take a few minutes on first open

3. **Connect Device or Start Emulator**
   
   **Physical Device:**
   - Enable Developer Options on your Android device
   - Enable USB Debugging
   - Connect via USB
   - Select device in Android Studio

   **Emulator:**
   - Open AVD Manager (Tools → Device Manager)
   - Create a new Virtual Device
   - Choose Pixel 7 or similar (API 34)
   - Start the emulator

4. **Run the App**
   - Click the green "Run" button (▶️) or press `Shift + F10`
   - Select your device/emulator
   - App should install and launch

### Using Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew assembleDebug installDebug

# Run tests
./gradlew test
```

---

## Testing the App

### 1. Login Flow

1. **Launch the app**
2. **Enter your email address**
   - Use any valid email you have access to
3. **Click "Send OTP"**
   - Check your email for the OTP code
   - Code should arrive within 1-2 minutes
4. **Enter the 6-digit OTP**
5. **Click "Verify"**
   - You should be authenticated and see the wallet screen

### 2. Get Test ETH (Sepolia)

Before you can send transactions, you need Sepolia testnet ETH:

**Option A: Google Cloud Faucet (Easiest)**
1. Go to: https://cloud.google.com/application/web3/faucet/ethereum/sepolia
2. Sign in with your Google account
3. Copy your wallet address from the app
4. Paste it in the faucet
5. Click "Send me ETH"
6. Receive 0.05 SepoliaETH instantly

**Option B: Alchemy Faucet**
1. Go to: https://www.alchemy.com/faucets/ethereum-sepolia
2. Create an Alchemy account (free)
3. Paste your wallet address
4. Complete any CAPTCHA if required
5. Get 0.5 SepoliaETH

**Verify your balance:**
- Visit: `https://sepolia.etherscan.io/address/YOUR_ADDRESS`
- You should see your SepoliaETH balance

### 3. Wallet Details Screen

1. **View your wallet address**
   - Full Ethereum address displayed
2. **Check network**
   - Should show "Sepolia" (Chain ID: 11155111)
3. **View balance**
   - Should show your SepoliaETH balance
4. **Copy address**
   - Click the copy button
   - Verify clipboard contains your address

### 4. Send Transaction

1. **Click "Send Transaction"**
2. **Enter recipient address**
   - Use another test wallet address
   - Or send to: `0x742d35Cc6634C0532925a3b844Bc9e7595f0bEb` (example)
3. **Enter amount**
   - Start with a small amount like 0.001 ETH
4. **Review transaction details**
5. **Click "Send Transaction"**
6. **Wait for confirmation**
   - Transaction should complete in 15-30 seconds
7. **Copy transaction hash**
8. **Verify on Etherscan**
   - Click the Etherscan link or visit:
   - `https://sepolia.etherscan.io/tx/YOUR_TX_HASH`

### 5. Test Error Handling

**Invalid Email:**
- Enter invalid email format
- Should show error message

**Invalid Address:**
- Enter malformed Ethereum address
- Should show error message

**Insufficient Balance:**
- Try to send more ETH than you have
- Should show error

---

## Troubleshooting

### Build Errors

**"SDK not found"**
```bash
# Solution: Update local.properties with correct SDK path
sdk.dir=/path/to/your/Android/sdk
```

**"Dynamic SDK not resolved"**
- Check that you've added the correct Dynamic SDK dependency
- Verify Maven repository is configured in `settings.gradle.kts`
- Try: File → Invalidate Caches → Restart

**"Gradle sync failed"**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### Runtime Errors

**"App crashes on launch"**
- Check that Dynamic SDK is properly initialized in MainActivity
- Verify Environment ID is correct
- Check Logcat for error messages

**"OTP not arriving"**
- Check spam folder
- Verify email is correct
- Try different email provider
- Check Dynamic dashboard for authentication logs

**"Balance not showing"**
- Verify you're connected to Sepolia network
- Check you have test ETH in your wallet
- Try pull-to-refresh
- Verify wallet address on Etherscan

**"Transaction fails"**
- Ensure you have sufficient balance
- Check recipient address is valid (42 characters, starts with 0x)
- Verify you have enough ETH for gas fees
- Try with smaller amount

### Network Issues

**"No internet connection"**
- Verify device/emulator has internet access
- Check firewall settings
- Try different network

**"Cannot connect to Dynamic"**
- Verify Environment ID is correct
- Check Dynamic service status
- Try restarting app

### Emulator Issues

**"Emulator is slow"**
- Allocate more RAM to emulator (4GB+)
- Enable hardware acceleration
- Use a lighter API level (API 30-33)

**"Emulator won't start"**
- Restart Android Studio
- Wipe emulator data
- Create new virtual device

---

## Additional Resources

### Documentation
- [Dynamic SDK Docs](https://github.com/dynamic-labs/android-sdk-and-sample-app)
- [Android Developer Guide](https://developer.android.com/docs)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Sepolia Testnet](https://sepolia.dev/)

### Tools
- [Etherscan Sepolia](https://sepolia.etherscan.io/)
- [MetaMask](https://metamask.io/) - For additional wallet management
- [Remix IDE](https://remix.ethereum.org/) - For smart contract testing

### Community
- [Dynamic Discord](https://discord.gg/dynamic)
- [Ethereum Stack Exchange](https://ethereum.stackexchange.com/)
- [Android Developers Reddit](https://reddit.com/r/androiddev)

---

## Next Steps

Once you have the app running:

1. ✅ Test all three screens
2. ✅ Verify authentication works
3. ✅ Send a test transaction
4. ✅ Review the code architecture
5. ✅ Customize the UI if desired
6. ✅ Add additional features (transaction history, etc.)

---

## Support

If you encounter issues not covered here:

1. Check the [GitHub Issues](link-to-issues)
2. Review Dynamic SDK documentation
3. Check Android Studio Logcat for detailed error messages
4. Reach out via the project's communication channels

---

**Happy coding! 🚀**
