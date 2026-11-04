package cz.utb.meteodenik.ui

import android.content.res.Configuration
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
import androidx.compose.material3.CardDefaults
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
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

@Composable
fun VizitkaKarta(
    jmeno: String,
    obor: String,
    email: String,
    modifier: Modifier = Modifier
) {
    val avatarPopis = stringResource(R.string.vizitka_avatar_popis, jmeno)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .semantics { contentDescription = avatarPopis },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicialy(jmeno),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // weight(1f): sloupec zabere zbytek řádku a dlouhý text se zlomí uvnitř karty
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jmeno,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = obor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.vizitka_email_popis, email),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** „Roman Vilčák“ → „RV“; nejvýš dvě písmena. */
private fun inicialy(jmeno: String): String =
    jmeno.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.first().uppercase() }

@Preview(name = "Vizitka — světlá", showBackground = true)
@Preview(name = "Vizitka — tmavá", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Vizitka — písmo 150 %", showBackground = true, fontScale = 1.5f, widthDp = 360)
@Composable
private fun VizitkaKartaPreview() {
    MeteoDenikTheme {
        VizitkaKarta(
            jmeno = "Roman Vilčák",
            obor = "Informační technologie · 3. ročník",   // nezlomitelná mezera: „3.“ a „ročník“ zůstanou na jednom řádku
            email = "r_vilcak@utb.cz",
            modifier = Modifier.padding(16.dp)
        )
    }
}
