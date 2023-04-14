package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.ui.state.TravelAppViewState
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun TokenErrorSnackBar(
    exploreViewModel: ExploreViewModel
) {
    if (exploreViewModel.tokenDialogState) {
        Snackbar(
            backgroundColor = Color.White,
        ) {
            Text(
                text = "您还未登录，请先登录！",
                color = MaterialTheme.colors.primary
            )
        }
    }
    LaunchedEffect(key1 = exploreViewModel.tokenDialogState) {
        delay(2000)
        exploreViewModel.showTokenError(false)
    }
}

@Composable
fun CloseAlertDialog(
    state: MutableState<Boolean>,
    isSave: MutableState<Boolean>,
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState,
    editorType: String
) {
    AlertDialog(
        onDismissRequest = {

        },
        title = {
            Text(
                text = "退出游记编辑"
            )
        },
        text = {
            Text(
                text = "您将退出游记编辑页面，如需要保存请返回并点击\"保存\"。"
            )
        },
        buttons = {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = {
                        state.value = false
//                        homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                        travelAppState.topBarState.value = true
                        travelAppState.bottomBarState.value = true
                        when (travelAppState.travelAppViewState.value) {
                            TravelAppViewState.HOME -> travelAppState.navController.navigate(route = "home")
                            TravelAppViewState.EXPLORE -> travelAppState.navController.navigate(
                                route = "explore"
                            )
                            TravelAppViewState.FAVORITE -> travelAppState.navController.navigate(
                                route = "favorite"
                            )
                            TravelAppViewState.ME -> travelAppState.navController.navigate(route = "me")
                            else -> {}
                        }
                        if (!isSave.value) {
                            when (editorType) {
                                "note" -> homeViewModel.clearNoteEditor()
                                "pictxt" -> homeViewModel.clearPictxtEditor()
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                    )
                    Text(
                        text = "退出编辑"
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = {
                        state.value = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "check",
                    )
                    Text(
                        text = "继续编辑"
                    )
                }
            }
        },
        modifier = Modifier.padding(10.dp)
    )
}