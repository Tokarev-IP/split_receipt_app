package com.iliatokarev.receipt_splitter.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.iliatokarev.receipt_splitter.main.presentation.MainActivityCompose
import com.iliatokarev.receipt_splitter.ui.theme.Receipt_SplitterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Receipt_SplitterTheme {
                MainActivityCompose()
            }
        }
    }
}