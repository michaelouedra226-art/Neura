package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.components.NeuraFloatingNav
import com.example.core.components.NeuraTab
import com.example.data.local.NeuraDatabase
import com.example.data.local.SettingsManager
import com.example.data.repository.AiEngineService
import com.example.data.repository.NeuraRepository
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreationsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeuraBackground
import com.example.ui.viewmodel.NeuraViewModel
import com.example.ui.viewmodel.NeuraViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: NeuraViewModel by viewModels {
        val database = NeuraDatabase.getDatabase(applicationContext)
        val settingsManager = SettingsManager(applicationContext)
        val aiEngineService = AiEngineService(settingsManager)
        val repository = NeuraRepository(
            conversationDao = database.conversationDao(),
            messageDao = database.messageDao(),
            memoryDao = database.memoryDao(),
            creationDao = database.creationDao(),
            settingsManager = settingsManager,
            aiEngineService = aiEngineService
        )
        NeuraViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NeuraApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun NeuraApp(viewModel: NeuraViewModel) {
    val settingsManager = viewModel.settingsManager
    var isSplashFinished by remember { mutableStateOf(false) }
    var isOnboardingDone by remember { mutableStateOf(settingsManager.isOnboardingDone) }

    val currentTab by viewModel.currentTab.collectAsState()
    val isImeVisible = WindowInsets.isImeVisible

    // BackHandler: return to Chat if on other tabs
    if (isSplashFinished && isOnboardingDone && currentTab != NeuraTab.CHAT) {
        BackHandler {
            viewModel.setTab(NeuraTab.CHAT)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NeuraBackground)
    ) {
        when {
            !isSplashFinished -> {
                SplashScreen(onSplashFinished = { isSplashFinished = true })
            }
            !isOnboardingDone -> {
                OnboardingScreen(
                    onFinishOnboarding = {
                        settingsManager.isOnboardingDone = true
                        isOnboardingDone = true
                    },
                    onSaveFocus = { focus ->
                        settingsManager.userFocus = focus
                    }
                )
            }
            else -> {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NeuraBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        // Automatically hide bottom navigation bar when typing to avoid any layout glitch
                        if (!isImeVisible) {
                            NeuraFloatingNav(
                                selectedTab = currentTab,
                                onTabSelected = { tab -> viewModel.setTab(tab) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (!isImeVisible) innerPadding.calculateBottomPadding() else 0.dp)
                    ) {
                        Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
                            when (tab) {
                                NeuraTab.CHAT -> ChatScreen(viewModel = viewModel)
                                NeuraTab.HISTORY -> HistoryScreen(viewModel = viewModel)
                                NeuraTab.CREATIONS -> CreationsScreen(viewModel = viewModel)
                                NeuraTab.MEMORY -> MemoryScreen(viewModel = viewModel)
                                NeuraTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
