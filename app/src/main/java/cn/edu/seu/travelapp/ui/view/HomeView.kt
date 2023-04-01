/**
 * HomeView.kt
 *
 * This file contains the entire ui of the Home view
 *
 * @author Li Jiawen
 * @mail nmjbh@qq.com
 */
package cn.edu.seu.travelapp.ui.view

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.dataStore
import cn.edu.seu.travelapp.R
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.model.NoteItem
import cn.edu.seu.travelapp.ui.components.NoteDetial
import cn.edu.seu.travelapp.ui.components.NoteEditor
import cn.edu.seu.travelapp.ui.components.NoteList
import cn.edu.seu.travelapp.ui.components.RetryPromptView
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.viewmodel.HomeViewModel
import cn.edu.seu.travelapp.viewmodel.HomeViewState
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    homeViewModel: HomeViewModel,
    travelAppState: TravelAppState,
    datastore: TokenStorage,
    paddingValues: PaddingValues
) {
    val homeViewState = homeViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    travelAppState.bottomBarState.value = true

    when (homeViewState.value.homeViewContentState) {
        HomeViewContentState.NOTE_LIST -> {
            travelAppState.bottomBarState.value = true
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
                    paddingValues = paddingValues
                )
            }
        }
        HomeViewContentState.NOTE_DETAIL -> {
            BackHandler(enabled = true) {
                homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
            }
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
        HomeViewContentState.NOTE_EDITOR -> {
            travelAppState.bottomBarState.value = false;
            if (travelAppState.isLogin.value) {
                NoteEditor(
                    dataStore = datastore,
                    homeViewModel = homeViewModel
                )
            } else {
                AlertDialog(
                    title = {
                        Text(text = "Hint")
                    },
                    text = {
                        Text(text = "您还未登录，请先登录！")
                    },
                    onDismissRequest = {
                        homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                        travelAppState.bottomBarState.value = true
                    },
                    buttons = {
                        Row(
                            modifier = Modifier.padding(all = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    homeViewModel.updateContentState(HomeViewContentState.NOTE_LIST)
                                    travelAppState.bottomBarState.value = true
                                }
                            ) {
                                Text("好")
                            }
                        }
                    }
                )
            }

        }
    }
}


@Composable
fun HomeAppBar(
    homeViewModel: HomeViewModel
) {
    val homeViewState = homeViewModel.uiState.collectAsState()
    when (homeViewState.value.homeViewContentState) {
        HomeViewContentState.NOTE_LIST -> {
            NoteAppBar(
                homeViewModel = homeViewModel
            )
        }
        HomeViewContentState.NOTE_DETAIL -> {
            NoteDetailAppBar(
                homeViewModel = homeViewModel,
                drawerState = homeViewState.value.drawerState
            )
        }
        HomeViewContentState.NOTE_EDITOR -> {

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppBar(
    homeViewModel: HomeViewModel
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
        actions = {
            IconButton(
                onClick = {
                    homeViewModel.updateContentState(
                        HomeViewContentState.NOTE_EDITOR
                    )
                    homeViewModel.myNoteContentListInit()
                }) {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = "Icon",
                    tint = Color.White,
                )
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailAppBar(
    homeViewModel: HomeViewModel,
    drawerState: DrawerState,
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