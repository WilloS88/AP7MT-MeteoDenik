package cz.utb.meteodenik.ui.mesta

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.utb.meteodenik.data.DuvodSelhani
import cz.utb.meteodenik.data.MestaZdroj
import cz.utb.meteodenik.data.MeteoRepository
import cz.utb.meteodenik.data.Repozitare
import cz.utb.meteodenik.data.VysledekSite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val KLIC_VYBRANE_MESTO = "vybraneMestoId"

class MestaViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository: MeteoRepository = Repozitare.meteo

    private val _uiState = MutableStateFlow(
        MestaUiState(
            mesta = vychoziMesta(),
            vybraneMestoId = savedStateHandle.get<Int>(KLIC_VYBRANE_MESTO)
        )
    )
    val uiState: StateFlow<MestaUiState> = _uiState.asStateFlow()

    init {
        obnov()
    }

    fun vyberMesto(id: Int) {
        savedStateHandle[KLIC_VYBRANE_MESTO] = id
        _uiState.update { it.copy(vybraneMestoId = id) }
    }

    fun obnov(vynutit: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(nacitaSe = true, chyba = null) }
            try {
                var prvniChyba: DuvodSelhani? = null
                val doplnena = _uiState.value.mesta.map { radek ->
                    when (val vysledek = repository.aktualniPocasi(radek.mesto, vynutit)) {
                        is VysledekSite.Uspech -> radek.copy(pocasi = vysledek.data)
                        is VysledekSite.Selhani -> {
                            if (prvniChyba == null) prvniChyba = vysledek.duvod
                            radek
                        }
                    }
                }
                _uiState.update { it.copy(mesta = doplnena, chyba = prvniChyba) }
            } finally {
                _uiState.update { it.copy(nacitaSe = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Stav", "onCleared")
    }

    private fun vychoziMesta(): List<MestoSPocasim> =
        MestaZdroj.mesta.value.map { mesto -> MestoSPocasim(mesto) }
}
