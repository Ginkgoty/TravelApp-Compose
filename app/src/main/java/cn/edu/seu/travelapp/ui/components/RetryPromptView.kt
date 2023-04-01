/**
 * RetryPromptView.kt
 *
 * This file is ui when your network is bad.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R

@Composable
fun RetryPromptView(
    paddingValues: PaddingValues,
    onViewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
            .clickable {
                onViewClick()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Sync,
            contentDescription = "Sync",
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = "网络异常，请点击重试",
            fontSize = 34.sp,
            fontFamily = FontFamily(Font(R.font.hggys)),
        )
    }


}