package com.example.eventoslocales

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventoslocales.data.SessionPrefs
import com.example.eventoslocales.data.ThemePrefs
import com.example.eventoslocales.ui.theme.AppTheme
import com.example.eventoslocales.ui.theme.LoginScreen
import com.example.eventoslocales.ui.theme.MapEventsScreen
import com.example.eventoslocales.ui.theme.SettingsScreen
import com.example.eventoslocales.ui.theme.viewmodel.AuthViewModel
import com.example.eventoslocales.ui.theme.viewmodel.EventsViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object MapEvents : Screen("map_events")
    object Settings : Screen("settings")
}

class MainActivity : ComponentActivity() {

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> /* manejar resultado si lo necesitas */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun checkAndRequestPermissions() {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        checkAndRequestPermissions()

        setContent {
            val context = this@MainActivity
            val scope = rememberCoroutineScope()


            val darkTheme by ThemePrefs.darkThemeFlow(context).collectAsStateWithLifecycle(false)
            val loggedIn by SessionPrefs.loggedInFlow(context).collectAsStateWithLifecycle(false)

            AppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        startDestination = if (loggedIn) Screen.MapEvents.route else Screen.Login.route,
                        darkTheme = darkTheme,
                        onToggleTheme = { newValue ->
                            scope.launch { ThemePrefs.setDarkTheme(context, newValue) }
                        },
                        onSetLoggedIn = { value ->
                            scope.launch { SessionPrefs.setLoggedIn(context, value) }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    startDestination: String,
    darkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onSetLoggedIn: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val eventsViewModel: EventsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    onSetLoggedIn(true) // recuerda sesión
                    navController.navigate(Screen.MapEvents.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.MapEvents.route) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            text = "Menú",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("Ajustes") },
                            selected = false,
                            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                            onClick = {
                                navController.navigate(Screen.Settings.route)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Eventos Locales") },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menú")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding)) {
                        MapEventsScreen(
                            viewModel = eventsViewModel,
                            onLogout = {
                                onSetLoggedIn(false) // olvida sesión
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.MapEvents.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }


        composable(Screen.Settings.route) {
            SettingsScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onLogout = {
                    onSetLoggedIn(false)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
