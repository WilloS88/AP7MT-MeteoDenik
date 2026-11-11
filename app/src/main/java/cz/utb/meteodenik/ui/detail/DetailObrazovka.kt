package cz.utb.meteodenik.ui.detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.meteodenik.R
import cz.utb.meteodenik.model.Mesto
import cz.utb.meteodenik.ui.theme.MeteoDenikTheme

@Composable
fun DetailObrazovka(
    onZpet: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val stav by viewModel.uiState.collectAsStateWithLifecycle()

    DetailObsah(stav = stav, onZpet = onZpet)
}

@Composable
private fun DetailObsah(
    stav: DetailUiState,
    onZpet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val mesto = stav.mesto
            if (mesto == null) {
                Text(
                    text = stringResource(R.string.detail_nenalezeno),
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Text(
                    text = mesto.nazev,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = if (mesto.teplotaC != null) {
                        stringResource(R.string.teplota_format, mesto.teplotaC)
                    } else {
                        stringResource(R.string.teplota_chybi)
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onZpet) {
                Text(text = stringResource(R.string.detail_zpet))
            }
        }
    }
}

@Preview(name = "Detail — světlý", showBackground = true)
@Preview(name = "Detail — tmavý", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DetailObsahPreview() {
    MeteoDenikTheme {
        DetailObsah(
            stav = DetailUiState(mesto = Mesto(id = 1, nazev = "Zlín", teplotaC = 21.4)),
            onZpet = {}
        )
    }
}

@Preview(name = "Detail — bez teploty", showBackground = true)
@Composable
private fun DetailObsahBezTeplotyPreview() {
    MeteoDenikTheme {
        DetailObsah(
            stav = DetailUiState(mesto = Mesto(id = 4, nazev = "Otrokovice", teplotaC = null)),
            onZpet = {}
        )
    }
}

@Preview(name = "Detail — město neexistuje", showBackground = true)
@Composable
private fun DetailObsahNenalezenoPreview() {
    MeteoDenikTheme {
        DetailObsah(stav = DetailUiState(mesto = null), onZpet = {})
    }
}
