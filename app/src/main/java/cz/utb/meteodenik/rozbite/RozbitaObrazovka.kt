package cz.utb.meteodenik.rozbite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.meteodenik.R
import cz.utb.meteodenik.ui.mesta.textProUzivatele

@Composable
fun RozbitaObrazovka(viewModel: RozbityViewModel = viewModel()) {
    val stav by viewModel.stav.collectAsStateWithLifecycle()

    Column(modifier = Modifier.safeDrawingPadding().padding(16.dp)) {
        when (val aktualni = stav) {
            RozbityStav.Nacitam -> CircularProgressIndicator()
            is RozbityStav.Hotovo -> Text(text = stringResource(R.string.teplota_format, aktualni.teplotaC))
            is RozbityStav.Chyba -> {
                Text(text = stringResource(aktualni.duvod.textProUzivatele()))
                Button(onClick = { viewModel.nacti() }) {
                    Text(stringResource(R.string.akce_zkusit_znovu))
                }
            }
        }
    }
}
