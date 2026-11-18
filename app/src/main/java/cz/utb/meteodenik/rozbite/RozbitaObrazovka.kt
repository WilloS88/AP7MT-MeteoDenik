package cz.utb.meteodenik.rozbite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RozbitaObrazovka(viewModel: RozbityViewModel = viewModel()) {
    val teplota by viewModel.teplota.collectAsStateWithLifecycle()

    Column(modifier = Modifier.safeDrawingPadding().padding(16.dp)) {
        Text(text = teplota?.toString() ?: "-")
        Button(onClick = { viewModel.nacti() }) {
            Text("Nacist")
        }
    }
}
