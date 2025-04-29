package ru.nyxsed.postscan.presentation.screens.loginscreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.navController
import com.composegears.tiamat.navDestination
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.compose.onetap.OneTap
import com.vk.id.onetap.compose.onetap.OneTapTitleScenario
import org.koin.androidx.compose.koinViewModel
import ru.nyxsed.postscan.R
import ru.nyxsed.postscan.util.UiEvent

val LoginScreen by navDestination<Unit> {

    val context = LocalContext.current

    val navController = navController()

    val loginViewModel = koinViewModel<LoginViewModel>()
    LaunchedEffect(Unit) {
        loginViewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                else -> {}
            }
        }
    }

    Scaffold { paddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
                .padding(vertical = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp),
                painter = painterResource(id = R.drawable.vk_logo),
                contentDescription = null
            )

            OneTap(
                modifier = Modifier
                    .width(250.dp),
                onAuth = { oAuth, token ->
                    navController.back()
                },
                onFail = { oAuth, fail ->
                    loginViewModel.showLoginError(fail.description)
                },
                authParams = VKIDAuthUiParams {
                    scopes = setOf("wall", "offline", "groups")
                },
                scenario = OneTapTitleScenario.SignIn,
                fastAuthEnabled = true,
            )

        }
    }
}