package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.api.ApiConstants.BASE_URL
import cn.edu.seu.travelapp.model.Region
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface MainApi {
    @GET("main")
    suspend fun getInfo(): List<Region>

    @GET("recommend")
    suspend fun getRecommend(): List<Region>

    companion object {
        var mainApi: MainApi? = null
        fun getInstance(): MainApi {
            if (mainApi == null) {
                mainApi = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(MainApi::class.java)
            }
            return mainApi!!
        }
    }
}