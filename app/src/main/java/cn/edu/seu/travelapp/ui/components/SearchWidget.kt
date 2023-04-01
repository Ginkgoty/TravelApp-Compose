/**
 * SearchWidget.kt
 *
 * This file is ui of search results
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel

@Composable
fun SearchWidget(
    exploreViewModel: ExploreViewModel,
    paddingValues: PaddingValues
) {
    Column() {
        Surface(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            LazyColumn(

            ) {
                item {
                    Text(
                        text = "地区搜索结果",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
                if (exploreViewModel.searchRegionList.isNotEmpty()) {
                    items(exploreViewModel.searchRegionList) { item ->
                        Surface(
                            elevation = 4.dp
                        ) {
                            RegionOutline(region = item, onRegionClicked = {
                                exploreViewModel.resultRegionClicked(item)
                            })
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "无",
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
                item { Spacer(modifier = Modifier.size(10.dp)) }
                item {
                    Text(
                        text = "景点搜索结果",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
                if (exploreViewModel.searchSpotList.isNotEmpty()) {
                    items(exploreViewModel.searchSpotList) { item ->
                        Surface(
                            elevation = 4.dp
                        ) {
                            SpotOutline(spot = item, onSpotClicked = {
                                exploreViewModel.resultSpotClicked(item)
                            })
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "无",
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
    }
}