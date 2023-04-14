package cn.edu.seu.travelapp.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.model.Food
import cn.edu.seu.travelapp.model.Spot
import cn.edu.seu.travelapp.ui.state.ExploreContentState
import cn.edu.seu.travelapp.ui.state.ExploreTopBarState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
import cn.edu.seu.travelapp.viewmodel.ExploreViewState
import coil.compose.AsyncImage

@Composable
fun RegionDetail(
    spotList: List<Spot>,
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: DataStorage,
    travelAppState: TravelAppState,
    paddingValues: PaddingValues
) {
    val spotFoldState = remember { mutableStateOf(true) }
    val foodFoldState = remember { mutableStateOf(true) }
    BackHandler(enabled = true) {
        travelAppState.bottomBarState.value = true
        if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.RESULT_REGION) {
            exploreViewModel.updateContentState(ExploreContentState.SEARCH)
            exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT)
        } else {
            exploreViewModel.updateContentState(ExploreContentState.REGIONS)
            exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
        }
    }
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    LazyColumn(
        modifier = Modifier
            .padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            )
            .background(MaterialTheme.colors.surface),
    ) {
        item {
            Column() {
                RegionIntro(
                    rname = exploreViewState.value.region!!.rname,
                    intro = exploreViewState.value.region!!.intro,
                    view = exploreViewState.value.region!!.view,
                    paddingValues = paddingValues
                )
                Surface(
                    elevation = 2.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "热门景点",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        if (spotFoldState.value) {
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        spotFoldState.value = !spotFoldState.value
                                    }
                                    .offset(y = 9.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.UnfoldLess,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        spotFoldState.value = !spotFoldState.value
                                    }
                                    .offset(y = 9.dp)
                            )
                        }

                    }
                    Spacer(modifier = Modifier.padding(30.dp))
                }
            }
        }
        itemsIndexed(spotList) { index, item ->
            Surface(
                elevation = 2.dp,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Column(
                ) {
                    if (spotFoldState.value) {
                        if (index < 5) {
                            SpotOutline(spot = item, onSpotClicked = {
                                exploreViewModel.spotClicked(item, token = token.value)
                            })
                        }
                    } else {
                        SpotOutline(spot = item, onSpotClicked = {
                            exploreViewModel.spotClicked(item, token = token.value)
                        })
                    }
                }
            }
        }
        if (exploreViewModel.foodShowState.value) {
            item {
                Surface(
                    elevation = 2.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "必吃美食",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.hggys)),
                            modifier = Modifier.offset(x = 10.dp, y = 10.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        if (foodFoldState.value) {
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        foodFoldState.value = !foodFoldState.value
                                    }
                                    .offset(y = 9.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.UnfoldLess,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        foodFoldState.value = !foodFoldState.value
                                    }
                                    .offset(y = 9.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(30.dp))
                }
            }
            itemsIndexed(exploreViewModel.foodListResponse) { index, item ->
                Surface(
                    elevation = 2.dp,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    Column(
                    ) {
                        if (foodFoldState.value) {
                            if (index < 5) {
                                FoodOutline(food = item) {
                                    exploreViewModel.foodClicked(food = item)
                                }
                            }
                        } else {
                            FoodOutline(food = item) {
                                exploreViewModel.foodClicked(food = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegionIntro(
    rname: String,
    intro: String,
    view: Int,
    paddingValues: PaddingValues
) {
    Column(

    ) {
        var introFoldState by remember { mutableStateOf(true) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Text(
                text = rname,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 60.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                tint = Color.Gray
            )
            Text(
                text = " $view 人看过",
                color = Color.Gray
            )
        }
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
                        if (introFoldState) {
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
                        } else {
                            IconButton(
                                onClick = {
                                    introFoldState = !introFoldState
                                },
                                modifier = Modifier.offset(y = 4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UnfoldLess,
                                    contentDescription = "expand"
                                )
                            }
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

@Composable
fun FoodOutline(
    food: Food,
    onFoodClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .clickable(
                onClick = onFoodClicked
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = food.pic,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = food.fname,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}