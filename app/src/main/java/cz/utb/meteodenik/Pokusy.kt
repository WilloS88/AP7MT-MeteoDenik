package cz.utb.meteodenik

data class Mesto(val nazev: String, val teplota: Double)

object Konfigurace {
    const val ZAKLADNI_URL = "https://api.open-meteo.com/v1/forecast"
}

fun Double.naStupneCelsia(): String = "%.1f °C".format(this)

fun vypisVse(mesta: List<Mesto>, akce: (Mesto) -> Unit) {
    mesta.forEach { akce(it) }
}

suspend fun pockejAVrat(): Int {
    kotlinx.coroutines.delay(100)
    return 42
}