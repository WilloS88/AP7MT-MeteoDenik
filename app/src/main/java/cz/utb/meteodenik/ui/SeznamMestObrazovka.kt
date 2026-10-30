package cz.utb.meteodenik.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.DetailMestaActivity
import cz.utb.meteodenik.Mesto
import cz.utb.meteodenik.R
import cz.utb.meteodenik.ZaznamUlozenReceiver
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

val UKAZKOVA_MESTA = listOf(
    Mesto("Zlín", 21.4),
    Mesto("Brno", 23.1),
    Mesto("Ostrava", 19.8)
)

@Composable
fun SeznamMestObrazovka(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        val pocet = UKAZKOVA_MESTA.size
        Text(
            text = stringResource(
                R.string.seznam_titulek_pocet,
                pluralStringResource(R.plurals.pocet_zaznamu, pocet, pocet)
            ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall
        )

        PolohaKarta(onPolohaPovolena = {})

        UKAZKOVA_MESTA.forEach { mesto ->
            Row(
                modifier = Modifier
                    .background(colorResource(R.color.pozadi_karty))
                    .fillMaxWidth()
                    .clickable(onClickLabel = stringResource(R.string.otevri_detail)) {
                        val intent = Intent(context, DetailMestaActivity::class.java).apply {
                            putExtra(DetailMestaActivity.EXTRA_MESTO, mesto.nazev)
                            putExtra(DetailMestaActivity.EXTRA_TEPLOTA, mesto.teplota)
                        }
                        context.startActivity(intent)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = mesto.nazev)
                Text(
                    text = stringResource(R.string.teplota_format, mesto.teplota),
                    color = colorResource(R.color.text_zvyrazneni)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            context.sendBroadcast(
                Intent(ZaznamUlozenReceiver.ACTION_ZAZNAM_ULOZEN).apply {
                    setPackage(context.packageName)
                    putExtra(ZaznamUlozenReceiver.EXTRA_MESTO, UKAZKOVA_MESTA.first().nazev)
                }
            )
        }) {
            Text(stringResource(R.string.poslat_zpravu))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeznamMestObrazovkaPreview() {
    MeteoDenikTheme { SeznamMestObrazovka() }
}
