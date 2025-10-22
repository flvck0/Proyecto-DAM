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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eventoslocales.data.SessionPrefs
import com.example.eventoslocales.data.ThemePrefs
import com.example.eventoslocales.ui.theme.AppTheme
import com.example.eventoslocales.ui.theme.EventDetailScreen
import com.example.eventoslocales.ui.theme.LoginScreen
import com.example.eventoslocales.ui.theme.MapEventsScreen
import com.example.eventoslocales.ui.theme.SettingsScreen
import com.example.eventoslocales.ui.theme.viewmodel.AuthViewModel
import com.example.eventoslocales.ui.theme.viewmodel.EventsViewModel
import kotlinx.coroutines.launch

// Navegación declarativa: centralizo las rutas en un sealed class para evitar strings mágicos.
// Ventaja: autocompletado y menos riesgo de typos en rutas y argumentos.
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object MapEvents : Screen("map_events")
    object Settings : Screen("settings")

    // Ruta con argumento: defino plantilla y helper para construir la ruta tipada.
    object EventDetail : Screen("event_detail/{eventId}") {
        fun route(eventId: Int) = "event_detail/$eventId"
    }
}

class MainActivity : ComponentActivity() {

    // Permiso de ubicación: uso el API moderno de Activity Result para pedir FINE_LOCATION.
    // Decisión: sólo pido permiso, no proceso el boolean aquí; la UI reaccionará según acceso real a ubicación.
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> /* puedo loguear o mostrar snack si quisiera */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Política de permisos en arranque:
        // 1) Si ya está concedido, sigo normal.
        // 2) Si no, disparo la solicitud. La app funciona; la carga real de eventos cercanos
        //    dependerá de que el ViewModel/Location layer valide permisos antes de pedir coordenadas.
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
            // Scope para operaciones suspend (guardar prefs) disparadas desde callbacks de UI.
            val context = this@MainActivity
            val scope = rememberCoroutineScope()

            // Estado de tema y sesión como flows persistidos:
            // - Los leo con collectAsStateWithLifecycle para respetar lifecycle y evitar leaks/actualizaciones en background.
            val darkTheme by ThemePrefs.darkThemeFlow(context).collectAsStateWithLifecycle(false)
            val loggedIn by SessionPrefs.loggedInFlow(context).collectAsStateWithLifecycle(false)

            // Envuelvo todo en mi tema para mantener tipografía/colores coherentes.
            AppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Decisión de startDestination en caliente según sesión:
                    // si hay login persistido salto directo al mapa; si no, voy a la pantalla de login.
                    AppNavigation(
                        startDestination = if (loggedIn) Screen.MapEvents.route else Screen.Login.route,
                        darkTheme = darkTheme,
                        // Guardado de preferencia de tema: lo hago desde aquí para mantener la UI dumb.
                        onToggleTheme = { newValue ->
                            scope.launch { ThemePrefs.setDarkTheme(context, newValue) }
                        },
                        // Persisto el estado de login: esto me permite matar el back stack y volver a Login limpio.
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
    // Controller de navegación Compose: una única fuente para navegar entre pantallas.
    val navController = rememberNavController()

    // ViewModels scopeados al NavBackStack (por defecto, a la NavGraph del Compose host).
    // Decisión: los instancio aquí para compartirlos entre pantallas dentro del mismo NavHost.
    val authViewModel: AuthViewModel = viewModel()
    val eventsViewModel: EventsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        //  Login
        composable(Screen.Login.route) {
            // Login notifica éxito hacia arriba; aquí marco sesión como iniciada y limpio el back stack.
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    onSetLoggedIn(true)
                    navController.navigate(Screen.MapEvents.route) {
                        // popUpTo + inclusive elimina Login del back stack para que no se pueda volver con back.
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        //  Mapa + Drawer
        composable(Screen.MapEvents.route) {
            // Drawer de Material 3: lo uso para exponer entradas de navegación lateral (ej. Ajustes).
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

                        // Entrada a Ajustes: navego y cierro el drawer para mantener UX fluida.
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
                // App bar + content scaffold: patrón estándar de Material.
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Eventos Locales") },
                            navigationIcon = {
                                // Icono de hamburguesa abre el drawer.
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menú")
                                }
                            }
                        )
                    }
                ) { padding ->
                    // Contenido del mapa: le delego la lógica de cargar/mostrar eventos al EventsViewModel.
                    Box(Modifier.fillMaxSize().padding(padding)) {
                        MapEventsScreen(
                            viewModel = eventsViewModel,
                            // Navego a detalle usando ruta tipada (evito concatenar strings a mano).
                            onOpenDetail = { id -> navController.navigate(Screen.EventDetail.route(id)) }
                        )
                    }
                }
            }
        }

        //  Ajustes
        composable(Screen.Settings.route) {
            // Ajustes controla tema y permite cerrar sesión desde un solo lugar.
            SettingsScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                onLogout = {
                    // Cerrar sesión: limpio flag persistido y regreso a Login vaciando el back stack actual.
                    onSetLoggedIn(false)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }

        // -------- Detalle de evento (ruta con argumento) --------
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Obtengo parámetro de navegación de forma segura; si falla, salgo silenciosamente del composable.
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable

            // Decisión: busco el evento en el VM para mantener una única fuente de datos en memoria
            // y evitar pasar objetos pesados por navegación.
            val event = eventsViewModel.getEventById(eventId)

            if (event != null) {
                EventDetailScreen(
                    event = event,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Fallback de UX simple: si no encuentro el evento (p.ej. lista no cargada),
                // muestro mensaje. Alternativa: podría disparar una carga puntual por ID.
                Text("Evento no encontrado")
            }
        }
    }
}
