package ru.nyxsed.postscan.core.event

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import com.composegears.tiamat.NavController
import com.composegears.tiamat.NavDestination
import kotlinx.coroutines.flow.Flow
import ru.nyxsed.postscan.core.util.Constants.MANGA_SEARCH_ACTION

@Composable
fun CollectUiEvent(
    uiEventFlow: Flow<UiEvent>,
    navController: NavController? = null,
    scrollState: LazyListState? = null,
    snackbarHostState: SnackbarHostState? = null,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(uiEventFlow) {
        uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is UiEvent.Scroll ->
                    scrollState?.scrollToItem(0)

                is UiEvent.NavigateBack ->
                    navController?.back()

                is UiEvent.NavigateTo<*> -> {
                    val destination = event.destination as NavDestination<Any?>
                    navController?.navigate(destination, event.navArgs)
                }

                is UiEvent.OpenUrl ->
                    uriHandler?.openUri(event.url)

                is UiEvent.OpenMihon -> {
                    val intent = Intent().apply {
                        action = MANGA_SEARCH_ACTION
                        val cleanedText = event.query
                            .replace(Regex("\\r?\\n"), " ")
                            .replace(Regex("[\\p{So}\\p{Cn}]|[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]"), "")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("query", cleanedText)
                    }
                    context.startActivity(intent)
                }

                is UiEvent.CopyToClipboard -> {
                    clipboardManager.setText(
                        annotatedString = AnnotatedString(event.text)
                    )
                }

                is UiEvent.ShowSnackbar -> {
                    snackbarHostState?.currentSnackbarData?.dismiss()
                    val snackbarResult = snackbarHostState?.showSnackbar(
                        message = context.getString(event.messageID),
                        actionLabel = context.getString(event.actionLabelID),
                        duration = SnackbarDuration.Short
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                        event.onAction()
                    }
                }
            }
        }
    }
}