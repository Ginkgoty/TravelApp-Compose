/**
 * RegionCard.kt
 *
 * This file is ui of region item in main view and search view
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.model.Region

@Composable
fun RegionCard(
    region: Region,
    onCardClicked: () -> Unit
) {
    val width = (LocalConfiguration.current.screenWidthDp / 2 - 10).dp
    val hggys = FontFamily(Font(R.font.hggys))
    Box(
        modifier = Modifier
            .clickable(onClick = onCardClicked)
            .padding(3.dp),
//        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = region.pic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = region.rname,
            fontWeight = FontWeight.Normal,
            fontFamily = hggys,
            fontSize = 24.sp,
        )
    }
}

@Composable
fun RegionOutline(
    region: Region,
    onRegionClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .clickable(
                onClick = onRegionClicked
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = region.pic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = region.rname,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = if (region.intro.length > 30) {
                    region.intro.substring(0, 30) + "..."
                } else {
                    region.intro
                },
                fontSize = 18.sp
            )
        }
    }
}

@Preview
@Composable
fun RegionCardPreview() {
}