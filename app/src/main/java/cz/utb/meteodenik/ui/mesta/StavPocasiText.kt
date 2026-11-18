package cz.utb.meteodenik.ui.mesta

import androidx.annotation.StringRes
import cz.utb.meteodenik.R
import cz.utb.meteodenik.model.StavPocasi

@StringRes
fun StavPocasi.popis(): Int = when (this) {
    StavPocasi.JASNO -> R.string.stav_jasno
    StavPocasi.POLOJASNO -> R.string.stav_polojasno
    StavPocasi.ZATAZENO -> R.string.stav_zatazeno
    StavPocasi.MLHA -> R.string.stav_mlha
    StavPocasi.MRHOLENI -> R.string.stav_mrholeni
    StavPocasi.DEST -> R.string.stav_dest
    StavPocasi.SNEZENI -> R.string.stav_snezeni
    StavPocasi.PREHANKY -> R.string.stav_prehanky
    StavPocasi.BOURKA -> R.string.stav_bourka
    StavPocasi.NEZNAMY -> R.string.stav_neznamy
}
