package cn.edu.seu.travelapp.ui.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.model.Region
import cn.edu.seu.travelapp.ui.components.*
import cn.edu.seu.travelapp.ui.state.ExploreContentState
import cn.edu.seu.travelapp.ui.state.ExploreTopBarState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
import cn.edu.seu.travelapp.viewmodel.ExploreViewState
import java.time.LocalDateTime

/**
 * @author Li Jiawen
 * @mail 213202838@seu.edu.cn
 *
 * This file contains the entire ui of the Explore view
 *
 * The correspondence between the top bar and the content is as follows:
 *  Content<->TopBar
 *  REGIONS - INIT,SEARCH
 *  SPOTS   - REGION,RESULT_DETAIL
 *  DETAIL  - SPOT,RESULT_DETAIL
 *  SEARCH  - RESULT
 */

@Composable
fun ExploreView(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    travelAppState: TravelAppState,
    dataStore: DataStorage,
    paddingValues: PaddingValues
) {
    when (exploreViewState.value.exploreContentState) {
        ExploreContentState.REGIONS -> {
            if (exploreViewModel.regionListResponse.isEmpty() || exploreViewModel.recommendationListResponse.isEmpty()) {
                BackHandler(enabled = true) {

                }
                RetryPromptView(paddingValues = paddingValues) {
                    exploreViewModel.getRecommendList()
                    exploreViewModel.getRegionList()
                }
            } else {
                Column {
                    BackHandler(enabled = true) {

                    }
                    RegionList(
                        recommendList = exploreViewModel.recommendationListResponse,
                        regionList = exploreViewModel.regionListResponse,
                        exploreViewModel = exploreViewModel,
                        dataStore = dataStore,
                        paddingValues = paddingValues,
                        travelAppState = travelAppState
                    )
                }
            }
        }
        ExploreContentState.SPOTS -> {
            Column() {
                TokenErrorSnackBar(exploreViewModel = exploreViewModel)
                Surface(
                    elevation = 4.dp
                ) {
                    RegionDetail(
                        spotList = exploreViewModel.spotListResponse,
                        exploreViewModel = exploreViewModel,
                        exploreViewState = exploreViewState,
                        dataStore = dataStore,
                        travelAppState = travelAppState,
                        paddingValues = paddingValues
                    )
                }
            }
        }
        ExploreContentState.DETAIL -> {
            Column() {
                BackHandler(enabled = true) {
                    when (exploreViewState.value.exploreTopBarState) {
                        ExploreTopBarState.RESULT_SPOT_REGION -> {
                            exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT_REGION)
                            exploreViewModel.updateContentState(ExploreContentState.SPOTS)
                        }
                        ExploreTopBarState.RESULT_SPOT -> {
                            exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT)
                            exploreViewModel.updateContentState(ExploreContentState.SEARCH)
                        }
                        ExploreTopBarState.SPOT -> {
                            exploreViewModel.updateTopBarState(ExploreTopBarState.REGION)
                            exploreViewModel.updateContentState(ExploreContentState.SPOTS)
                        }
                        else -> {

                        }
                    }
                }
                TokenErrorSnackBar(exploreViewModel = exploreViewModel)
                SpotDetail(
                    sname = exploreViewState.value.spot!!.sname,
                    detail = exploreViewModel.detailResponse,
                    paddingValues = paddingValues
                )
            }
        }
        ExploreContentState.FOOD -> {
            BackHandler() {
                exploreViewModel.updateContentState(ExploreContentState.SPOTS)
                exploreViewModel.updateTopBarState(ExploreTopBarState.REGION)
            }
            FoodDetail(
                food = exploreViewState.value.food!!
            )
        }
        ExploreContentState.SEARCH -> {
            BackHandler(enabled = true) {
                travelAppState.bottomBarState.value = true
                exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
                exploreViewModel.updateContentState(ExploreContentState.REGIONS)
            }
            SearchWidget(
                exploreViewModel = exploreViewModel,
                paddingValues = paddingValues,
                token = dataStore.getAccessToken.collectAsState(
                    initial = ""
                ).value
            )
        }
    }
}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@Composable
fun RegionList(
    recommendList: List<Region>,
    regionList: List<Region>,
    exploreViewModel: ExploreViewModel,
    dataStore: DataStorage,
    travelAppState: TravelAppState,
    paddingValues: PaddingValues
) {
    val month = LocalDateTime.now().monthValue
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = 3),
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
    ) {
        header {
            Text(
                text = "${month}月推荐目的地",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.hggys)),
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        items(recommendList) { item ->
            RegionCard(region = item, onCardClicked = {
                travelAppState.bottomBarState.value = false
                exploreViewModel.regionClicked(region = item, token = token.value)
            })
        }
        header {
            Text(
                text = "热门旅行目的地",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.hggys)),
                modifier = Modifier.padding(start = 5.dp)
            )
        }
        items(regionList) { item ->
            RegionCard(region = item, onCardClicked = {
                travelAppState.bottomBarState.value = false
                exploreViewModel.regionClicked(region = item, token = token.value)
            })
        }
    }
}

@Composable
fun ExploreAppBar(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: DataStorage,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    travelAppState: TravelAppState
) {
    when (exploreViewState.value.exploreTopBarState) {
        ExploreTopBarState.INIT -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered
            )
        }
        ExploreTopBarState.SEARCH -> {
            SearchAppBar(
                text = exploreViewState.value.searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
        ExploreTopBarState.REGION, ExploreTopBarState.SPOT -> {
            DetailAppBar(
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                dataStore = dataStore,
                travelAppState = travelAppState
            )
        }
        ExploreTopBarState.RESULT -> {
            ResultAppBar(exploreViewModel = exploreViewModel, travelAppState = travelAppState)
        }
        ExploreTopBarState.RESULT_REGION, ExploreTopBarState.RESULT_SPOT, ExploreTopBarState.RESULT_SPOT_REGION -> {
            ResultDetailAppBar(
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                dataStore = dataStore
            )
        }
        ExploreTopBarState.FOOD -> {
            FoodAppBar(travelAppState = travelAppState, exploreViewModel = exploreViewModel)
        }

    }
}

/**
 * Top App Bar with Search Icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "行者",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.hggys)),
                fontSize = 32.sp
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colors.primary),
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White
                )
            }
        }
    )
}

/**
 * Top App Bar when you search
 */
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.primary
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = "搜索您想去的地方...",
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            ),
        )
    }
}

/**
 * Top App Bar when view spot/region detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAppBar(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    travelAppState: TravelAppState,
    dataStore: DataStorage,
) {
    val token = dataStore.getAccessToken.collectAsState(initial = "")
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
                when (exploreViewState.value.exploreTopBarState) {
                    ExploreTopBarState.SPOT -> {
                        exploreViewModel.updateTopBarState(ExploreTopBarState.REGION)
                        exploreViewModel.updateContentState(ExploreContentState.SPOTS)
                    }
                    ExploreTopBarState.REGION -> {
                        travelAppState.bottomBarState.value = true
                        exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
                        exploreViewModel.updateContentState(ExploreContentState.REGIONS)
                    }
                    else -> {}
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (token.value == "") {
                        exploreViewModel.showTokenError(true)
                        Log.d("errorbar", exploreViewModel.tokenDialogState.toString())
                    } else {
                        if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.REGION)
                            exploreViewModel.updateRegionFavorite(token = token.value)
                        else
                            exploreViewModel.updateSpotFavorite(token = token.value)
                    }
                }
            ) {
                if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.REGION) {
                    if (exploreViewState.value.isRegionFavorite) {
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
                } else {
                    if (exploreViewState.value.isSpotFavorite) {
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
        }
    )
}

/**
 * Top App Bar After Search
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultAppBar(
    exploreViewModel: ExploreViewModel,
    travelAppState: TravelAppState
) {
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
                exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
                exploreViewModel.updateContentState(ExploreContentState.REGIONS)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
    )
}

/**
 * Top App Bar Of Search Result's Detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultDetailAppBar(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: DataStorage,
) {
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    val exploreContentState = exploreViewModel.uiState.collectAsState()
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
                when (exploreContentState.value.exploreTopBarState) {
                    ExploreTopBarState.RESULT_REGION, ExploreTopBarState.RESULT_SPOT -> {
                        exploreViewModel.updateContentState(ExploreContentState.SEARCH)
                        exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT)
                    }
                    ExploreTopBarState.RESULT_SPOT_REGION -> {
                        exploreViewModel.updateContentState(ExploreContentState.SPOTS)
                        exploreViewModel.updateTopBarState(ExploreTopBarState.RESULT_REGION)
                    }
                    else -> {

                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (token.value == "") {
                        exploreViewModel.showTokenError(true)
                        Log.d("errorbar", exploreViewModel.tokenDialogState.toString())
                    } else {
                        if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.RESULT_REGION)
                            exploreViewModel.updateRegionFavorite(token = token.value)
                        else
                            exploreViewModel.updateSpotFavorite(token = token.value)
                    }
                }
            ) {
                if (exploreViewState.value.exploreTopBarState == ExploreTopBarState.RESULT_REGION) {
                    if (exploreViewState.value.isRegionFavorite) {
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
                } else {
                    if (exploreViewState.value.isSpotFavorite) {
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
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodAppBar(
    travelAppState: TravelAppState,
    exploreViewModel: ExploreViewModel
) {
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
                exploreViewModel.updateTopBarState(ExploreTopBarState.REGION)
                exploreViewModel.updateContentState(ExploreContentState.SPOTS)
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to forward UI",
                    tint = Color.White,
                )
            }
        },
    )
}

@Composable
@Preview
fun DefaultAppBarPreview() {
    DefaultAppBar(onSearchClicked = {})
}

@Composable
@Preview
fun SearchAppBarPreview() {
    SearchAppBar(
        text = "Some random text",
        onTextChange = {},
        onCloseClicked = {},
        onSearchClicked = {}
    )
}

@Composable
@Preview
fun MainContentPreview() {

}