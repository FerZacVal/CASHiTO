# Cashito - Personal Savings App

## Overview
Cashito is a modern, clean mobile application designed for personal savings management. The app helps users set financial goals, track progress, and manage their savings with an intuitive and friendly interface.

## Design System

### Colors
- **Primary Green**: #2DBE7A
- **Primary Dark**: #117A52
- **Neutral Dark**: #111827
- **Neutral Mid**: #374151
- **Surface/Card Background**: #FFFFFF
- **Background**: #F6F8FA
- **Muted Text**: #64748B
- **Error**: #EF4444
- **Success**: #10B981

### Typography
- **H1/Amount**: 36sp, Bold
- **H2**: 20sp, Semibold
- **H3**: 16sp, Semibold
- **Body**: 14sp, Regular
- **Caption**: 12sp, Medium

### Spacing
- Base unit: 8dp
- Multipliers: 8, 16, 24, 32dp

### Border Radius
- Cards: 16dp
- Buttons: 12dp
- Small elements: 8dp

## Screens Implemented

### 1. Splash/Onboarding Screen
- Gradient background from #F6F8FA to #FFFFFF
- Centered logo with app name "Cashito"
- Tagline: "Ahorra fácil. Llega a tus metas."
- Primary CTA button: "Comenzar"
- Auto-navigation after 3 seconds

### 2. Login/Registration Screen
- Email and password fields with validation
- "Remember me" checkbox
- Social login options (Google, Apple)
- "Forgot password" link
- Form validation with error messages

### 3. Dashboard Screen
- Personalized greeting with avatar
- Hero card showing total balance and main goal progress
- Donut chart for goal progress visualization
- Quick action buttons (Deposit, Withdraw)
- Horizontal scrolling goals section
- Recent transactions list
- Insights card with monthly savings summary
- Bottom navigation bar

### 4. Goal Detail Screen
- Large donut chart showing progress percentage
- Goal information (saved amount, target amount, target date)
- Action buttons (Deposit, Edit Goal)
- Recurring savings plan toggle
- Transaction history specific to the goal
- Empty state with motivational message

### 5. Goal Form Screen
- Goal name input
- Target amount input with numeric keyboard
- Date picker for target date
- Icon selection from predefined options
- Color selection from color palette
- Recurring savings configuration
- Form validation and save functionality

### 6. Transactions Screen
- Search bar for filtering transactions
- Filter chips (All, Deposits, Withdrawals, Goals)
- Grouped transaction list by date
- Transaction details with icons and categories
- Floating action button for new transactions

### 7. Quick Save Modal
- Preset amount buttons (S/ 5, 10, 20, 50)
- Custom amount input
- Goal selection chips
- Confirmation button
- Modal overlay design

### 8. Profile/Settings Screen
- User profile header with avatar and info
- Account settings (Personal info, Linked accounts)
- Security settings (Password, Biometric authentication)
- Notification preferences
- Support options (FAQ, Contact support)
- Security notice
- Logout button

## Components

### Buttons
- **Primary Button**: Green background, white text, 52dp height
- **Secondary Button**: Outline style with green border
- **Small Button**: Compact version for chips and quick actions

### Cards
- **Hero Card**: Large card with gradient background for main balance
- **Goal Card**: Compact card for goal display with progress bar
- **Transaction Card**: List item for transaction display

### Inputs
- **Text Field**: Standard input with label, placeholder, and validation
- **Search Field**: Input with search icon
- **Date Picker**: Card-style date selection

### Navigation
- **Bottom Navigation**: 4-tab navigation (Dashboard, Transactions, Goals, Profile)
- **Top App Bar**: Consistent header with back navigation and actions

## Features

### Core Functionality
- ✅ User authentication and registration
- ✅ Dashboard with balance overview
- ✅ Goal creation and management
- ✅ Transaction history and filtering
- ✅ Quick save functionality
- ✅ Profile and settings management

### UI/UX Features
- ✅ Modern, clean design with green/blue color scheme
- ✅ Responsive layout for 390×844px artboard
- ✅ Consistent spacing and typography
- ✅ Intuitive navigation flow
- ✅ Form validation and error handling
- ✅ Empty states with motivational messaging

### Accessibility
- ✅ High contrast text (4.5:1 ratio)
- ✅ Touch targets ≥44×44dp
- ✅ Clear visual hierarchy
- ✅ Descriptive labels and placeholders

## Technical Implementation

### Architecture
- MVVM pattern with Compose UI
- Navigation Component for screen routing
- Material 3 design system
- Custom theme implementation

### Dependencies
- Jetpack Compose
- Material 3
- Navigation Compose
- Material Icons Extended

### File Structure
```
app/src/main/java/com/cashito/
├── ui/
│   ├── components/
│   │   ├── buttons/
│   │   ├── cards/
│   │   ├── inputs/
│   │   └── navigation/
│   ├── screens/
│   │   ├── splash/
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── goal_detail/
│   │   ├── goal_form/
│   │   ├── transactions/
│   │   ├── quick_save/
│   │   └── profile/
│   └── theme/
└── Navigation.kt
```

## Future Enhancements
- [ ] Dark mode support
- [ ] Data persistence with Room database
- [ ] Cloud sync functionality
- [ ] Push notifications
- [ ] Advanced analytics and insights
- [ ] Goal sharing features
- [ ] Multi-currency support
- [ ] Biometric authentication integration

## Getting Started
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

The app will start with the splash screen and guide users through the onboarding flow to the main dashboard.
