package cn.edu.seu.travelapp.ui.state

import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController

enum class TravelAppViewState {
    SPLASH,
    HOME,
    EXPLORE,
    FAVORITE,
    ME
}

data class TravelAppState(
    val navController: NavHostController,
    var travelAppViewState: MutableState<TravelAppViewState>,
    var isLogin: MutableState<Boolean>,
    val topBarState : MutableState<Boolean>,
    val bottomBarState : MutableState<Boolean>,
) {
    fun checkLogin(token: String) {
        isLogin.value = (token != "")
    }

    fun updateTravelAppViewState(newValue: TravelAppViewState) {
        travelAppViewState.value = newValue
    }

}