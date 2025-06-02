package ru.nyxsed.postscan.uikit.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.util.Constants.DATE_MASK
import ru.nyxsed.postscan.core.util.MaskVisualTransformation
import java.util.Calendar


@Composable
fun DatePickerTextField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var showDatePicker by remember { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clickable { showDatePicker = true },
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clickable { showDatePicker = true },
                contentDescription = null,
                imageVector = Icons.Filled.DateRange
            )
        },
        visualTransformation = MaskVisualTransformation(DATE_MASK),
    )


    if (showDatePicker) {
        DatePickerDialog(
            context,
            R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "${selectedDay.toString().padStart(2, '0')}${
                    (selectedMonth + 1).toString().padStart(2, '0')
                }$selectedYear"
                onDateSelected(formattedDate)
                showDatePicker = false
            },
            year,
            month,
            day
        ).apply {
            setOnDismissListener {
                showDatePicker = false
            }
            datePicker.setOnDateChangedListener { _, newYear, newMonth, newDay ->
                val formattedDate =
                    "${newDay.toString().padStart(2, '0')}${(newMonth + 1).toString().padStart(2, '0')}$newYear"
                onDateSelected(formattedDate)
                showDatePicker = false
                dismiss()
            }
        }.show()
    }
}