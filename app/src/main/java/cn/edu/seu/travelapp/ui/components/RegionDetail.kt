/**
 * RegionDetail.kt
 *
 * This file is ui of region's detail, including spots in this region
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.model.Spot
import cn.edu.seu.travelapp.ui.state.ExploreContentState
import cn.edu.seu.travelapp.ui.state.ExploreTopBarState
import cn.edu.seu.travelapp.ui.state.MeContentState
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
import cn.edu.seu.travelapp.viewmodel.ExploreViewState
import coil.compose.AsyncImage

@Composable
fun SpotList(
    spotList: List<Spot>,
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: TokenStorage,
    paddingValues: PaddingValues
) {
    BackHandler(enabled = true) {
        if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.RESULT_REGION) {
            exploreViewModel.updateContentState(ExploreContentState.SEARCH)
            exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT)
        }else{
            exploreViewModel.updateContentState(ExploreContentState.REGIONS)
            exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
        }
    }
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    LazyColumn(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding(), top = paddingValues.calculateTopPadding())
            .background(MaterialTheme.colors.surface)
    ) {
        item {
            RegionIntro(
                rname = exploreViewState.value.region!!.rname,
                intro = exploreViewState.value.region!!.intro,
                paddingValues = paddingValues
            )
        }
        items(spotList) { item ->
            Surface(
                elevation = 2.dp,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Column(
                ) {
                    SpotOutline(spot = item, onSpotClicked = {
                        exploreViewModel.spotClicked(item, token = token.value)
                    })
                }
            }
        }
    }
}

@Composable
fun RegionIntro(
    rname: String,
    intro: String,
    paddingValues: PaddingValues
) {
    Column(

    ) {
        var introFoldState by remember { mutableStateOf(true) }
        Text(
            text = rname,
            fontFamily = FontFamily(Font(R.font.hggys)),
            fontSize = 60.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp)
        )
        Surface(
            elevation = 2.dp,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {
            Column() {
                if (intro.length > 200) {
                    Row() {
                        Text(
                            text = "景点介绍",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                        )
                        IconButton(
                            onClick = {
                                introFoldState = !introFoldState
                            },
                            modifier = Modifier.offset(y = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = "expand"
                            )
                        }
                    }
                } else {
                    Text(
                        text = "地区简介",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.offset(x = 10.dp, y = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
                if (intro.length > 200 && introFoldState) {
                    Text(
                        text = "        $intro".substring(0, 200) + "...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    )
                } else {
                    Text(
                        text = "        $intro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Surface(
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = "热门景点",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.hggys)),
                modifier = Modifier.offset(x = 10.dp, y = 10.dp)
            )
            Spacer(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()))
        }
    }
}

@Composable
fun SpotOutline(
    spot: Spot,
    onSpotClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .clickable(
                onClick = onSpotClicked
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = spot.pic,
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
                text = spot.sname,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = spot.intro,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
    }
}