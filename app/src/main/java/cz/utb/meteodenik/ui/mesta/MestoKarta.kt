package cz.utb.meteodenik.ui.mesta

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.R
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.model.MeteoZaznam
import cz.utb.meteodenik.model.StavPocasi
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme
import java.time.LocalDateTime

@Composable
fun MestoKarta(
    radek: MestoSPocasim,
    modifier: Modifier = Modifier,
    vybrane: Boolean = false
) {
    val mesto = radek.mesto
    val pocasi = radek.pocasi

    Card(
        modifier = modifier.fillMaxWidth(),
        border = if (vybrane) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .semantics { contentDescription = mesto.nazev },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mesto.nazev.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mesto.nazev,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (pocasi != null) {
                        stringResource(
                            R.string.mesto_teplota_a_stav,
                            stringResource(R.string.teplota_format, pocasi.teplotaC),
                            stringResource(pocasi.stav.popis())
                        )
                    } else {
                        stringResource(R.string.teplota_chybi)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val ukazkoveMereni = MeteoZaznam(
    cas = LocalDateTime.of(2026, 11, 18, 14, 15),
    teplotaC = 8.4,
    vlhkostProcent = 72,
    rychlostVetruKmH = 11.5,
    stav = StavPocasi.POLOJASNO
)

@Preview(name = "Karta - svetla", showBackground = true)
@Preview(name = "Karta - tmava", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MestoKartaPreview() {
    MeteoDenikTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            MestoKarta(
                radek = MestoSPocasim(Mesto(1, "Zlín", 49.2265, 17.6683), ukazkoveMereni)
            )
            Spacer(modifier = Modifier.size(8.dp))
            MestoKarta(
                radek = MestoSPocasim(Mesto(2, "Brno", 49.1951, 16.6068)),
                vybrane = true
            )
        }
    }
}
