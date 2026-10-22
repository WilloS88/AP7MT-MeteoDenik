package cz.utb.meteodenik.cv04.prehled

data class Mesto(val nazev: String, val teplota: Double)

fun Double.jakoCelsia(): String = "$this C"

sealed interface Stav {
    data object Nacita : Stav
    data object Chyba : Stav
    data object Prazdno : Stav
    data class Hotovo(val mesta: List<Mesto>) : Stav
}

fun textStavu(stav: Stav): String = when (stav) {
    Stav.Nacita -> "Načítám…"
    Stav.Chyba -> "Chyba"
    Stav.Prazdno -> "Nic tu není"
    is Stav.Hotovo -> "Měst: ${stav.mesta.size}"
}

class Prehled(private val mesta: List<Mesto>) {
    private val stav: Stav =
        if (mesta.isEmpty()) Stav.Prazdno else Stav.Hotovo(mesta)

    fun nejteplejsi(): Mesto? = mesta.maxByOrNull { it.teplota }

    fun vypis() {
        mesta.forEach { println("${it.nazev}: ${it.teplota.jakoCelsia()}") }
        println(textStavu(stav))
        nejteplejsi()?.let {
            println("Nejtepleji: ${it.nazev} (${it.teplota.jakoCelsia()})")
        }
    }
}

fun main() {
    Prehled(listOf(Mesto("Zlín", 21.4), Mesto("Brno", 23.0), Mesto("Otrokovice", 20.9))).vypis()
}