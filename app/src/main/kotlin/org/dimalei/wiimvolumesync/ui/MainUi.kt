package org.dimalei.wiimvolumesync.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.example.wiimvolumesync.ui.theme.WiiMVolumeSyncTheme
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

@Composable
fun Settings(model: VolumeSyncModel) {
    Scaffold() {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SettingsPanel(model)
            Box(Modifier.width(16.dp))
            LogPanel(model)
        }
    }
}

@Composable
fun LogPanel(model: VolumeSyncModel) {
    Box(Modifier.padding(vertical = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(400.dp)
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(8.dp)
        ) {
            Text(
                "Test Output",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            LazyColumn() {
                items(model.logMessages.value) {
                    Text(
                        "> $it",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsPanel(model: VolumeSyncModel) {
    Column(
        modifier = Modifier
            .width(400.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Volume Sync Settings",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("IP Address:") },
            state = model.ipTextFieldState,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Max Volume:") },
            state = model.maxVolTextFieldState,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Public Key Pin:") },
            state = model.pinBaseFieldState,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { model.apply() }
            ) { Text("Apply") }
            OutlinedButton(
                onClick = { model.getPublicKeyPin() }
            ) { Text("Get Key Pin") }
            OutlinedButton(
                onClick = { model.test() }
            ) { Text("Test") }
        }
    }
}

@Composable
fun AppUI(model: VolumeSyncModel) {
    WiiMVolumeSyncTheme {
        Settings(model)
    }
}