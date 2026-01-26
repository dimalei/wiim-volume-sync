package org.dimalei.wiimvolumesync.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.example.wiimvolumesync.ui.theme.WiiMVolumeSyncTheme
import org.dimalei.wiimvolumesync.viewmodel.VolumeSyncModel

@Composable
fun Settings(model: VolumeSyncModel) {
    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Volume Sync Settings",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                modifier = Modifier.width(200.dp),
                label = { Text("IP Address:") },
                state = model.ipTextFieldState,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            OutlinedTextField(
                modifier = Modifier.width(200.dp),
                label = { Text("Max Volume:") },
                state = model.maxVolTextFieldState,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Button(
                onClick = { model.apply() }
            ) { Text("Apply") }
        }
    }
}

@Composable
fun AppUI(model: VolumeSyncModel) {
    WiiMVolumeSyncTheme {
        Settings(model)
    }
}