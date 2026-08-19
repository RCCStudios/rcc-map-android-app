package cc.rccstudios.map.ui.screens.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.rccstudios.map.ui.MainViewModel
import cc.rccstudios.map.ui.AuthMode

@Composable
fun AccountScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val authMode = state.authMode
    val uriHandler = LocalUriHandler.current
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.getOtp()
    }

    when (authMode) {
        AuthMode.REGISTER -> RegisterScreen(
            viewModel = viewModel,
            state = state,
            haptic = haptic,
            modifier = modifier
        )
        AuthMode.LOGIN -> LoginScreen(
            viewModel = viewModel,
            state = state,
            haptic = haptic,
            modifier = modifier
        )
        AuthMode.LOGGED_IN -> ProfileScreen(
            viewModel = viewModel,
            scope = scope,
            state = state,
            context = context,
            uriHandler = uriHandler,
            haptic = haptic,
            modifier = modifier
        )
    }
}