package com.darling.spendwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ripple
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.LocalIndication
import com.darling.spendwise.data.local.database.AppDatabase
import com.darling.spendwise.data.repository.TransactionRepository
import com.darling.spendwise.ui.navigation.MainNavigation
import com.darling.spendwise.ui.theme.SpendWiseTheme
import com.darling.spendwise.viewModel.TransactionViewModel
import com.darling.spendwise.viewModel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(
            TransactionRepository(
                AppDatabase.getDatabase(this).transactionDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SpendWiseTheme {
                // Override LocalIndication để dùng Material3 ripple thay vì Material1
                CompositionLocalProvider(
                    LocalIndication provides ripple()
                ) {
                    MainNavigation(viewModel)
                }
            }
        }
    }
}