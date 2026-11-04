package cz.utb.meteodenik.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.utb.meteodenik.R

@Composable
fun RozbityMestaSeznam(modifier: Modifier = Modifier) {

    var novyNazev by rememberSaveable { mutableStateOf("") }
    var mesta by remember { mutableStateOf(listOf("Zlín", "Brno", "Ostrava", "Olomouc")) }

    Column(modifier = modifier.padding(16.dp)) {

        OutlinedTextField(
            value = novyNazev,
            onValueChange = { novyNazev = it },
            label = { Text(stringResource(R.string.mesto_pole_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val nazev = novyNazev.trim()
            if (nazev.isNotEmpty() && mesta.none { it.equals(nazev, ignoreCase = true) }) {
                mesta = mesta + nazev
                novyNazev = ""
            }
        }) {
            Text(stringResource(R.string.mesto_pridat))
        }

        Text(
            text = pluralStringResource(R.plurals.mesta_pocet, mesta.size, mesta.size),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = mesta, key = { nazev -> nazev }) { nazev ->
                var rozbaleno by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { rozbaleno = !rozbaleno }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = nazev, modifier = Modifier.weight(1f))
                            TextButton(onClick = { mesta = mesta - nazev }) {
                                Text(stringResource(R.string.mesto_smazat))
                            }
                        }
                        if (rozbaleno) {
                            Text(
                                text = stringResource(R.string.mesto_teplota_neznama),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
