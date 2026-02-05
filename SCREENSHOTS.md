# Screenshots & UI Reference

This document describes the visual appearance of each screen in the Crypto Wallet app.

## Screen Previews

### 1. Login Screen

**Layout:**
```
┌────────────────────────────────────┐
│         [Status Bar]                │
├────────────────────────────────────┤
│                                     │
│                                     │
│              🔐                     │
│       (Large Lock Icon)             │
│                                     │
│        Crypto Wallet                │
│     (Large, Blue Title)             │
│                                     │
│   Secure Web3 Authentication        │
│        (Subtitle)                   │
│                                     │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ 📧  Email Address            │  │
│  │  user@example.com            │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │      Send OTP                │  │
│  │   (Primary Blue Button)      │  │
│  └──────────────────────────────┘  │
│                                     │
│  We'll send a one-time password     │
│  to your email for secure           │
│  authentication                     │
│                                     │
│                                     │
└────────────────────────────────────┘
```

**Colors:**
- Background: Light gray (#FAFAFA)
- Primary button: Blue (#1E88E5)
- Text: Dark gray (#212121)
- Icons: Blue accent

**Features:**
- Large emoji icon (🔐) for visual appeal
- Clear hierarchy with title and subtitle
- Email input with icon
- Prominent CTA button
- Helpful description text

---

### 2. OTP Verification Dialog

**Layout:**
```
┌────────────────────────────────────┐
│                                     │
│     Enter OTP Code                  │
│   (Dialog Title - Bold)             │
│                                     │
│  Please enter the 6-digit code      │
│  sent to your email                 │
│  (Body text)                        │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  OTP Code                     │  │
│  │  [______]                     │  │
│  │  (6-digit input field)        │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────┐      ┌────────────┐  │
│  │ Cancel   │      │   Verify   │  │
│  │ (Text)   │      │  (Button)  │  │
│  └──────────┘      └────────────┘  │
│                                     │
└────────────────────────────────────┘
```

**States:**
- Default: Empty input field
- Loading: Spinner in Verify button
- Error: Red error message below input
- Success: Checkmark, navigate to wallet

---

### 3. Wallet Details Screen

**Layout:**
```
┌────────────────────────────────────┐
│  My Wallet            [Logout 🚪]  │
│  (Top Bar - Blue)                   │
├────────────────────────────────────┤
│                                     │
│  ┌──────────────────────────────┐  │
│  │     Total Balance            │  │
│  │                              │  │
│  │        0.05 ETH              │  │
│  │    (Large, Bold)             │  │
│  │                              │  │
│  └──────────────────────────────┘  │
│  (Balance Card - Light Blue BG)     │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  ☁️  Network                 │  │
│  │     Sepolia                  │  │
│  │     Chain ID: 11155111    ⚡ │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Wallet Address         [📋] │  │
│  │                              │  │
│  │  0x742d35Cc6634C053292...   │  │
│  │  (Monospace font)            │  │
│  │                              │  │
│  │  ✓ Address copied            │  │
│  │  (Shows after copy)          │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  📤  Send Transaction        │  │
│  │  (Primary Action Button)     │  │
│  └──────────────────────────────┘  │
│                                     │
└────────────────────────────────────┘
```

**Key Features:**
- Pull-to-refresh enabled
- Blue top bar with logout
- Prominent balance display
- Network indicator with chain ID
- Copyable wallet address
- Large send button

**Card Hierarchy:**
1. Balance (most important)
2. Network info
3. Wallet address
4. Action button

---

### 4. Send Transaction Screen

**Layout:**
```
┌────────────────────────────────────┐
│  ← Send Transaction                │
│  (Top Bar - Blue)                   │
├────────────────────────────────────┤
│                                     │
│  ┌──────────────────────────────┐  │
│  │  ℹ️  Sending on Sepolia      │  │
│  │      Testnet                 │  │
│  └──────────────────────────────┘  │
│  (Info Card - Light Blue)           │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  👤  Recipient Address       │  │
│  │  0x...                       │  │
│  │  Enter the Ethereum address  │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Ξ   Amount (ETH)       ETH  │  │
│  │  0.001                       │  │
│  │  Enter amount to send        │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Transaction Summary         │  │
│  │  ────────────────────────    │  │
│  │  Network    Sepolia Testnet  │  │
│  │  Gas Limit  21,000           │  │
│  │  Amount     0.001 ETH        │  │
│  │  ────────────────────────    │  │
│  │  Total      0.001 ETH        │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  📤  Send Transaction        │  │
│  │  (Primary Action Button)     │  │
│  └──────────────────────────────┘  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  ⚠️  Important               │  │
│  │  • Double-check recipient    │  │
│  │  • Ensure enough ETH         │  │
│  │  • Cannot be reversed        │  │
│  └──────────────────────────────┘  │
│  (Warning Card - Amber BG)          │
│                                     │
└────────────────────────────────────┘
```

**Form Elements:**
- Text inputs with icons
- Helper text below fields
- Live transaction summary
- Warning section at bottom
- Disabled state when loading

---

### 5. Transaction Success Dialog

**Layout:**
```
┌────────────────────────────────────┐
│                                     │
│            ✅                       │
│      (Large Success Icon)           │
│                                     │
│     Transaction Sent!               │
│       (Title - Bold)                │
│                                     │
│  Your transaction has been          │
│  successfully submitted to          │
│  the network.                       │
│  (Body text)                        │
│                                     │
│  Transaction Hash:                  │
│  (Label)                            │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  0xaaaa...aaaa               │  │
│  │  (Full hash, monospace)      │  │
│  └──────────────────────────────┘  │
│                                     │
│  [📋 Copy Hash]                     │
│  (Text button)                      │
│                                     │
│  View on Etherscan:                 │
│  sepolia.etherscan.io/tx/0x...      │
│  (Small gray text)                  │
│                                     │
│  ┌──────────────────────────────┐  │
│  │         Done                 │  │
│  │    (Primary Button)          │  │
│  └──────────────────────────────┘  │
│                                     │
└────────────────────────────────────┘
```

**Features:**
- Large success icon
- Full transaction hash
- Copy functionality
- Etherscan link
- Clear dismiss button

---

## Error States

### Login Error
```
┌────────────────────────────────────┐
│  ⚠️  Please enter a valid email    │
│  (Error card - Red background)     │
└────────────────────────────────────┘
```

### Transaction Error
```
┌────────────────────────────────────┐
│  ❌  Transaction Failed             │
│  Insufficient balance for           │
│  transaction and gas fees           │
│  (Error card - Red background)      │
└────────────────────────────────────┘
```

### Loading State
```
┌────────────────────────────────────┐
│           ⏳                        │
│      (Loading Spinner)              │
│     Loading wallet...               │
└────────────────────────────────────┘
```

---

## Color Palette

### Primary Colors
- **Primary Blue**: #1E88E5
- **Primary Dark**: #1565C0
- **Secondary Green**: #4CAF50
- **Error Red**: #E53935
- **Success Green**: #43A047

### Background Colors
- **Background Light**: #FAFAFA
- **Surface**: #FFFFFF
- **Surface Variant**: #F5F5F5

### Text Colors
- **Text Primary**: #212121
- **Text Secondary**: #757575
- **On Primary**: #FFFFFF

### Accent Colors
- **Info Blue**: #2196F3
- **Warning Amber**: #FFA726

---

## Typography Scale

### Headlines
- **Display Large**: 57sp, Bold
- **Headline Large**: 32sp, Bold
- **Headline Medium**: 28sp, Bold
- **Title Large**: 22sp, SemiBold

### Body
- **Body Large**: 16sp, Regular
- **Body Medium**: 14sp, Regular
- **Body Small**: 12sp, Regular

### Labels
- **Label Large**: 14sp, Medium
- **Label Medium**: 12sp, Medium
- **Label Small**: 11sp, Medium

---

## Icons & Emojis Used

### Emojis
- 🔐 - Lock (Login)
- 📧 - Email
- ☁️ - Cloud/Network
- 👤 - Person/Account
- Ξ - Ethereum symbol
- 📤 - Send
- ⚡ - Quick/Fast
- ✅ - Success
- ❌ - Error
- ⚠️ - Warning
- ℹ️ - Information
- 📋 - Copy
- ✓ - Check/Copied
- 🚪 - Logout

### Material Icons
- ArrowBack
- ContentCopy
- Send
- Error
- Info
- Warning
- Check
- CheckCircle
- Cloud
- AccountBox
- Clear
- Logout

---

## Responsive Behavior

### Small Screens (< 360dp)
- Reduce padding
- Smaller text sizes
- Stacked buttons

### Medium Screens (360-600dp)
- Standard layout (as shown)
- Optimal spacing
- Side-by-side buttons

### Large Screens (> 600dp)
- Max width constraints
- Centered content
- More generous spacing

---

## Accessibility

### Text Sizes
- All text scalable with system settings
- Minimum touch target: 48dp
- High contrast mode support

### Screen Reader Support
- All interactive elements labeled
- State changes announced
- Error messages read aloud

### Visual Indicators
- Not relying solely on color
- Icons supplement text
- Clear focus indicators

---

## Animation & Transitions

### Screen Transitions
- Slide from right for forward
- Slide to right for back
- 300ms duration
- Decelerate interpolator

### Dialog Animations
- Fade in with scale
- 200ms duration
- Slide up for bottom sheets

### Loading States
- Circular progress indicator
- Fade transition between states
- Skeleton screens for long loads

---

## Dark Mode (Future Enhancement)

The app currently uses light mode. For dark mode:
- Invert background colors
- Adjust card surfaces
- Maintain color contrast ratios
- Keep brand colors visible

---

## UI/UX Best Practices Applied

1. **Clear Visual Hierarchy**
   - Most important info first
   - Size indicates importance
   - Consistent spacing

2. **Feedback**
   - Loading indicators
   - Success/error messages
   - Disabled states clear

3. **Consistency**
   - Same patterns throughout
   - Predictable navigation
   - Familiar icons

4. **Error Prevention**
   - Input validation
   - Confirmation dialogs
   - Warning messages

5. **Recovery**
   - Clear error messages
   - Retry options
   - Cancel actions available

---

## Screenshot Capture Guide

To capture screenshots:

1. **Setup**
   - Use Pixel 7 emulator
   - Android 14 (API 34)
   - 1080x2400 resolution

2. **Prepare Data**
   - Complete login
   - Get test ETH
   - Send test transaction

3. **Capture Screens**
   - Login (empty state)
   - Login (with email)
   - OTP dialog
   - Wallet (with balance)
   - Send (empty form)
   - Send (filled form)
   - Success dialog

4. **Tools**
   - Android Studio Device File Explorer
   - Or ADB: `adb exec-out screencap -p > screen.png`

---

This visual reference ensures consistent, professional UI across all screens of the Crypto Wallet app.
