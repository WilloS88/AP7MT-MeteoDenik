package cz.utb.meteodenik.rozbite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.navigace.Detail
import cz.utb.meteodenik.navigace.Seznam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RozbityUiState(
    val mesta: List<Mesto> = emptyList()
)

class RozbityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RozbityUiState())
    val uiState: StateFlow<RozbityUiState> = _uiState.asStateFlow()

    fun nacti() {
        _uiState.value = RozbityUiState(
            mesta = listOf(
                Mesto(1, "Zlín", 21.4),
                Mesto(2, "Brno", 23.1)
            )
        )
    }

    fun vymaz() {
        _uiState.value = RozbityUiState()
    }
}

@Composable
fun RozbityGraf() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Seznam) {
        composable<Seznam> {
            RozbitySeznam(
                onMestoKliknuto = { id ->
                    navController.navigate(Detail(mestoId = id)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Detail> { Text("Detail") }
    }
}

@Composable
fun RozbitySeznam(onMestoKliknuto: (Int) -> Unit) {
    val viewModel: RozbityViewModel = viewModel()
    val stav by viewModel.uiState.collectAsStateWithLifecycle()

    var pocetOtevreni by rememberSaveable { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Otevřeno detailů: $pocetOtevreni")
        Button(onClick = { viewModel.nacti() }) { Text("Načíst") }
        Button(onClick = { viewModel.vymaz() }) { Text("Vymazat") }

        stav.mesta.forEach { mesto ->
            Text(
                text = mesto.nazev,
                modifier = Modifier
                    .clickable {
                        pocetOtevreni++
                        onMestoKliknuto(mesto.id)
                    }
                    .padding(vertical = 12.dp)
            )
        }
    }
}
