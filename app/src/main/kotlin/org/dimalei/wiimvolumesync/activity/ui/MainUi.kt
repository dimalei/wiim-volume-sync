package org.dimalei.wiimvolumesync.activity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.dimalei.wiimvolumesync.activity.ui.theme.WiiMVolumeSyncTheme
import org.dimalei.wiimvolumesync.activity.viewmodel.VolumeSyncModel

@Composable
fun Settings(model: VolumeSyncModel) {
    Scaffold(Modifier.background(MaterialTheme.colorScheme.background)) {
        when {
            model.openEditDialog.value != VolumeSyncModel.EditDialog.NONE -> {
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
    Dialog(onDismissRequest = { model.openEditDialog.value = VolumeSyncModel.EditDialog.NONE }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (model.openEditDialog.value) {
                    VolumeSyncModel.EditDialog.WIIM_IP_ADDRESS -> {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("WiiM IP Address") },
                            maxLines = 1,
                            value = model.wiimIpAddress,
                            onValueChange = { model.wiimIpAddress = it },
                            isError = model.ipHasErrors,
                            supportingText = {
                                if (model.ipHasErrors) {
                                    Text(
                                        "Not a valid IP address",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            )
                        )
                    }

                    VolumeSyncModel.EditDialog.VOLUME_STEP -> {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Volume Step") },
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
                    }

                    VolumeSyncModel.EditDialog.NETFLIX_BUTTON_URL ->
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Netflix Button URL") },
                            maxLines = 1,
                            value = model.netflixButtonUrl,
                            onValueChange = { model.netflixButtonUrl = it },
                            isError = model.netflixActionUrlHasErrors,
                            supportingText = {
                                if (model.volumeStepHasErrors) {
                                    Text(
                                        "Must be a valid URL",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Done
                            )
                        )

                    VolumeSyncModel.EditDialog.NONE -> {}
                }
                Button(
                    shape = ShapeDefaults.Small,
                    onClick = { model.openEditDialog.value = VolumeSyncModel.EditDialog.NONE }
                ) {
                    Text("close")
                }
            }


        }
    }
}


@Composable
fun LogPanel(model: VolumeSyncModel, modifier: Modifier) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(model.logs.size) {
        if (model.logs.isNotEmpty())
            scrollState.animateScrollToItem(model.logs.size - 1)
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            state = scrollState
        ) {
            item { Box(modifier = Modifier.height(4.dp)) }
            items(
                count = model.logs.size,
                itemContent = { LogEntry(model.logs[it]) }
            )
            item { Box(modifier = Modifier.height(4.dp)) }
        }
    }
}

@Composable
fun LogEntry(log: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp)
    ) {
        Text(
            ">", fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodyMedium
        )
        Box(modifier = Modifier.width(2.dp))
        Text(
            log, fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SettingsPanel(model: VolumeSyncModel, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                text = "WiiM Volume Sync Settings"
            )
            Column(Modifier.padding(vertical = 8.dp)) {
                SettingsMenuItem(
                    headline = "WiiM IP Address",
                    support = model.wiimIpAddress,
                    onClick = {
                        model.openEditDialog.value =
                            VolumeSyncModel.EditDialog.WIIM_IP_ADDRESS
                    })
                SettingsMenuItem(
                    headline = "Volume Increment",
                    support = model.volumeStep,
                    onClick = {
                        model.openEditDialog.value =
                            VolumeSyncModel.EditDialog.VOLUME_STEP
                    })
                SettingsMenuItem(
                    headline = "Netflix Button URL",
                    support = model.netflixButtonUrl,
                    onClick = {
                        model.openEditDialog.value =
                            VolumeSyncModel.EditDialog.NETFLIX_BUTTON_URL
                    })
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { model.verifyAndApply() },
                    shape = MaterialTheme.shapes.small,
                    enabled = !model.ipHasErrors && !model.volumeStepHasErrors
                ) { Text("Verify & Apply") }
                OutlinedButton(
                    onClick = { model.testManually() },
                    shape = MaterialTheme.shapes.small
                ) { Text("Test") }
            }
        }
    }
}

@Composable
fun SettingsMenuItem(headline: String, support: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent),
        headlineContent = { Text(headline) },
        supportingContent = {
            Text(
                text = support,
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}

@Composable
fun AppUI(model: VolumeSyncModel) {
    WiiMVolumeSyncTheme {
        Settings(model)
    }
}
