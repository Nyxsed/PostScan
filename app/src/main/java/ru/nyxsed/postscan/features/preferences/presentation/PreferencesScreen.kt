package ru.nyxsed.postscan.features.preferences.presentation

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.navDestination
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.core.domain.models.SettingKey
import ru.nyxsed.postscan.core.util.UiEvent

val PreferencesScreen by navDestination<Unit> {
    val preferencesViewModel = koinViewModel<PreferencesScreenViewModel>()
    val context = LocalContext.current

    var settingNotLoadLikedPosts = preferencesViewModel.settingNotLoadLikedPosts.collectAsState()
    var settingUseMihon = preferencesViewModel.settingUseMihon.collectAsState()
    var settingDeleteAfterLike = preferencesViewModel.settingDeleteAfterLike.collectAsState()

    LaunchedEffect(Unit) {
        preferencesViewModel.loadSettings()
        preferencesViewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                else -> {}
            }
        }
    }

    PreferencesScreenContent(
        preferencesViewModel = preferencesViewModel,
        settingNotLoadLikedPosts = settingNotLoadLikedPosts,
        settingUseMihon = settingUseMihon,
        settingDeleteAfterLike = settingDeleteAfterLike,
        context = context,
    )
}

@Composable
fun PreferencesScreenContent(
    preferencesViewModel: PreferencesScreenViewModel,
    settingNotLoadLikedPosts: State<Boolean>,
    settingUseMihon: State<Boolean>,
    settingDeleteAfterLike: State<Boolean>,
    context: Context,
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
                    preferencesViewModel.importDataBaseFromFile(selectedUri)
                }
            }

            val launcherExport =
                rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
                    uri?.let { selectedUri ->
                        preferencesViewModel.exportDataBaseToFile(selectedUri)
                    }
                }

            SettingRow(
                label = stringResource(R.string.not_load_liked_posts),
                checked = settingNotLoadLikedPosts.value,
                onCheckChange = {
                    preferencesViewModel.saveSettingBoolean(SettingKey.NOT_LOAD_LIKED_POSTS, it)
                }
            )
            SettingRow(
                label = stringResource(R.string.use_mihon_for_manga_search),
                checked = settingUseMihon.value,
                onCheckChange = {
                    preferencesViewModel.saveSettingBoolean(SettingKey.USE_MIHON, it)
                }
            )
            SettingRow(
                label = stringResource(R.string.delete_post_after_liking),
                checked = settingDeleteAfterLike.value,
                onCheckChange = {
                    preferencesViewModel.saveSettingBoolean(SettingKey.DELETE_AFTER_LIKE, it)
                }
            )
            SettingButton(
                label = stringResource(R.string.vk_logout),
                onClick = {
                    preferencesViewModel.logOut()
                }
            )
            SettingButton(
                label = stringResource(R.string.import_db),
                onClick = {
                    launcherImport.launch(arrayOf("application/octet-stream", "application/x-sqlite3"))
                }
            )
            SettingButton(
                label = stringResource(R.string.export_db),
                onClick = {
                    launcherExport.launch("app_database")
                }
            )
            SettingButton(
                label = stringResource(R.string.show_tutorial),
                onClick = {
                    preferencesViewModel.saveSettingBoolean(SettingKey.SHOWED_TUTORIAL_POSTS, false)
                    preferencesViewModel.saveSettingBoolean(SettingKey.SHOWED_TUTORIAL_GROUPS, false)
                    preferencesViewModel.saveSettingBoolean(SettingKey.SHOWED_TUTORIAL_IMAGE, false)
                }
            )
        }
    }
}

@Composable
fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = label
        )
        Checkbox(
            checked = checked,
            onCheckedChange = {
                onCheckChange(it)
            }
        )
    }
}

@Composable
fun SettingButton(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                onClick()
            }
        ) {
            Text(text = label)
        }
    }
}