package cc.rccstudios.map.ui.dialogs.timeperiod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.rccstudios.map.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePeriodDialog(
    title: String,
    startText: String,
    endText: String,
    onDismissRequest: () -> Unit,
    onConfirm: (startMinuteOfDay: Int, endMinuteOfDay: Int) -> Unit,
    initialStartMinute: Int = 0,
    initialEndMinute: Int = 0,
) {
    val startState = rememberTimePickerState(
        initialHour = initialStartMinute / 60,
        initialMinute = initialStartMinute % 60,
        is24Hour = true
    )
    val endState = rememberTimePickerState(
        initialHour = initialEndMinute / 60,
        initialMinute = initialEndMinute % 60,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = startText,
                    style = MaterialTheme.typography.titleMedium
                )
                TimeInput(state = startState)

                Text(
                    text = endText,
                    style = MaterialTheme.typography.titleMedium
                )
                TimeInput(state = endState)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val start = startState.hour * 60 + startState.minute
                val end = endState.hour * 60 + endState.minute
                onConfirm(start, end)
                onDismissRequest()
            }) {
                Text(stringResource(R.string.save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

