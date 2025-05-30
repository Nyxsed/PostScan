package ru.nyxsed.postscan.common.presentation.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.util.Constants.toDateLong
import ru.nyxsed.postscan.common.util.Constants.toStringDate

@Composable
fun DownloadModalDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDownloadClicked: (String, String) -> Unit,
) {
    val currentTime = System.currentTimeMillis().toStringDate().replace(".", "")
    var startDate by remember { mutableStateOf(currentTime) }
    var endDate by remember { mutableStateOf(currentTime) }
    val regex = Regex("^([0-2][0-9]|3[01])(0[1-9]|1[0-2])[0-9]{4}$")

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Text(
                        text = stringResource(R.string.download_posts),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(R.string.download_posts_in_this_range)
                    )
                    DatePickerTextField(
                        label = stringResource(R.string.start_date),
                        selectedDate = startDate,
                        onDateSelected = { newDate ->
                            startDate = newDate
                        }
                    )
                    DatePickerTextField(
                        label = stringResource(R.string.end_date),
                        selectedDate = endDate,
                        onDateSelected = { newDate ->
                            endDate = newDate
                        }
                    )
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        enabled = regex.matches(startDate) && regex.matches(endDate) && startDate.toDateLong() <= endDate.toDateLong(),
                        onClick = {
                            onDownloadClicked(startDate, endDate)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.download)
                        )
                    }
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteModalDialog(
    title: String,
    description: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmClicked: () -> Unit,
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = description
                    )
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onConfirmClicked()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.confirm)
                        )
                    }
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AddModalDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSearchClicked: () -> Unit,
    onPickClicked: () -> Unit,
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_group),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = stringResource(R.string.group_search_dialog_question)
                    )
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onSearchClicked()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.search_for)
                        )
                    }
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onPickClicked()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.pick_from)
                        )
                    }
                }
            }
        }
    }
}