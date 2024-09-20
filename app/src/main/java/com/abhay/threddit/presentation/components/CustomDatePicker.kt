package com.abhay.threddit.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abhay.threddit.ui.theme.ThredditTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    onDateSelected: (Long?) -> Unit = {_ -> },
    onDismissRequest: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(currentYear - 100, currentYear)
    )

    val datePickColors = DatePickerDefaults.colors(
        todayDateBorderColor = MaterialTheme.colorScheme.onPrimary,
        todayContentColor = MaterialTheme.colorScheme.onSurface,
        selectedDayContentColor = MaterialTheme.colorScheme.primary,
        selectedDayContainerColor = MaterialTheme.colorScheme.onPrimary,
        selectedYearContainerColor = MaterialTheme.colorScheme.onSurface.copy(0.5f),
        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
    )

    DatePickerDialog(
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(text = "Ok", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = datePickColors
    ) {
        DatePicker(
            state = datePickerState,
            colors = datePickColors
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun DatePickerPreview() {
    ThredditTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CustomDatePicker()
        }
    }
}