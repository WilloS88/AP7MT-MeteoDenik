package cz.utb.meteodenik.ui.mesta

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.R
import cz.utb.meteodenik.data.DuvodSelhani
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.StavPocasi
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme
import java.time.LocalDateTime

@Composable
fun SeznamMestObsah(
    stav: MestaUiState,
    onZkusitZnovu: () -> Unit,
    onMestoKliknuto: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        if (stav.nacitaSe) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (stav.chyba != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(stav.chyba.textProUzivatele()),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onZkusitZnovu) {
                    Text(stringResource(R.string.akce_zkusit_znovu))
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stav.mesta, key = { it.mesto.id }) { radek ->
                MestoKarta(
                    radek = radek,
                    vybrane = radek.mesto.id == stav.vybraneMestoId,
                    modifier = Modifier.clickable { onMestoKliknuto(radek.mesto.id) }
                )
            }
        }
    }
}

private val ukazkoveRadky = listOf(
    MestoSPocasim(
        Mesto(1, "Zlín", 49.2265, 17.6683),
        MeteoZaznam(LocalDateTime.of(2026, 11, 18, 14, 15), 8.4, 72, 11.5, StavPocasi.POLOJASNO)
    ),
    MestoSPocasim(
        Mesto(2, "Brno", 49.1951, 16.6068),
        MeteoZaznam(LocalDateTime.of(2026, 11, 18, 14, 15), 9.1, 65, 18.0, StavPocasi.ZATAZENO)
    ),
    MestoSPocasim(Mesto(3, "Ostrava", 49.8209, 18.2625))
)

@Preview(name = "Seznam - hotovo", showBackground = true)
@Preview(name = "Seznam - tmavy", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SeznamHotovoPreview() {
    MeteoDenikTheme {
        SeznamMestObsah(
            stav = MestaUiState(mesta = ukazkoveRadky, vybraneMestoId = 2),
            onZkusitZnovu = {},
            onMestoKliknuto = {}
        )
    }
}

@Preview(name = "Seznam - nacitani", showBackground = true)
@Composable
private fun SeznamNacitaniPreview() {
    MeteoDenikTheme {
        SeznamMestObsah(
            stav = MestaUiState(mesta = ukazkoveRadky.map { it.copy(pocasi = null) }, nacitaSe = true),
            onZkusitZnovu = {},
            onMestoKliknuto = {}
        )
    }
}

@Preview(name = "Seznam - chyba", showBackground = true)
@Composable
private fun SeznamChybaPreview() {
    MeteoDenikTheme {
        SeznamMestObsah(
            stav = MestaUiState(mesta = ukazkoveRadky, chyba = DuvodSelhani.BEZ_PRIPOJENI),
            onZkusitZnovu = {},
            onMestoKliknuto = {}
        )
    }
}
