package com.example.cryptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.core.ClientProps
import com.dynamic.sdk.android.core.LoggerLevel
import com.example.cryptowallet.data.repository.WalletRepository
import com.example.cryptowallet.ui.navigation.NavGraph
import com.example.cryptowallet.ui.theme.CryptoWalletAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var walletRepository: WalletRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val props = ClientProps(
            environmentId = "f78800c2-0e93-4a69-8ed2-eb059ee82ae1",
            appName = "Crypto Wallet Test",
            redirectUrl = "dynamictest://",
            appOrigin = "https://test.app",
            logLevel = LoggerLevel.DEBUG
        )

        // Initialize SDK and provide it to the repository
        val sdk = DynamicSDK.initialize(props, applicationContext, this)
        walletRepository.initialize(sdk)
        
        setContent {
            CryptoWalletAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
