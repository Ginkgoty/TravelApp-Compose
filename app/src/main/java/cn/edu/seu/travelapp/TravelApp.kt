package cn.edu.seu.travelapp


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.ui.components.NoteEditor
import cn.edu.seu.travelapp.ui.components.PictxtEditor
import cn.edu.seu.travelapp.ui.state.*
import cn.edu.seu.travelapp.ui.view.*
import cn.edu.seu.travelapp.viewmodel.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TravelApp() {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Black,
            darkIcons = false
        )
    }

    val travelAppState = TravelAppState(
        navController = navController,
        travelAppViewState = rememberSaveable {
            mutableStateOf(TravelAppViewState.SPLASH)
        },
        isLogin = rememberSaveable {
            mutableStateOf(false)
        },
        topBarState = rememberSaveable() {
            mutableStateOf(true)
        },
        bottomBarState = rememberSaveable() {
            mutableStateOf(false)
        }
    )

    val dataStore = DataStorage(LocalContext.current)

    val homeViewModel = HomeViewModel()

    val exploreViewModel = ExploreViewModel()
    val exploreViewState = exploreViewModel.uiState.collectAsState()

    val favoriteViewModel = FavoriteViewModel()
    val favoriteViewState = favoriteViewModel.uiState.collectAsState()

    val meViewModel = MeViewModel(travelAppState = travelAppState, dataStore = dataStore)

    val bottomDrawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val selectedItem = remember { mutableStateOf(0) }

    val token = dataStore.getAccessToken.collectAsState(initial = "").value

    Scaffold(
        topBar = {
            Log.d("topbar", travelAppState.topBarState.value.toString())
            if (travelAppState.topBarState.value) {
                when (travelAppState.travelAppViewState.value) {
                    TravelAppViewState.SPLASH -> {

                    }
                    TravelAppViewState.HOME -> {
                        HomeAppBar(homeViewModel = homeViewModel, travelAppState = travelAppState)
                    }
                    TravelAppViewState.EXPLORE -> {
                        ExploreAppBar(
                            exploreViewModel = exploreViewModel,
                            exploreViewState = exploreViewState,
                            dataStore = dataStore,
                            onTextChange = {
                                exploreViewModel.updateSearchTextState(newValue = it)
                            },
                            onCloseClicked = {
                                exploreViewModel.updateTopBarState(newValue = ExploreTopBarState.INIT)
                            },
                            onSearchClicked = {
                                travelAppState.bottomBarState.value = false
                                exploreViewModel.searchButtonClicked()
                            },
                            onSearchTriggered = {
                                exploreViewModel.updateTopBarState(newValue = ExploreTopBarState.SEARCH)
                            },
                            travelAppState = travelAppState
                        )
                    }
                    TravelAppViewState.FAVORITE -> {
                        FavoriteAppBar(
                            favoriteViewModel = favoriteViewModel,
                            favoriteViewState = favoriteViewState,
                            travelAppState = travelAppState,
                            token = token
                        )
                    }
                    TravelAppViewState.ME -> {
                        MeAppBar(meViewModel = meViewModel)
                    }
                }
            }
        },
        bottomBar = {
            Log.d("bottom bar", travelAppState.bottomBarState.value.toString())
            if (travelAppState.bottomBarState.value) {
                BottomAppBar(
                    cutoutShape = CircleShape,
                    elevation = 10.dp
                ) {
                    BottomNav(
                        travelAppState = travelAppState,
                        homeViewModel = homeViewModel,
                        exploreViewModel = exploreViewModel,
                        favoriteViewModel = favoriteViewModel,
                        meViewModel = meViewModel,
                        selectedItem = selectedItem,
                        dataStore = dataStore
                    )
                }
            }
        },
        floatingActionButton = {
            if (travelAppState.bottomBarState.value) {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    onClick = {
                        scope.launch {
                            travelAppState.bottomBarState.value = false
                            bottomDrawerState.open()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "write",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) { PaddingValues ->
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                DrawerContent(
                    drawerState = bottomDrawerState,
                    travelAppState = travelAppState,
                    selectedItem = selectedItem,
                    navController = navController
                )
            },
            gesturesEnabled = false
        ) {
            TravelAppNavHost(
                travelAppState = travelAppState,
                navHostController = travelAppState.navController,
                homeViewModel = homeViewModel,
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                favoriteViewModel = favoriteViewModel,
                meViewModel = meViewModel,
                dataStore = dataStore,
                paddingValues = PaddingValues
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DrawerContent(
    drawerState: BottomDrawerState,
    travelAppState: TravelAppState,
    selectedItem: MutableState<Int>,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        travelAppState.bottomBarState.value = true
        selectedItem.value = travelAppState.travelAppViewState.value.ordinal - 1
        scope.launch {
            drawerState.close()
        }
    }) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp)
    ) {
        Button(
            onClick = {
                travelAppState.topBarState.value = false
                navController.navigate(route = "editor/pictxt")
                scope.launch {
                    drawerState.close()
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = MaterialTheme.colors.primary
            ),
        ) {
            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "")
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "写图文",
                fontSize = 20.sp
            )
        }
        Button(onClick = {
            travelAppState.topBarState.value = false
            navController.navigate(route = "editor/note")
            scope.launch {
                drawerState.close()
            }
        }) {
            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = "",
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "写游记",
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun TravelAppNavHost(
    travelAppState: TravelAppState,
    navHostController: NavHostController,
    homeViewModel: HomeViewModel,
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    favoriteViewModel: FavoriteViewModel,
    meViewModel: MeViewModel,
    dataStore: DataStorage,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navHostController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(navController = navHostController, travelAppState = travelAppState)
            travelAppState.checkLogin(
                token = dataStore.getAccessToken.collectAsState(initial = "").value
            )
        }
        composable(route = "home") {
            HomeView(
                homeViewModel = homeViewModel,
                travelAppState = travelAppState,
                paddingValues = paddingValues,
                token = dataStore.getAccessToken.collectAsState(initial = "").value
            )
        }
        composable(route = "explore") {
            ExploreView(
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                dataStore = dataStore,
                paddingValues = paddingValues,
                travelAppState = travelAppState
            )
        }
        composable(route = "favorite") {
            FavoriteView(
                travelAppState = travelAppState,
                paddingValues = paddingValues,
                favoriteViewModel = favoriteViewModel,
                token = dataStore.getAccessToken.collectAsState(initial = "").value
            )
        }
        composable(route = "me") {
            MeView(
                meViewModel = meViewModel,
                travelAppState = travelAppState,
                paddingValues = paddingValues,
                dataStore = dataStore
            )
        }
        // 游记编辑
        composable(route = "editor/note") {
            NoteEditor(
                travelAppState = travelAppState,
                homeViewModel = homeViewModel,
                dataStore = dataStore
            )
        }
        // 图文编辑
        composable(route = "editor/pictxt") {
            PictxtEditor(
                travelAppState = travelAppState,
                homeViewModel = homeViewModel,
                dataStore = dataStore
            )
        }
    }
}

@Composable
fun BottomNav(
    travelAppState: TravelAppState,
    exploreViewModel: ExploreViewModel,
    homeViewModel: HomeViewModel,
    favoriteViewModel: FavoriteViewModel,
    meViewModel: MeViewModel,
    selectedItem: MutableState<Int>,
    dataStore: DataStorage
) {
    val token = dataStore.getAccessToken.collectAsState(initial = "")
//    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        Icons.Filled.Home,
        Icons.Filled.LocationCity,
        Icons.Filled.Favorite,
        Icons.Filled.Person
    )
    val names = listOf(
        "主页",
        "发现",
        "收藏夹",
        "我"
    )
    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                modifier = when (index) {
                    1 -> Modifier.padding(end = 30.dp)
                    2 -> Modifier.padding(start = 30.dp)
                    else -> {
                        Modifier.padding(0.dp)
                    }
                },
                icon = { Icon(item, contentDescription = null) },
                label = { Text(names[index]) },
                selected = selectedItem.value == index,
                onClick = {
                    selectedItem.value = index
                    when (index) {
                        0 -> {
                            travelAppState.bottomBarState.value = true
                            travelAppState.checkLogin(token = token.value)
                            homeViewModel.getNoteList()
                            travelAppState.navController.navigate(route = "home")
                            travelAppState.updateTravelAppViewState(TravelAppViewState.HOME)
                        }
                        1 -> {
                            travelAppState.bottomBarState.value = true
                            exploreViewModel.getRecommendList()
                            exploreViewModel.getRegionList()
                            travelAppState.navController.navigate(route = "explore")
                            travelAppState.updateTravelAppViewState(TravelAppViewState.EXPLORE)
                        }
                        2 -> {
                            travelAppState.bottomBarState.value = true
                            travelAppState.checkLogin(token = token.value)
                            favoriteViewModel.getFavRegionList(token = token.value)
                            favoriteViewModel.getFavSpotList(token = token.value)
                            travelAppState.navController.navigate(route = "favorite")
                            travelAppState.updateTravelAppViewState(TravelAppViewState.FAVORITE)
                        }
                        3 -> {
                            travelAppState.bottomBarState.value = true
                            travelAppState.checkLogin(token = token.value)
//                            if (travelAppState.isLogin.value) {
//                                meViewModel.updateContentState(MeContentState.ME)
//                            } else {
//                                meViewModel.updateContentState(MeContentState.INIT)
//                            }
                            travelAppState.navController.navigate(route = "me")
                            travelAppState.updateTravelAppViewState(TravelAppViewState.ME)
                        }
                    }
                }
            )
        }
    }
}

