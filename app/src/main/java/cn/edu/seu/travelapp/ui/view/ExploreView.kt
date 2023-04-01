/**
 * ExploreView.kt
 *
 * This file contains the entire ui of the Explore view
 *
 * The correspondence between the top bar and the content is as follows:
 *  Content<->TopBar
 *  REGIONS - INIT,SEARCH
 *  SPOTS   - REGION,RESULT_DETAIL
 *  DETAIL  - SPOT,RESULT_DETAIL
 *  SEARCH  - RESULT
 *
 * @author Li Jiawen
 * @mail nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.model.Region
import cn.edu.seu.travelapp.ui.components.*
import cn.edu.seu.travelapp.ui.state.ExploreContentState
import cn.edu.seu.travelapp.ui.state.ExploreTopBarState
import cn.edu.seu.travelapp.viewmodel.ExploreViewModel
import cn.edu.seu.travelapp.viewmodel.ExploreViewState
import com.autonavi.base.amap.mapcore.AMapNativeGlOverlayLayer
import com.melody.map.gd_compose.GDMap
import com.melody.map.gd_compose.poperties.MapProperties
import com.melody.map.gd_compose.poperties.MapUiSettings


@Composable
fun ExploreView(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: TokenStorage,
    paddingValues: PaddingValues
) {
    when (exploreViewState.value.exploreContentState) {
        ExploreContentState.REGIONS -> {
            if (exploreViewModel.regionListResponse.isEmpty()) {
                BackHandler(enabled = true) {

                }
                RetryPromptView(paddingValues = paddingValues) {
                    exploreViewModel.getRegionList()
                }
            } else {
                Column {
                    BackHandler(enabled = true) {

                    }
                    Text(
                        text = "热门旅行目的地",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(R.font.hggys)),
                        modifier = Modifier.padding(start = 5.dp)
                    )
                    RegionList(
                        regionList = exploreViewModel.regionListResponse,
                        exploreViewModel = exploreViewModel,
                        dataStore = dataStore,
                        paddingValues = paddingValues
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
                    SpotList(
                        spotList = exploreViewModel.spotListResponse,
                        exploreViewModel = exploreViewModel,
                        exploreViewState = exploreViewState,
                        dataStore = dataStore,
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
        ExploreContentState.SEARCH -> {
            BackHandler(enabled = true) {
                exploreViewModel.updateTopBarState(ExploreTopBarState.INIT)
                exploreViewModel.updateContentState(ExploreContentState.REGIONS)
            }
            SearchWidget(exploreViewModel = exploreViewModel, paddingValues = paddingValues)
        }
    }
}


@Composable
fun RegionList(
    regionList: List<Region>,
    exploreViewModel: ExploreViewModel,
    dataStore: TokenStorage,
    paddingValues: PaddingValues
) {
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        items(regionList) { item ->
            RegionCard(region = item, onCardClicked = {
                exploreViewModel.regionClicked(region = item, token = token.value)
            })
        }
    }
}

@Composable
fun ExploreAppBar(
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    dataStore: TokenStorage,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
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
                dataStore = dataStore
            )
        }
        ExploreTopBarState.RESULT -> {
            ResultAppBar(exploreViewModel = exploreViewModel)
        }
        ExploreTopBarState.RESULT_REGION, ExploreTopBarState.RESULT_SPOT, ExploreTopBarState.RESULT_SPOT_REGION -> {
            ResultDetailAppBar(
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                dataStore = dataStore
            )
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
        TextField(modifier = Modifier
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
            ))
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
    dataStore: TokenStorage,
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
    dataStore: TokenStorage,
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