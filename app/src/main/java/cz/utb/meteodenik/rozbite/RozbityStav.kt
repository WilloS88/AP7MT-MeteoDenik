package cz.utb.meteodenik.rozbite

// ZÁMĚRNĚ ROZBITÝ KÓD — čtyři chyby, každá jiného druhu.

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.utb.meteodenik.model.Mesto
import kotlinx.coroutines.flow.MutableStateFlow

data class RozbityUiState(
    val mesta: List<Mesto> = emptyList()
)

class RozbityViewModel(private val context: Context) : ViewModel() {

    val uiState = MutableStateFlow(RozbityUiState())

    fun nacti() {
        uiState.value = RozbityUiState(
            mesta = listOf(
                Mesto(1, "Zlín", 21.4),
                Mesto(2, "Brno", 23.1)
            )
        )
    }

    fun nazevAplikace(): String = context.getString(
        context.applicationInfo.labelRes
    )
}

class RozbitaFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RozbityViewModel(context) as T
    }
}

@Composable
fun RozbityGraf() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "seznam") {
        composable("seznam") { RozbitySeznam(navController) }
        composable("detail") { Text("Detail") }
    }
}

@Composable
fun RozbitySeznam(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: RozbityViewModel = viewModel(factory = RozbitaFactory(context))
    val stav by viewModel.uiState.collectAsState()

    var pocetOtevreni by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Otevřeno detailů: $pocetOtevreni")
        Button(onClick = { viewModel.nacti() }) { Text("Načíst") }
        Button(onClick = { viewModel.uiState.value = RozbityUiState() }) { Text("Vymazat") }

        stav.mesta.forEach { mesto ->
            Text(
                text = mesto.nazev,
                modifier = Modifier
                    .clickable {
                        pocetOtevreni++
                        navController.navigate("detail/${mesto.id}")
                    }
                    .padding(vertical = 12.dp)
            )
        }
    }
}
