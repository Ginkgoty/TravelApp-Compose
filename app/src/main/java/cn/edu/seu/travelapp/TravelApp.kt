/**
 * TravelApp.kt
 *
 * UI of TravelApp, including navigation.
 *
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 */
package cn.edu.seu.travelapp


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.edu.seu.travelapp.data.TokenStorage
import cn.edu.seu.travelapp.ui.state.*
import cn.edu.seu.travelapp.ui.view.*
import cn.edu.seu.travelapp.viewmodel.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController


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

    val dataStore = TokenStorage(LocalContext.current)

    val homeViewModel = HomeViewModel()
    val homeViewState = homeViewModel.uiState.collectAsState()

    val exploreViewModel = ExploreViewModel()
    val exploreViewState = exploreViewModel.uiState.collectAsState()

    val favoriteViewModel = FavoriteViewModel()
    val favoriteViewState = favoriteViewModel.uiState.collectAsState()

    val meViewModel = MeViewModel(travelAppState = travelAppState, dataStore = dataStore)

    Scaffold(
        topBar = {
            when (travelAppState.travelAppViewState.value) {
                TravelAppViewState.SPLASH -> {

                }
                TravelAppViewState.HOME -> {
                    HomeAppBar(homeViewModel = homeViewModel)
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
                            exploreViewModel.searchButtonClicked()
                        },
                        onSearchTriggered = {
                            exploreViewModel.updateTopBarState(newValue = ExploreTopBarState.SEARCH)
                        }
                    )
                }
                TravelAppViewState.FAVORITE -> {
                    FavoriteAppBar(
                        favoriteViewModel = favoriteViewModel,
                        favoriteViewState = favoriteViewState
                    )
                }
                TravelAppViewState.ME -> {
                    MeAppBar(meViewModel = meViewModel)
                }
            }
        },
        bottomBar = {
            if (travelAppState.bottomBarState.value) {
                BottomAppBar(
                    travelAppState = travelAppState,
                    homeViewModel = homeViewModel,
                    exploreViewModel = exploreViewModel,
                    favoriteViewModel = favoriteViewModel,
                    meViewModel = meViewModel,
                    dataStore = dataStore
                )
            }
        }
    ) { PaddingValues ->
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

@Composable
fun TravelAppNavHost(
    travelAppState: TravelAppState,
    navHostController: NavHostController,
    homeViewModel: HomeViewModel,
    exploreViewModel: ExploreViewModel,
    exploreViewState: State<ExploreViewState>,
    favoriteViewModel: FavoriteViewModel,
    meViewModel: MeViewModel,
    dataStore: TokenStorage,
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
                datastore = dataStore,
                paddingValues = paddingValues
            )
        }
        composable(route = "explore") {
            ExploreView(
                exploreViewModel = exploreViewModel,
                exploreViewState = exploreViewState,
                dataStore = dataStore,
                paddingValues = paddingValues
            )
        }
        composable(route = "favorite") {
            FavoriteView(
                travelAppState = travelAppState,
                paddingValues = paddingValues,
                favoriteViewModel = favoriteViewModel
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
    }
}

@Composable
fun BottomAppBar(
    travelAppState: TravelAppState,
    exploreViewModel: ExploreViewModel,
    homeViewModel: HomeViewModel,
    favoriteViewModel: FavoriteViewModel,
    meViewModel: MeViewModel,
    dataStore: TokenStorage
) {
    val token = dataStore.getAccessToken.collectAsState(initial = "")
    var selectedItem by remember { mutableStateOf(0) }
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
                icon = { Icon(item, contentDescription = null) },
                label = { Text(names[index]) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
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
                            if (travelAppState.isLogin.value) {
                                meViewModel.updateContentState(MeContentState.ME)
                            } else {
                                meViewModel.updateContentState(MeContentState.INIT)
                            }
                            travelAppState.navController.navigate(route = "me")
                            travelAppState.updateTravelAppViewState(TravelAppViewState.ME)
                        }
                    }
                }
            )
        }
    }
}

