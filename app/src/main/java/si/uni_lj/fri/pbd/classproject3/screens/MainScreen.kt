package si.uni_lj.fri.pbd.classproject3.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import si.uni_lj.fri.pbd.classproject3.ui.theme.ClassProject3Theme

sealed class Screen(val route: String, val title: String) {
    object Search : Screen("search", "Search")
    object Favorites : Screen("favorites", "Favorites")
    object RecipeDetails : Screen("recipe_details/{recipeId}/{fromFavorites}", "Recipe Details") {
        fun createRoute(recipeId: String, fromFavorites: Boolean = false) = "recipe_details/$recipeId/$fromFavorites"
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetails.createRoute(recipeId, fromFavorites = false))
                    }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onRecipeClick = { recipeId ->
                        navController.navigate(Screen.RecipeDetails.createRoute(recipeId, fromFavorites = true))
                    }
                )
            }

            composable(Screen.RecipeDetails.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                val fromFavorites = backStackEntry.arguments?.getString("fromFavorites")?.toBoolean() ?: false
                RecipeDetailsScreen(
                    recipeId = recipeId,
                    fromFavorites = fromFavorites,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Search.route } == true,
            onClick = {
                navController.navigate(Screen.Search.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites"
                )
            },
            label = { Text("Favorites") },
            selected = currentDestination?.hierarchy?.any { it.route == Screen.Favorites.route } == true,
            onClick = {
                navController.navigate(Screen.Favorites.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}