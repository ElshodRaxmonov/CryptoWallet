package com.example.cryptowallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class AppStartDestination {
    object Login : AppStartDestination()
    object Home : AppStartDestination()
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Ready(val destination: AppStartDestination) : MainUiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeStartDestination()
    }

    private fun observeStartDestination() {
        viewModelScope.launch {
            walletRepository.observeAuthState().collect { isAuthenticated ->
                if (
                    isAuthenticated
                ) {
                    _uiState.value = MainUiState.Ready(
                        destination = AppStartDestination.Home
                    )

                } else {
                    _uiState.value = MainUiState.Ready(
                        destination = AppStartDestination.Login
                    )
                }
            }
        }
    }
}


