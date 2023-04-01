/**
 * SearchApi.kt
 *
 * This file is api about search function.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Region
import cn.edu.seu.travelapp.model.Spot
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/region")
    suspend fun searchRegion(@Query("keyword") keyword: String): Response<List<Region>>

    @GET("search/spot")
    suspend fun searchSpot(@Query("keyword") keyword: String): Response<List<Spot>>

    companion object {
        var searchApi: SearchApi? = null

        fun getInstance(): SearchApi {
            if (searchApi == null) {
                searchApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SearchApi::class.java)
            }
            return searchApi!!
        }
    }
}