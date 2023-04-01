/**
 * AlertMessage.kt
 *
 * This file is alert dialog when user not login
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
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
        delay(2000)  // show 2 sec
        exploreViewModel.showTokenError(false)
    }
}