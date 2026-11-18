package cz.utb.meteodenik.data.remote

import cz.utb.meteodenik.data.remote.dto.GeoMistoDto
import cz.utb.meteodenik.data.remote.dto.PredpovedDto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.NalezeneMesto
import cz.utb.meteodenik.model.StavPocasi
import java.time.LocalDateTime

internal fun PredpovedDto.toDomain(): MeteoZaznam = MeteoZaznam(
    cas = LocalDateTime.parse(current.time),
    teplotaC = current.teplotaC,
    vlhkostProcent = current.vlhkostProcent,
    rychlostVetruKmH = current.rychlostVetruKmH,
    stav = wmoNaStav(current.kodPocasi)
)

internal fun GeoMistoDto.toDomain(): NalezeneMesto = NalezeneMesto(
    nazev = name,
    kraj = admin1,
    stat = country,
    sirka = latitude,
    delka = longitude
)

private fun wmoNaStav(kod: Int): StavPocasi = when (kod) {
    0 -> StavPocasi.JASNO
    1, 2 -> StavPocasi.POLOJASNO
    3 -> StavPocasi.ZATAZENO
    45, 48 -> StavPocasi.MLHA
    51, 53, 55, 56, 57 -> StavPocasi.MRHOLENI
    61, 63, 65, 66, 67 -> StavPocasi.DEST
    71, 73, 75, 77 -> StavPocasi.SNEZENI
    80, 81, 82, 85, 86 -> StavPocasi.PREHANKY
    95, 96, 99 -> StavPocasi.BOURKA
    else -> StavPocasi.NEZNAMY
}
