package cn.edu.seu.travelapp.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.seu.travelapp.api.*
import cn.edu.seu.travelapp.model.*
import cn.edu.seu.travelapp.ui.state.ExploreContentState
import cn.edu.seu.travelapp.ui.state.ExploreTopBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreViewState(
    val exploreTopBarState: ExploreTopBarState = ExploreTopBarState.INIT,
    val searchTextState: String = "",
    val exploreContentState: ExploreContentState = ExploreContentState.REGIONS,
    val region: Region? = null,
    val spot: Spot? = null,
    val food: Food? = null,
    val isRegionFavorite: Boolean = false,
    val isSpotFavorite: Boolean = false
)

class ExploreViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreViewState())
    val uiState: StateFlow<ExploreViewState> = _uiState.asStateFlow()

    fun regionClicked(region: Region, token: String) {
        getSpotList(rid = region.rid)
        getFoodList(rid = region.rid)
        _uiState.update {
            it.copy(
                isRegionFavorite = false
            )
        }
        checkRegionFavoriteStatus(rid = region.rid, token = token)
        _uiState.update {
            it.copy(
                exploreContentState = ExploreContentState.SPOTS,
                region = region,
                exploreTopBarState = ExploreTopBarState.REGION
            )
        }
    }

    fun checkRegionFavoriteStatus(rid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                _uiState.update {
                    it.copy(
                        isRegionFavorite = favoriteApi.checkregion(
                            rid,
                            Token(token)
                        ).result
                    )
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun spotClicked(spot: Spot, token: String) {
        getDetail(sid = spot.sid)
        _uiState.update {
            it.copy(
                isSpotFavorite = false
            )
        }
        checkSpotFavoriteStatus(sid = spot.sid, token = token)
        _uiState.update {
            it.copy(
                exploreContentState = ExploreContentState.DETAIL,
                spot = spot,
                exploreTopBarState = ExploreTopBarState.SPOT
            )
        }
    }

    fun foodClicked(food: Food) {
        _uiState.update {
            it.copy(
                exploreContentState = ExploreContentState.FOOD,
                food = food,
                exploreTopBarState = ExploreTopBarState.FOOD
            )
        }
    }

    fun checkSpotFavoriteStatus(sid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                _uiState.update {
                    it.copy(
                        isSpotFavorite = favoriteApi.checkspot(
                            sid,
                            Token(token)
                        ).result
                    )
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun resultRegionClicked(region: Region, token: String) {
        getSpotList(region.rid)
        getFoodList(region.rid)
        Log.d("rid", region.rid.toString())
        checkRegionFavoriteStatus(rid = region.rid, token = token)
        _uiState.update {
            it.copy(
                exploreContentState = ExploreContentState.SPOTS,
                region = region,
                exploreTopBarState = ExploreTopBarState.RESULT_REGION
            )
        }
    }

    fun resultSpotClicked(spot: Spot, token: String) {
        getDetail(spot.sid)
        checkSpotFavoriteStatus(sid = spot.sid, token = token)
        _uiState.update {
            it.copy(
                exploreContentState = ExploreContentState.DETAIL,
                spot = spot,
                exploreTopBarState = ExploreTopBarState.RESULT_SPOT
            )
        }
    }

    var searchRegionList: List<Region> by mutableStateOf(listOf())
    var searchSpotList: List<Spot> by mutableStateOf(listOf())

    fun searchButtonClicked() {
        Log.d("Search Text", _uiState.value.searchTextState)
        searchRegionList = listOf()
        searchSpotList = listOf()
        viewModelScope.launch {
            val searchApi = SearchApi.getInstance()
            try {
                val regions = searchApi.searchRegion(_uiState.value.searchTextState).body()
                val spots = searchApi.searchSpot(_uiState.value.searchTextState).body()
                updateTopBarState(ExploreTopBarState.RESULT)
                updateContentState(ExploreContentState.SEARCH)
                if (regions != null) {
                    searchRegionList = regions
                }
                if (spots != null) {
                    searchSpotList = spots
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun updateTopBarState(newValue: ExploreTopBarState) {
        _uiState.update {
            it.copy(exploreTopBarState = newValue)
        }
    }

    fun updateSearchTextState(newValue: String) {
        _uiState.update {
            it.copy(searchTextState = newValue)
        }
    }

    fun updateContentState(newValue: ExploreContentState) {
        _uiState.update {
            it.copy(exploreContentState = newValue)
        }
    }

    var tokenDialogState by mutableStateOf(false)

    fun showTokenError(newValue: Boolean) {
        tokenDialogState = newValue
    }

    fun updateRegionFavorite(
        token: String
    ) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                if (uiState.value.isRegionFavorite) {
                    _uiState.update {
                        it.copy(
                            isRegionFavorite = !favoriteApi.unfavregion(
                                rid = uiState.value.region!!.rid,
                                token = Token(token)
                            ).result
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isRegionFavorite = favoriteApi.favregion(
                                rid = uiState.value.region!!.rid,
                                token = Token(token)
                            ).result
                        )
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun updateSpotFavorite(token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                if (uiState.value.isSpotFavorite) {
                    _uiState.update {
                        it.copy(
                            isSpotFavorite = !favoriteApi.unfavspot(
                                sid = uiState.value.spot!!.sid,
                                token = Token(token)
                            ).result
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSpotFavorite = favoriteApi.favspot(
                                sid = uiState.value.spot!!.sid,
                                token = Token(token)
                            ).result
                        )
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    var regionListResponse: List<Region> by mutableStateOf(listOf())
    var recommendationListResponse: List<Region> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")
    fun getRegionList() {
        viewModelScope.launch {
            val mainApi = MainApi.getInstance()
            try {
                val regionList = mainApi.getInfo()
                regionListResponse = regionList
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun getRecommendList() {
        viewModelScope.launch {
            val mainApi = MainApi.getInstance()
            try {
                val regionList = mainApi.getRecommend()
                recommendationListResponse = regionList
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }


    init {
        getRegionList()
        getRecommendList()
    }


    var spotListResponse: List<Spot> by mutableStateOf(listOf())
    var foodListResponse: List<Food> by mutableStateOf(listOf())
    private fun getSpotList(rid: Int) {
        viewModelScope.launch {
            val spotApi = SpotApi.getInstance()
            try {
                val spotList = spotApi.getRegionDetail(rid)
                spotListResponse = spotList
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    val foodShowState = mutableStateOf(false)

    private fun getFoodList(rid: Int) {
        viewModelScope.launch {
            val foodApi = FoodApi.getInstance()
            try {
                val foodList = foodApi.getFood(rid)
                if (foodList != null) {
                    foodListResponse = foodList
                    foodShowState.value = true
                } else {
                    foodShowState.value = false
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
                foodShowState.value = false
            }
        }
    }

    var detailResponse: Detail by mutableStateOf(
        Detail(
            sid = 0,
            intro = "",
            tel = "",
            consumption = "",
            traffic = "",
            ticket = "",
            openness = "",
            pic1 = "",
            pic2 = "",
            pic3 = "",
            location = "",
            lat = 0.0,
            lng = 0.0
        )
    )

    private fun getDetail(sid: Int) {
        viewModelScope.launch {
            val detailApi = DetailApi.getInstance()
            try {
                val detail = detailApi.getDetail(sid)
                detailResponse = detail
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error:", errorMessage)
            }
        }
    }
}


