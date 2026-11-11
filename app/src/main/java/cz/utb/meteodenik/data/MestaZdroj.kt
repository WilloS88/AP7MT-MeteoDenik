package cz.utb.meteodenik.data

import cz.utb.meteodenik.model.Mesto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MestaZdroj {

    private val _mesta = MutableStateFlow(
        listOf(
            Mesto(id = 1, nazev = "Zlín", teplotaC = 21.4),
            Mesto(id = 2, nazev = "Brno", teplotaC = 23.1),
            Mesto(id = 3, nazev = "Ostrava", teplotaC = 19.8)
        )
    )
    val mesta: StateFlow<List<Mesto>> = _mesta.asStateFlow()

    fun pridej(nazev: String) {
        _mesta.update { seznam ->
            val noveId = (seznam.maxOfOrNull { mesto -> mesto.id } ?: 0) + 1
            seznam + Mesto(id = noveId, nazev = nazev, teplotaC = null)
        }
    }

    fun smaz(id: Int) {
        _mesta.update { seznam -> seznam.filterNot { mesto -> mesto.id == id } }
    }

    fun podleId(id: Int): Mesto? = _mesta.value.firstOrNull { mesto -> mesto.id == id }
}
