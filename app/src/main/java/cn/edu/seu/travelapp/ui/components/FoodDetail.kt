package cn.edu.seu.travelapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.model.Food
import coil.compose.AsyncImage

@Composable
fun FoodDetail(
    food: Food
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(
            state = scrollState
        )
    ) {
        Text(
            text = food.fname,
            fontFamily = FontFamily(Font(R.font.hggys)),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        AsyncImage(
            model = food.pic,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Text(
            text = food.intro,
            fontSize = 20.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}