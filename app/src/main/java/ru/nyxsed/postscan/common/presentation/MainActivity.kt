package ru.nyxsed.postscan.common.presentation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.composegears.tiamat.Navigation
import com.composegears.tiamat.rememberNavController
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.refresh.VKIDRefreshTokenCallback
import com.vk.id.refresh.VKIDRefreshTokenFail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.common.domain.models.SettingKey
import ru.nyxsed.postscan.common.domain.repository.DataStoreRepository
import ru.nyxsed.postscan.common.presentation.screens.changegroupscreen.ChangeGroupScreen
import ru.nyxsed.postscan.common.presentation.screens.groupsscreen.GroupsScreen
import ru.nyxsed.postscan.common.presentation.screens.imagepagerscreen.ImagePagerScreen
import ru.nyxsed.postscan.common.presentation.screens.pickgroupscreen.PickGroupScreen
import ru.nyxsed.postscan.common.presentation.screens.postsscreen.PostsScreen
import ru.nyxsed.postscan.common.presentation.ui.theme.PostScanTheme
import ru.nyxsed.postscan.features.comments.presentation.CommentsScreen
import ru.nyxsed.postscan.features.login.presentation.LoginScreen
import ru.nyxsed.postscan.features.preferences.presentation.PreferencesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val datastore: DataStoreRepository by inject()
        lifecycleScope.launch {
            val isNotificationPermissionRequested =
                datastore.getBoolean(SettingKey.NOTIFICATION_PERMISSION_REQUESTED)

            if (!isNotificationPermissionRequested && !NotificationManagerCompat.from(this@MainActivity)
                    .areNotificationsEnabled()
            ) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.notification_permission))
                    .setMessage(getString(R.string.notification_permission_desc))
                    .setPositiveButton(getString(R.string.notification_permission_desc_settings)) { _, _ ->
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(intent)

                        lifecycleScope.launch(Dispatchers.IO) {
                            datastore.setBoolean(SettingKey.NOTIFICATION_PERMISSION_REQUESTED, true)
                        }
                    }
                    .setNegativeButton(getString(R.string.notification_permission_desc_cancel)) { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            datastore.setBoolean(SettingKey.NOTIFICATION_PERMISSION_REQUESTED, true)
                        }
                    }
                    .show()
            }
        }

        setContent {
            val navController = rememberNavController(
                startDestination = PostsScreen,
                destinations = arrayOf(
                    PostsScreen,
                    LoginScreen,
                    GroupsScreen,
                    ChangeGroupScreen,
                    PickGroupScreen,
                    ImagePagerScreen,
                    CommentsScreen,
                    PreferencesScreen,
                )
            )
            PostScanTheme {
                Navigation(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val currentToken = VKID.Companion.instance.accessToken
            if (currentToken?.token == null) return@launch

            if (currentToken.expireTime <= System.currentTimeMillis()) {
                VKID.instance.refreshToken(
                    callback = object : VKIDRefreshTokenCallback {
                        override fun onSuccess(token: AccessToken) {
                            // do nothing
                        }

                        override fun onFail(fail: VKIDRefreshTokenFail) {
                            Toast.makeText(this@MainActivity, R.string.refresh_token_fail, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}