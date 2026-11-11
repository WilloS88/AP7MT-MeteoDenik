package cz.utb.meteodenik.navigace

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.utb.meteodenik.ui.detail.DetailObrazovka
import cz.utb.meteodenik.ui.mesta.SeznamMestObrazovka

@Composable
fun MeteoDenikNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Seznam) {

        composable<Seznam> {
            SeznamMestObrazovka(
                onMestoKliknuto = { id ->
                    navController.navigate(Detail(mestoId = id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Detail> {
            DetailObrazovka(
                onZpet = { navController.popBackStack() }
            )
        }
    }
}
