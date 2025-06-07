package ru.nyxsed.postscan.core.event

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import com.composegears.tiamat.NavController
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectUiEvent(
    uiEventFlow: Flow<UiEvent>,
    navController: NavController? = null,
    scrollState: LazyListState? = null,
    circularIndicatorState: MutableState<Boolean>? = null,
    uriHandler: UriHandler? = null
) {
    val context = LocalContext.current

    LaunchedEffect(uiEventFlow) {
        uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is UiEvent.Navigate ->
                    navController?.navigate(event.destination)

                is UiEvent.NavigateToPost ->
                    navController?.navigate(event.destination, event.navArgs)

                is UiEvent.Scroll ->
                    scrollState?.scrollToItem(0)

                is UiEvent.UpdateStatus ->
                    circularIndicatorState?.value = event.status

                is UiEvent.NavigateBack ->
                    navController?.back()

                is UiEvent.NavigateToChangeGroup ->
                    navController?.navigate(event.destination, event.navArgs)

                is UiEvent.NavigateToPicker ->
                    navController?.navigate(event.destination, event.navArgs)

                is UiEvent.OpenUrl ->
                    uriHandler?.openUri(event.url)
            }
        }
    }
}