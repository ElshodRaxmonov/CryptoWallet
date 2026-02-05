package com.example.cryptowallet.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.data.model.AuthState
import com.example.cryptowallet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }
    
    fun sendOtp() {
        if (_email.value.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        
        if (!isValidEmail(_email.value)) {
            _authState.value = AuthState.Error("Please enter a valid email")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            walletRepository.sendOtp(_email.value)
                .onSuccess {
                    _authState.value = AuthState.OtpSent
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Failed to send OTP"
                    )
                }
        }
    }
    
    fun verifyOtp(code: String) {
        if (code.isBlank()) {
            _authState.value = AuthState.Error("Please enter the OTP code")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            walletRepository.verifyOtp(code)
                .onSuccess {
                    _authState.value = AuthState.Authenticated
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Invalid OTP code"
                    )
                }
        }
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
