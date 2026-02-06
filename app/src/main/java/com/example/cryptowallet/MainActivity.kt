package com.example.cryptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.core.ClientProps
import com.dynamic.sdk.android.core.LoggerLevel
import com.dynamic.sdk.android.UI.DynamicUI
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
            environmentId = BuildConfig.DYNAMIC_ENVIRONMENT_ID,
            appName = "Crypto Wallet Test",
            redirectUrl = "dynamictest://",
            appOrigin = "https://test.app",
            logLevel = LoggerLevel.DEBUG
        )

        val sdk = DynamicSDK.initialize(props, applicationContext, this)
        walletRepository.initialize(sdk)
        
        setContent {
            CryptoWalletAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavGraph()

                        DynamicUI()
                    }
                }
            }
        }
    }
}
