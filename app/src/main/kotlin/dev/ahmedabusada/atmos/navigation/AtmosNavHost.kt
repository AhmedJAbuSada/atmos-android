package dev.ahmedabusada.atmos.navigation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ahmedabusada.atmos.BuildConfig
import dev.ahmedabusada.atmos.feature.about.AboutScreen
import dev.ahmedabusada.atmos.feature.cities.CitiesRoute
import dev.ahmedabusada.atmos.feature.home.HomeRoute
import dev.ahmedabusada.atmos.feature.settings.SettingsRoute
import kotlinx.serialization.Serializable

/**
 * Type-safe routes: a destination is a class, so a typo in a route is a compile error, and
 * arguments (if a route ever needs them) are typed properties, not strings in a URL.
 */
@Serializable data object HomeDestination

@Serializable data object CitiesDestination

@Serializable data object SettingsDestination

@Serializable data object AboutDestination

/** Home → Cities, Home → Settings → About (wireframes, "Navigation"). */
@Composable
fun AtmosNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = HomeDestination, modifier = modifier) {
        composable<HomeDestination> {
            HomeRoute(
                onOpenCities = { navController.navigate(CitiesDestination) },
                onOpenSettings = { navController.navigate(SettingsDestination) },
            )
        }
        composable<CitiesDestination> { CitiesRoute(onClose = navController::popBackStack) }
        composable<SettingsDestination> {
            SettingsRoute(onBack = navController::popBackStack, onOpenAbout = {
                navController.navigate(AboutDestination)
            })
        }
        composable<AboutDestination> {
            AboutScreen(
                versionName = BuildConfig.VERSION_NAME,
                onOpenLink = { url ->
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (_: ActivityNotFoundException) {
                        // No browser installed: nothing sensible to do.
                    }
                },
                onBack = navController::popBackStack,
            )
        }
    }
}
