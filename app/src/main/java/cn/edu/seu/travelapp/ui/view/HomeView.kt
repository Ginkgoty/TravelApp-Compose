package cn.edu.seu.travelapp.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.ui.components.*
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState,
    paddingValues: PaddingValues,
    token: String
) {
    val homeViewState = homeViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    when (homeViewState.value.homeViewContentState) {
        HomeViewContentState.NOTE_LIST -> {
            BackHandler(enabled = true) {

            }
            if (homeViewModel.noteList.isEmpty()) {
                BackHandler(enabled = true) {

                }
                RetryPromptView(paddingValues = paddingValues) {
                    homeViewModel.getNoteList()
                }
            } else {
                NoteList(
                    noteList = homeViewModel.noteList,
                    homeViewModel = homeViewModel,
                    paddingValues = paddingValues,
                    token = token
                )
            }
        }
        HomeViewContentState.NOTE_DETAIL -> {
            BackHandler(enabled = true) {
                homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                travelAppState.bottomBarState.value = true
            }
            // close bar temporarily
            travelAppState.bottomBarState.value = false
            if (homeViewState.value.drawerState.isOpen) {
                ModalDrawer(
                    drawerState = homeViewState.value.drawerState,
                    drawerContent = {
                        Column(
                            modifier = Modifier.verticalScroll(state = scrollState, enabled = true)
                        ) {
                            Text(
                                text = "目录",
                                textAlign = TextAlign.Left,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            homeViewModel.catalogue.fastForEachIndexed { index, it ->
                                Row(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .clickable {
                                            scope.launch {
                                                homeViewState.value.lazyListState.animateScrollToItem(
                                                    index = it.first
                                                )
                                            }
                                        }
                                ) {
                                    Text(
                                        text = if ((index + 1).toString().length == 1) {
                                            "0" + (index + 1).toString() + "/ "
                                        } else {
                                            (index + 1).toString() + "/ "
                                        },
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Left,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = it.second,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Left
                                    )
                                }
                                Spacer(modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                ) {
                    NoteDetial(
                        noteDetail = homeViewModel.noteDetial,
                        state = homeViewState.value.lazyListState,
                        paddingValues = paddingValues
                    )
                }
            } else {
                NoteDetial(
                    noteDetail = homeViewModel.noteDetial,
                    state = homeViewState.value.lazyListState,
                    paddingValues = paddingValues
                )
            }
        }
        HomeViewContentState.PICTXT_LIST -> {
            PictxtList(
                token = token,
                homeViewModel = homeViewModel,
                travelAppState = travelAppState,
                paddingValues = paddingValues
            )
        }
        HomeViewContentState.PICTXT_DETAIL -> {
            PictxtDetail(
                token = token,
                homeViewModel = homeViewModel,
                travelAppState = travelAppState
            )
        }
        else -> {}
    }
}


@Composable
fun HomeAppBar(
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState
) {
    val homeViewState = homeViewModel.uiState.collectAsState()
    when (homeViewState.value.homeViewContentState) {
        HomeViewContentState.NOTE_LIST, HomeViewContentState.PICTXT_LIST -> {
            NoteAppBar()
        }
        HomeViewContentState.NOTE_DETAIL -> {
            NoteDetailAppBar(
                homeViewModel = homeViewModel,
                drawerState = homeViewState.value.drawerState,
                travelAppState = travelAppState
            )
        }
        HomeViewContentState.PICTXT_DETAIL -> {}
        else -> {}
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppBar(
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
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailAppBar(
    homeViewModel: HomeViewModel,
    drawerState: DrawerState,
    travelAppState: TravelAppState
) {
    val scope = rememberCoroutineScope()
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
                homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                travelAppState.bottomBarState.value = true
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
                    scope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        } else {
                            homeViewModel.indexCatalogue()
                            drawerState.open()
                        }
                    }
                },
            ) {
                Icon(imageVector = Icons.Filled.Menu, null, tint = Color.White)
            }
        }
    )
}