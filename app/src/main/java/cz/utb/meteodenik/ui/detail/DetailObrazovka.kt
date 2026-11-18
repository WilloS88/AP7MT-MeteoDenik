package cz.utb.meteodenik.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.meteodenik.R
import cz.utb.meteodenik.data.DuvodSelhani
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.StavPocasi
import cz.utb.meteodenik.ui.mesta.popis
import cz.utb.meteodenik.ui.mesta.textProUzivatele
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DetailObrazovka(
    onZpet: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val stav by viewModel.uiState.collectAsStateWithLifecycle()

    DetailObsah(
        stav = stav,
        onZkusitZnovu = { viewModel.obnov(vynutit = true) },
        onZpet = onZpet
    )
}

@Composable
private fun DetailObsah(
    stav: DetailUiState,
    onZkusitZnovu: () -> Unit,
    onZpet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (stav.nacitaSe) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Column(modifier = Modifier.padding(16.dp)) {
                val mesto = stav.mesto
                if (mesto == null) {
                    Text(
                        text = stringResource(R.string.detail_nenalezeno),
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Text(text = mesto.nazev, style = MaterialTheme.typography.headlineMedium)
                    val pocasi = stav.pocasi
                    if (pocasi != null) {
                        Text(
                            text = stringResource(
                                R.string.mesto_teplota_a_stav,
                                stringResource(R.string.teplota_format, pocasi.teplotaC),
                                stringResource(pocasi.stav.popis())
                            ),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = stringResource(R.string.detail_vlhkost, pocasi.vlhkostProcent),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(text = stringResource(R.string.detail_vitr, pocasi.rychlostVetruKmH))
                        Text(
                            text = stringResource(
                                R.string.detail_cas,
                                pocasi.cas.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
                            )
                        )
                    } else if (stav.chyba == null) {
                        Text(
                            text = stringResource(R.string.teplota_chybi),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                if (stav.chyba != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
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

                Button(onClick = onZpet, modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = stringResource(R.string.detail_zpet))
                }
            }
        }
    }
}

private val ukazkoveMesto = Mesto(1, "Zlín", 49.2265, 17.6683)
private val ukazkoveMereni = MeteoZaznam(
    cas = LocalDateTime.of(2026, 11, 18, 14, 15),
    teplotaC = 8.4,
    vlhkostProcent = 72,
    rychlostVetruKmH = 11.5,
    stav = StavPocasi.POLOJASNO
)

@Preview(name = "Detail - svetly", showBackground = true)
@Preview(name = "Detail - tmavy", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DetailHotovoPreview() {
    MeteoDenikTheme {
        DetailObsah(
            stav = DetailUiState(mesto = ukazkoveMesto, pocasi = ukazkoveMereni),
            onZkusitZnovu = {},
            onZpet = {}
        )
    }
}

@Preview(name = "Detail - chyba", showBackground = true)
@Composable
private fun DetailChybaPreview() {
    MeteoDenikTheme {
        DetailObsah(
            stav = DetailUiState(mesto = ukazkoveMesto, chyba = DuvodSelhani.BEZ_PRIPOJENI),
            onZkusitZnovu = {},
            onZpet = {}
        )
    }
}

@Preview(name = "Detail - mesto neexistuje", showBackground = true)
@Composable
private fun DetailNenalezenoPreview() {
    MeteoDenikTheme {
        DetailObsah(stav = DetailUiState(), onZkusitZnovu = {}, onZpet = {})
    }
}
