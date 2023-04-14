package cn.edu.seu.travelapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.seu.travelapp.api.DetailApi
import cn.edu.seu.travelapp.api.FavoriteApi
import cn.edu.seu.travelapp.api.PictxtApi
import cn.edu.seu.travelapp.api.SpotApi
import cn.edu.seu.travelapp.model.Detail
import cn.edu.seu.travelapp.model.Region
import cn.edu.seu.travelapp.model.Spot
import cn.edu.seu.travelapp.model.Token
import cn.edu.seu.travelapp.ui.state.FavoriteContentState
import cn.edu.seu.travelapp.ui.state.FavoriteTopBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteViewState(
    val favoriteTopBarState: FavoriteTopBarState = FavoriteTopBarState.DEFAULT,
    val favoriteContentState: FavoriteContentState = FavoriteContentState.FAVORITE,
    val region: Region? = null,
    val spot: Spot? = null,
)

class FavoriteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteViewState())
    val uiState: StateFlow<FavoriteViewState> = _uiState.asStateFlow()

    fun updateTopBarState(newValue: FavoriteTopBarState) {
        _uiState.update {
            it.copy(favoriteTopBarState = newValue)
        }
    }

    fun updateContentState(newValue: FavoriteContentState) {
        _uiState.update {
            it.copy(favoriteContentState = newValue)
        }
    }

    var regionListResponse: List<Region> by mutableStateOf(listOf())
    var spotListResponse: List<Spot> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")

    fun getFavRegionList(token: String) {
        regionListResponse = emptyList()
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                val regionList = favoriteApi.getFavRegions(Token(token)).body()
                if (regionList != null) {
                    regionListResponse = regionList
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun getFavSpotList(token: String) {
        spotListResponse = emptyList()
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                val spotList = favoriteApi.getFavSpots(Token(token)).body()
                if (spotList != null) {
                    spotListResponse = spotList
                }
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun regionClicked(region: Region) {
        getSpotsInRegion(rid = region.rid)
        _uiState.update {
            it.copy(
                favoriteContentState = FavoriteContentState.SPOTS,
                favoriteTopBarState = FavoriteTopBarState.REGION,
                region = region
            )
        }
    }

    fun spotClicked(spot: Spot) {
        getDetail(spot.sid)
        _uiState.update {
            it.copy(
                favoriteTopBarState = FavoriteTopBarState.SPOT,
                favoriteContentState = FavoriteContentState.DETAIL,
                spot = spot
            )
        }
    }

    fun spotInRegionClicked(spot: Spot) {
        getDetail(spot.sid)
        _uiState.update {
            it.copy(
                favoriteTopBarState = FavoriteTopBarState.SPOT_FROM_REGION,
                favoriteContentState = FavoriteContentState.DETAIL,
                spot = spot
            )
        }
    }

    var spotsInRegion: List<Spot> by mutableStateOf(listOf())
    private fun getSpotsInRegion(rid: Int) {
        viewModelScope.launch {
            val spotApi = SpotApi.getInstance()
            try {
                val spotList = spotApi.getRegionDetail(rid)
                spotsInRegion = spotList
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
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

    fun getDetail(sid: Int) {
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

    val opState = mutableStateOf(true)

    fun favSpot(sid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                opState.value = favoriteApi.favspot(sid, Token(token)).result
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun unfavSpot(sid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                opState.value = favoriteApi.unfavspot(sid, Token(token)).result
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun favRegion(rid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                opState.value = favoriteApi.favregion(rid, Token(token)).result
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun unfavRegion(rid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                opState.value = favoriteApi.unfavregion(rid, Token(token)).result
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    val query = mutableStateOf(false)
    fun isFavSpot(sid: Int, token: String) {
        viewModelScope.launch {
            val favoriteApi = FavoriteApi.getInstance()
            try {
                query.value = favoriteApi.checkspot(sid, Token(token)).result
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }


}