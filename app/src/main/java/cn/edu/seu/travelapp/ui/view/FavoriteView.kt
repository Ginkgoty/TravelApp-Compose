package cn.edu.seu.travelapp.ui.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.model.Spot
import cn.edu.seu.travelapp.ui.components.RegionIntro
import cn.edu.seu.travelapp.ui.components.RegionOutline
import cn.edu.seu.travelapp.ui.components.SpotDetail
import cn.edu.seu.travelapp.ui.components.SpotOutline
import cn.edu.seu.travelapp.ui.state.*
import cn.edu.seu.travelapp.viewmodel.FavoriteViewModel
import cn.edu.seu.travelapp.viewmodel.FavoriteViewState
import kotlinx.coroutines.delay

@Composable
fun FavoriteView(
    travelAppState: TravelAppState,
    favoriteViewModel: FavoriteViewModel,
    paddingValues: PaddingValues,
    token: String
) {
    val favoriteViewState = favoriteViewModel.uiState.collectAsState()
    if (travelAppState.isLogin.value) {
        when (favoriteViewState.value.favoriteContentState) {
            FavoriteContentState.FAVORITE -> {
                BackHandler(enabled = true) {

                }
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
                                text = "收藏的地区",
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                fontFamily = FontFamily(Font(R.font.hggys)),
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                        if (favoriteViewModel.regionListResponse.isNotEmpty()) {
                            items(favoriteViewModel.regionListResponse) { item ->
                                Surface(
                                    elevation = 4.dp
                                ) {
                                    RegionOutline(region = item, onRegionClicked = {
                                        travelAppState.bottomBarState.value = false
                                        favoriteViewModel.regionClicked(item)
                                    })
                                }
                                Spacer(modifier = Modifier.size(5.dp))
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
                                text = "收藏的景点",
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                fontFamily = FontFamily(Font(R.font.hggys)),
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                        if (favoriteViewModel.spotListResponse.isNotEmpty()) {
                            items(favoriteViewModel.spotListResponse) { item ->
                                Surface(
                                    elevation = 4.dp
                                ) {
                                    SpotOutline(spot = item, onSpotClicked = {
                                        travelAppState.bottomBarState.value = false
                                        favoriteViewModel.spotClicked(item)
                                    })
                                }
                                Spacer(modifier = Modifier.size(5.dp))
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
            FavoriteContentState.SPOTS -> {
                BackHandler(enabled = true) {
                    travelAppState.bottomBarState.value = true
                    favoriteViewModel.getFavSpotList(token)
                    favoriteViewModel.getFavRegionList(token)
                    favoriteViewModel.updateTopBarState(FavoriteTopBarState.DEFAULT)
                    favoriteViewModel.updateContentState(FavoriteContentState.FAVORITE)
                }

                val snackState = remember { mutableStateOf(false) }
                if (snackState.value) {
                    Snackbar(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    ) {
                        Text("操作失败，请重试")
                    }
                }
                LaunchedEffect(key1 = favoriteViewModel.opState) {
                    snackState.value = !favoriteViewModel.opState.value
                    delay(2000)
                    snackState.value = false
                }
                FavoriteSpotList(
                    spotList = favoriteViewModel.spotsInRegion,
                    favoriteViewModel = favoriteViewModel,
                    favoriteViewState = favoriteViewState,
                    paddingValues = paddingValues
                )
            }
            FavoriteContentState.DETAIL -> {
                BackHandler(enabled = true) {
                    when (favoriteViewState.value.favoriteTopBarState) {
                        FavoriteTopBarState.SPOT -> {
                            travelAppState.bottomBarState.value = true
                            favoriteViewModel.getFavRegionList(token)
                            favoriteViewModel.getFavSpotList(token)
                            favoriteViewModel.updateTopBarState(FavoriteTopBarState.DEFAULT)
                            favoriteViewModel.updateContentState(FavoriteContentState.FAVORITE)
                        }
                        FavoriteTopBarState.SPOT_FROM_REGION -> {
                            favoriteViewModel.updateTopBarState(FavoriteTopBarState.REGION)
                            favoriteViewModel.updateContentState(FavoriteContentState.SPOTS)
                        }
                        else -> {

                        }
                    }
                }

                val snackState = remember { mutableStateOf(false) }
                if (snackState.value) {
                    Snackbar(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        contentColor = MaterialTheme.colors.primary
                    ) {
                        Text("操作失败，请重试")
                    }
                }
                LaunchedEffect(key1 = favoriteViewModel.opState) {
                    snackState.value = !favoriteViewModel.opState.value
                    delay(2000)
                    snackState.value = false
                }
                SpotDetail(
                    sname = favoriteViewState.value.spot!!.sname,
                    detail = favoriteViewModel.detailResponse,
                    paddingValues = paddingValues
                )
            }
        }
    } else {
        BackHandler(enabled = true) {

        }
        LoginPrompt(paddingValues = paddingValues)
    }
}

@Composable
fun LoginPrompt(
    paddingValues: PaddingValues
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.PersonAdd,
                contentDescription = "ICON",
                modifier = Modifier.size(128.dp)
            )
        }
        Text(
            text = "您还未登录，请先登录",
            fontSize = 30.sp,
            fontFamily = FontFamily(Font(R.font.hggys)),
        )
    }
}

@Composable
fun FavoriteAppBar(
    favoriteViewModel: FavoriteViewModel,
    favoriteViewState: State<FavoriteViewState>,
    travelAppState: TravelAppState,
    token: String
) {
    when (favoriteViewState.value.favoriteTopBarState) {
        FavoriteTopBarState.DEFAULT -> {
            DefaultAppBar()
        }
        FavoriteTopBarState.REGION -> {
            RegionAppBar(
                favoriteViewModel = favoriteViewModel,
                travelAppState = travelAppState,
                token = token
            )
        }
        FavoriteTopBarState.SPOT -> {
            SpotAppBar(
                favoriteViewModel = favoriteViewModel,
                travelAppState = travelAppState,
                token = token
            )
        }
        FavoriteTopBarState.SPOT_FROM_REGION -> {
            SpotFromRegionAppBar(favoriteViewModel = favoriteViewModel, token = token)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionAppBar(
    token: String,
    travelAppState: TravelAppState,
    favoriteViewModel: FavoriteViewModel
) {
    val region = favoriteViewModel.uiState.collectAsState().value.region
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                travelAppState.bottomBarState.value = true
                favoriteViewModel.getFavRegionList(token)
                favoriteViewModel.getFavSpotList(token)
                favoriteViewModel.updateTopBarState(FavoriteTopBarState.DEFAULT)
                favoriteViewModel.updateContentState(FavoriteContentState.FAVORITE)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
        actions = {
            val favState = remember {
                mutableStateOf(true)
            }
            IconButton(
                onClick = {
                    if (favState.value) {
                        favoriteViewModel.unfavRegion(region!!.rid, token = token)
                        favState.value = false
                    } else {
                        favoriteViewModel.favRegion(region!!.rid, token = token)
                        favState.value = true
                    }
                }
            ) {
                if (favState.value) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotAppBar(
    token: String,
    travelAppState: TravelAppState,
    favoriteViewModel: FavoriteViewModel
) {
    val spot = favoriteViewModel.uiState.collectAsState().value.spot
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                travelAppState.bottomBarState.value = true
                favoriteViewModel.getFavSpotList(token)
                favoriteViewModel.getFavRegionList(token)
                favoriteViewModel.updateTopBarState(FavoriteTopBarState.DEFAULT)
                favoriteViewModel.updateContentState(FavoriteContentState.FAVORITE)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
        actions = {
            val favState = remember {
                mutableStateOf(true)
            }
            IconButton(
                onClick = {
                    if (favState.value) {
                        favoriteViewModel.unfavSpot(spot!!.sid, token = token)
                        favState.value = false
                    } else {
                        favoriteViewModel.favSpot(spot!!.sid, token = token)
                        favState.value = true
                    }
                }
            ) {
                if (favState.value) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotFromRegionAppBar(
    token: String,
    favoriteViewModel: FavoriteViewModel
) {
    val spot = favoriteViewModel.uiState.collectAsState().value.spot
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colors.primary,
        ),
        navigationIcon = {
            IconButton(onClick = {
                favoriteViewModel.updateTopBarState(FavoriteTopBarState.REGION)
                favoriteViewModel.updateContentState(FavoriteContentState.SPOTS)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
        actions = {
            favoriteViewModel.isFavSpot(spot!!.sid, token = token)
            IconButton(
                onClick = {
                    if (favoriteViewModel.query.value) {
                        favoriteViewModel.unfavSpot(spot.sid, token = token)
                        favoriteViewModel.query.value = false
                    } else {
                        favoriteViewModel.favSpot(spot.sid, token = token)
                        favoriteViewModel.query.value = true
                    }
                }
            ) {
                if (favoriteViewModel.query.value) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Favorite Button",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun FavoriteSpotList(
    spotList: List<Spot>,
    favoriteViewModel: FavoriteViewModel,
    favoriteViewState: State<FavoriteViewState>,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .background(MaterialTheme.colors.surface)
    ) {
        item {
            RegionIntro(
                rname = favoriteViewState.value.region!!.rname,
                intro = favoriteViewState.value.region!!.intro,
                view = favoriteViewState.value.region!!.view,
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
                        favoriteViewModel.spotInRegionClicked(item)
                    })
                }
            }
        }
    }
}

