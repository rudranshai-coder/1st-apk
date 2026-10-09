package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AiQuizScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BcaRoadmapScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.ExamPrepScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyToolsScreen
import com.example.ui.theme.CreamBase
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.MindNestTheme
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.MindNestViewModel
import com.example.viewmodel.Screen

private data class NavDestination(
    val route: String,
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindNestTheme {
                val viewModel: MindNestViewModel = viewModel()
                val navController = rememberNavController()

                // Sync ViewModel's navigateTo with NavController
                val currentScreen by viewModel.currentScreen.collectAsState()

                LaunchedEffect(currentScreen) {
                    val targetRoute = when (currentScreen) {
                        Screen.AUTH -> "auth"
                        Screen.DASHBOARD -> "dashboard"
                        Screen.CHAT -> "chat"
                        Screen.PROFILE -> "profile"
                        Screen.DOCUMENTS -> "documents"
                        Screen.STUDY_TOOLS -> "tools"
                        Screen.EXAM_PREP -> "exams"
                        Screen.HISTORY -> "history"
                        Screen.SAVED -> "saved"
                        Screen.SETTINGS -> "settings"
                        Screen.ROADMAP -> "roadmap"
                        Screen.QUIZ -> "quiz"
                    }
                    if (navController.currentDestination?.route != targetRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Clean 3-tab primary navigation for simplicity
                val primaryNavItems = listOf(
                    NavDestination(
                        route = "dashboard",
                        screen = Screen.DASHBOARD,
                        label = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        testTag = "nav_home"
                    ),
                    NavDestination(
                        route = "chat",
                        screen = Screen.CHAT,
                        label = "Nesty AI",
                        selectedIcon = Icons.Default.AutoAwesome,
                        unselectedIcon = Icons.Default.AutoAwesome,
                        testTag = "nav_chat"
                    ),
                    NavDestination(
                        route = "profile",
                        screen = Screen.PROFILE,
                        label = "Profile",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        testTag = "nav_profile"
                    )
                )

                val showBottomBar = currentRoute in listOf("dashboard", "chat", "profile", "documents", "tools", "exams", "roadmap", "quiz")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CreamBase,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = CreamSurface,
                                tonalElevation = 3.dp,
                                modifier = Modifier
                                    .shadow(6.dp)
                                    .border(0.8.dp, Color(0xFFE5E0D6))
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                primaryNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    val isChat = item.route == "chat"

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.navigateTo(item.screen)
                                        },
                                        icon = {
                                            if (isChat) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) SagePrimary else SageLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = item.selectedIcon,
                                                        contentDescription = item.label,
                                                        tint = if (isSelected) Color.White else SageDark,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.label,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = item.label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = SageDark,
                                            unselectedIconColor = Color(0xFF88958E),
                                            selectedTextColor = SageDark,
                                            unselectedTextColor = Color(0xFF88958E),
                                            indicatorColor = if (isChat) Color.Transparent else SageLight
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(viewModel = viewModel)
                        }
                        composable("chat") {
                            val chatViewModel: ChatViewModel = viewModel()
                            ChatScreen(
                                viewModel = chatViewModel,
                                onNavigateToExams = { viewModel.navigateTo(Screen.EXAM_PREP) },
                                onNavigateToProfile = { viewModel.navigateTo(Screen.PROFILE) }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(viewModel = viewModel)
                        }
                        composable("documents") {
                            DocumentsScreen(viewModel = viewModel)
                        }
                        composable("tools") {
                            StudyToolsScreen(viewModel = viewModel)
                        }
                        composable("exams") {
                            ExamPrepScreen(viewModel = viewModel)
                        }
                        composable("history") {
                            HistoryScreen(viewModel = viewModel)
                        }
                        composable("saved") {
                            SavedScreen(viewModel = viewModel)
                        }
                        composable("settings") {
                            SettingsScreen(viewModel = viewModel)
                        }
                        composable("auth") {
                            AuthScreen(viewModel = viewModel)
                        }
                        composable("roadmap") {
                            BcaRoadmapScreen(viewModel = viewModel)
                        }
                        composable("quiz") {
                            AiQuizScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
