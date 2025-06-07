package ru.nyxsed.postscan.core.event

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectUiEvent(
    uiEventFlow: Flow<UiEvent>,
) {
    val context = LocalContext.current
    LaunchedEffect(uiEventFlow) {
        uiEventFlow.collect {event ->
            when(event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}