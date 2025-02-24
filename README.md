# SpendLess - Secure Finance Tracking Android App

SpendLess is a modern Android finance tracking application with a strong focus on security and data protection. It enables users to track their income and expenses while ensuring their financial data remains secure through local encryption and session management.
The app will create a test user with the username "testUser" and a pin of 12345 on first launch of a debug build. This can be used for demo purposes.

## Features

- 🔐 Secure local authentication with PIN and optional biometrics
- 💰 Comprehensive transaction tracking and categorization
- 📊 Insightful financial statistics and reporting
- 🔄 Support for recurring transactions
- 📱 Material Design UI with Jetpack Compose
- 🔒 Encrypted local data storage
- 📈 Detailed transaction history with filtering
- 📤 Export capabilities for transaction data

## Technical Highlights

The application demonstrates modern Android development practices and technologies:

### Security Features
- Local session management with configurable timeouts
- Encrypted PIN storage and transaction data
- Biometric authentication support
- Customizable security preferences

### Architecture & Design
- Clean Architecture principles
- Material Design implementation
- Single Activity architecture
- Local-first data approach

### Data Management
- Encrypted local storage for sensitive data
- CSV and PDF export capabilities
- Transaction categorization system
- Support for various currency formats

## Implementation Details

### Authentication System
- 5-digit PIN authentication
- Configurable session duration
- Biometric authentication integration
- Account lockout mechanism with customizable duration
- Local-only account management

### Transaction Management
- Support for both income and expenses
- Customizable recurring transaction schedules
- Transaction categorization for expenses
- Optional note attachment for transactions
- Comprehensive transaction history

### User Preferences
- Customizable currency display formats
- Flexible decimal and thousands separators
- Multiple currency symbol options
- Persistent user preferences

## Building The Project

1. Clone the repository
```bash
git clone https://github.com/BLTuckerDev/SpendLess
```

2. Open the project in Android Studio

3. Build and run the project

## Requirements
- Minimum SDK: 27
- Target SDK: 35
- Kotlin: 2.0.21

## License

This project is licensed under the [Your chosen license] - see the [LICENSE](LICENSE) file for details.