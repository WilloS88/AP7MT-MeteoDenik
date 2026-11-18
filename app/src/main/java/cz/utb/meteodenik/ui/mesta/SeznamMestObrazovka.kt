package cz.utb.meteodenik.ui.mesta

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.meteodenik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeznamMestObrazovka(
    onMestoKliknuto: (Int) -> Unit,
    viewModel: MestaViewModel = viewModel()
) {
    val stav by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.mesta_titulek)) },
                actions = {
                    TextButton(onClick = { viewModel.obnov(vynutit = true) }) {
                        Text(stringResource(R.string.akce_obnovit))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { insety ->
        SeznamMestObsah(
            stav = stav,
            onZkusitZnovu = { viewModel.obnov(vynutit = true) },
            onMestoKliknuto = { id ->
                viewModel.vyberMesto(id)
                onMestoKliknuto(id)
            },
            modifier = Modifier.padding(insety)
        )
    }
}
