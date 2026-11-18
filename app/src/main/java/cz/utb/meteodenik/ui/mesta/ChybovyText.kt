package cz.utb.meteodenik.ui.mesta

import androidx.annotation.StringRes
import cz.utb.meteodenik.R
import cz.utb.meteodenik.data.DuvodSelhani

@StringRes
fun DuvodSelhani.textProUzivatele(): Int = when (this) {
    DuvodSelhani.BEZ_PRIPOJENI -> R.string.chyba_bez_pripojeni
    DuvodSelhani.SERVER_NEODPOVIDA -> R.string.chyba_server_neodpovida
    DuvodSelhani.PRILIS_MNOHO_DOTAZU -> R.string.chyba_prilis_mnoho_dotazu
    DuvodSelhani.CHYBA_POZADAVKU -> R.string.chyba_pozadavku
    DuvodSelhani.CHYBA_SERVERU -> R.string.chyba_serveru
    DuvodSelhani.NEPLATNA_ODPOVED -> R.string.chyba_neplatna_odpoved
    DuvodSelhani.JINA_CHYBA_SITE -> R.string.chyba_jina_site
}
