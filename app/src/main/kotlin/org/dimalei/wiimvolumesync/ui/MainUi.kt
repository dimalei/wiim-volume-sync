package org.dimalei.wiimvolumesync.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Text
import com.example.wiimvolumesync.ui.theme.WiiMVolumeSyncTheme
import org.dimalei.wiimvolumesync.activity.viewmodel.VolumeSyncModel

@Composable
fun Settings(model: VolumeSyncModel) {
    Scaffold() {


        when {
            model.openEditDialog.value -> {
                InputDialog(model)
            }
        }

        Row(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsPanel(model, Modifier.weight(1f))
            Box(Modifier.width(8.dp))
            LogPanel(model, Modifier.weight(1f))
        }
    }
}

@Composable
fun InputDialog(model: VolumeSyncModel) {
    Dialog(onDismissRequest = { model.openEditDialog.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Netflix Button URL") },
                    maxLines = 1,
                    value = model.netflixButtonUrl,
                    onValueChange = { model.netflixButtonUrl = it },
                    isError = model.volumeStepHasErrors,
                    supportingText = {
                        if (model.netflixActionUrlHasErrors) {
                            Text(
                                model.netflixActionUrlErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
                Button(
                    shape = ShapeDefaults.Small,
                    onClick = { model.openEditDialog.value = false }
                ) {
                    Text("close")
                }
            }


        }
    }
}


@Composable
fun LogPanel(model: VolumeSyncModel, modifier: Modifier) {
    val scrollState = rememberScrollState()

    LaunchedEffect(model.log) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState),
            text = model.log,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SettingsPanel(model: VolumeSyncModel, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Volume Sync Settings",
                style = MaterialTheme.typography.titleMedium,
            )
            ListItem(
                modifier = Modifier.clickable(onClick = { model.openEditDialog.value = true }),
                headlineContent = { Text("WiiM IP Address") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "WiiM Icon",
                    )
                },
                supportingContent = { Text(text = if (model.wiimIpAddress.isNotBlank()) model.wiimIpAddress else "Please enter IP") }
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("IP Address:") },
                maxLines = 1,
                value = model.wiimIpAddress,
                onValueChange = { model.wiimIpAddress = it },
                isError = model.ipHasErrors,
                supportingText = {
                    if (model.ipHasErrors) {
                        Text(
                            "Incorrect IP format",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Volume Increment:") },
                maxLines = 1,
                value = model.volumeStep,
                onValueChange = { model.volumeStep = it },
                isError = model.volumeStepHasErrors,
                supportingText = {
                    if (model.volumeStepHasErrors) {
                        Text(
                            model.volumeStepErrorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Netflix Button URL") },
                maxLines = 1,
                value = model.netflixButtonUrl,
                onValueChange = { model.netflixButtonUrl = it },
                isError = model.volumeStepHasErrors,
                supportingText = {
                    if (model.netflixActionUrlHasErrors) {
                        Text(
                            model.netflixActionUrlErrorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { model.verifyAndApply() },
                    enabled = !model.ipHasErrors && !model.volumeStepHasErrors
                ) { Text("Verify & Apply") }
                OutlinedButton(
                    onClick = { model.testManually() }
                ) { Text("Test") }
            }
        }
    }
}

@Composable
fun AppUI(model: VolumeSyncModel) {
    WiiMVolumeSyncTheme {
        Settings(model)
    }
}