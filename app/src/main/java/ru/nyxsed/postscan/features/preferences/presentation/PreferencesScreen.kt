package ru.nyxsed.postscan.features.preferences.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.event.CollectUiEvent
import ru.nyxsed.postscan.uikit.components.BasicButton
import ru.nyxsed.postscan.uikit.components.SettingRow


val PreferencesScreen by navDestination<Unit> {
    val preferencesViewModel = koinViewModel<PreferencesViewModel>()
    val state by preferencesViewModel.state.collectAsState()

    CollectUiEvent(preferencesViewModel.uiEventFlow)

    PreferencesScreenContent(
        state = state,
        processIntent = {
            preferencesViewModel.processIntent(it)
        }
    )
}

@Composable
fun PreferencesScreenContent(
    state: PreferencesState,
    processIntent: (PreferencesIntent) -> Unit,
) {
    Scaffold { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(4.dp)
        ) {
            val launcherImport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                uri?.let { selectedUri ->
                    processIntent(PreferencesIntent.ImportDB(selectedUri))
                }
            }

            val launcherExport =
                rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
                    uri?.let { selectedUri ->
                        processIntent(PreferencesIntent.ExportDB(selectedUri))
                    }
                }

            SettingRow(
                label = stringResource(R.string.not_load_liked_posts),
                checked = state.notLoadLikedPosts,
                onCheckChange = {
                    processIntent(PreferencesIntent.ToggleSetting(SettingKey.NOT_LOAD_LIKED_POSTS, it))
                }
            )
            SettingRow(
                label = stringResource(R.string.use_mihon_for_manga_search),
                checked = state.useMihon,
                onCheckChange = {
                    processIntent(PreferencesIntent.ToggleSetting(SettingKey.USE_MIHON, it))
                }
            )
            SettingRow(
                label = stringResource(R.string.delete_post_after_liking),
                checked = state.deleteAfterLike,
                onCheckChange = {
                    processIntent(PreferencesIntent.ToggleSetting(SettingKey.DELETE_AFTER_LIKE, it))
                }
            )
            BasicButton(
                label = stringResource(R.string.vk_logout),
                onClick = {
                    processIntent(PreferencesIntent.Logout)
                }
            )
            BasicButton(
                label = stringResource(R.string.import_db),
                onClick = {
                    launcherImport.launch(arrayOf("application/octet-stream", "application/x-sqlite3"))
                }
            )
            BasicButton(
                label = stringResource(R.string.export_db),
                onClick = {
                    launcherExport.launch("app_database")
                }
            )
            BasicButton(
                label = stringResource(R.string.show_tutorial),
                onClick = {
                    processIntent(PreferencesIntent.ResetTutorial)
                }
            )
        }
    }
}