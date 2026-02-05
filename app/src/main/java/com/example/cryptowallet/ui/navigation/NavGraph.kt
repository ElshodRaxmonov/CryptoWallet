package com.example.cryptowallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cryptowallet.AppStartDestination
import com.example.cryptowallet.MainUiState
import com.example.cryptowallet.MainViewModel
import com.example.cryptowallet.ui.screens.login.LoginScreen
import com.example.cryptowallet.ui.screens.login.LoginViewModel
import com.example.cryptowallet.ui.screens.send.SendTransactionScreen
import com.example.cryptowallet.ui.screens.verify.VerifyScreen
import com.example.cryptowallet.ui.screens.wallet.WalletDetailsScreen

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object VerifyOtp : Screen("verify_otp")
    data object WalletDetails : Screen("wallet_details")
    data object SendTransaction : Screen("send_transaction")
}

/**
 * Main navigation graph
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    when(val state = uiState){
        is MainUiState.Loading -> {

        }
        is MainUiState.Ready -> {
            val startDestination = when(state.destination){
                AppStartDestination.Login -> Screen.Login.route
                AppStartDestination.Home -> Screen.WalletDetails.route
            }
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // Login Screen
                composable(Screen.Login.route) {
                    val viewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.WalletDetails.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onOtpSent = {
                            navController.navigate(Screen.VerifyOtp.route)
                        }
                    )
                }

                // Verify OTP Screen
                composable(Screen.VerifyOtp.route) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Login.route)
                    }
                    val viewModel: LoginViewModel = hiltViewModel(parentEntry)

                    VerifyScreen(
                        viewModel = viewModel,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onLoginSuccess = {
                            navController.navigate(Screen.WalletDetails.route) {
                                // Clear login and verify from back stack
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                // Wallet Details Screen
                composable(Screen.WalletDetails.route) {
                    WalletDetailsScreen(
                        onSendTransaction = {
                            navController.navigate(Screen.SendTransaction.route)
                        },
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                // Clear entire back stack
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // Send Transaction Screen
                composable(Screen.SendTransaction.route) {
                    SendTransactionScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }


}
