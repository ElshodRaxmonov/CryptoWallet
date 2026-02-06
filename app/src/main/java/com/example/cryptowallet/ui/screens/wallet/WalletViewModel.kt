package com.example.cryptowallet.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.data.model.WalletState
import com.example.cryptowallet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Loading)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadWalletInfo()
    }

    fun loadWalletInfo() {
        viewModelScope.launch {
            _walletState.value = WalletState.Loading

            try {

                val addressResult = walletRepository.getWalletAddress()

                if (addressResult.isFailure) {
                    _walletState.value = WalletState.Error(
                        addressResult.exceptionOrNull()?.message ?: "Failed to load wallet"
                    )
                    return@launch
                }

                val address = addressResult.getOrNull() ?: ""

                // Get balance
                val balanceResult = walletRepository.getBalance(address)

                if (balanceResult.isFailure) {
                    _walletState.value = WalletState.Error(
                        balanceResult.exceptionOrNull()?.message ?: "Failed to load balance"
                    )
                    return@launch
                }

                val balance = balanceResult.getOrNull() ?: "0.0"

                _walletState.value = WalletState.Success(
                    address = address,
                    balance = balance,
                    network = "Sepolia",
                    chainId = 11155111L
                )
            } catch (e: Exception) {
                _walletState.value = WalletState.Error(
                    e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            walletRepository.logout()
                .onSuccess {
                    onLogoutComplete()
                }
                .onFailure { error ->
                    onLogoutComplete()
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadWalletInfo()
            _isRefreshing.value = false
        }
    }
}
