package cz.utb.meteodenik.ui.mesta

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.utb.meteodenik.data.MestaZdroj
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val KLIC_VYBRANE_MESTO = "vybraneMestoId"

class MestaViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vybraneMestoId: StateFlow<Int?> =
        savedStateHandle.getStateFlow<Int?>(KLIC_VYBRANE_MESTO, null)

    private val jeDuplikat = MutableStateFlow(false)

    val uiState: StateFlow<MestaUiState> =
        combine(MestaZdroj.mesta, vybraneMestoId, jeDuplikat) { seznam, vybraneId, duplikat ->
            MestaUiState(mesta = seznam, vybraneMestoId = vybraneId, jeDuplikat = duplikat)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MestaUiState()
        )

    fun vyberMesto(id: Int) {
        savedStateHandle[KLIC_VYBRANE_MESTO] = id
    }

    fun pridejMesto(nazev: String): Boolean {
        val cistyNazev = nazev.trim()
        if (cistyNazev.isEmpty()) return false

        val uzExistuje = MestaZdroj.mesta.value.any { mesto ->
            mesto.nazev.trim().equals(cistyNazev, ignoreCase = true)
        }
        jeDuplikat.value = uzExistuje
        if (uzExistuje) return false

        MestaZdroj.pridej(cistyNazev)
        return true
    }

    fun zrusChybuDuplikatu() {
        jeDuplikat.value = false
    }

    fun smazMesto(id: Int) {
        if (vybraneMestoId.value == id) {
            savedStateHandle[KLIC_VYBRANE_MESTO] = null
        }
        MestaZdroj.smaz(id)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Stav", "onCleared")
    }
}
