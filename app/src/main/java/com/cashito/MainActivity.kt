package com.cashito

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.cashito.core.BiometricAuthenticator
import com.cashito.ui.theme.CASHiTOTheme

class MainActivity : FragmentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CASHiTOTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        BiometricAuthenticator.registerActivity(this)
    }

    override fun onPause() {
        super.onPause()
        BiometricAuthenticator.unregisterActivity()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CASHiTOTheme {
        AppNavHost()
    }
}
