package com.example.cryptowallet.ui.screens.send

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.data.model.TransactionState
import com.example.cryptowallet.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendTransactionViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val transactionState: StateFlow<TransactionState> = _transactionState.asStateFlow()
    
    private val _recipientAddress = MutableStateFlow("")
    val recipientAddress: StateFlow<String> = _recipientAddress.asStateFlow()
    
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()
    
    fun updateRecipientAddress(address: String) {
        _recipientAddress.value = address
    }
    
    fun updateAmount(newAmount: String) {
        // Only allow valid decimal numbers
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _amount.value = newAmount
        }
    }
    
    fun sendTransaction() {
        // Validate inputs
        if (_recipientAddress.value.isBlank()) {
            _transactionState.value = TransactionState.Error("Please enter recipient address")
            return
        }
        
        if (!isValidEthereumAddress(_recipientAddress.value)) {
            _transactionState.value = TransactionState.Error("Invalid Ethereum address")
            return
        }
        
        if (_amount.value.isBlank()) {
            _transactionState.value = TransactionState.Error("Please enter amount")
            return
        }
        
        val amountValue = _amount.value.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            _transactionState.value = TransactionState.Error("Invalid amount")
            return
        }
        
        viewModelScope.launch {
            _transactionState.value = TransactionState.Loading
            
            walletRepository.sendTransaction(
                to = _recipientAddress.value,
                amount = _amount.value
            )
                .onSuccess { txHash ->
                    _transactionState.value = TransactionState.Success(txHash)
                }
                .onFailure { error ->
                    _transactionState.value = TransactionState.Error(
                        error.message ?: "Transaction failed"
                    )
                    Log.d("TAG", "sendTransaction: ${_transactionState.value} ")
                }
        }
    }
    
    fun resetTransactionState() {
        _transactionState.value = TransactionState.Idle
    }
    
    fun clearForm() {
        _recipientAddress.value = ""
        _amount.value = ""
        _transactionState.value = TransactionState.Idle
    }
    
    private fun isValidEthereumAddress(address: String): Boolean {
        // Basic Ethereum address validation
        // Must start with 0x and be 42 characters long (0x + 40 hex characters)
        return address.matches(Regex("^0x[a-fA-F0-9]{40}$"))
    }
}
