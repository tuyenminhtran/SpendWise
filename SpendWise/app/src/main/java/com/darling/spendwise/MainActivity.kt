package com.darling.spendwise

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darling.spendwise.data.local.database.AppDatabase
import com.darling.spendwise.data.repository.TransactionRepository
import com.darling.spendwise.ui.navigation.MainNavigation
import com.darling.spendwise.ui.theme.SpendWiseTheme
import com.darling.spendwise.utils.UserPreferences
import com.darling.spendwise.viewModel.AuthViewModel
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
            val context = LocalContext.current
            val userPrefs = remember { UserPreferences(context) }
            var isDarkMode by remember { mutableStateOf<Boolean>(userPrefs.isDarkMode) }

            // AuthViewModel — dùng viewModel() để tự quản lý lifecycle
            val application = LocalContext.current.applicationContext as Application
            val authViewModel: AuthViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory(application)
            )

            val view = LocalView.current
            LaunchedEffect(isDarkMode) {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !isDarkMode
                    isAppearanceLightNavigationBars = !isDarkMode
                }
            }

            SpendWiseTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(LocalIndication provides ripple()) {
                    MainNavigation(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = {
                            isDarkMode = it
                            userPrefs.isDarkMode = it
                        }
                    )
                }
            }
        }
    }
}