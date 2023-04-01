/**
 * FavoriteApi.kt
 *
 * This file is api about favorite function.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Token
import cn.edu.seu.travelapp.model.CheckResult
import cn.edu.seu.travelapp.model.Region
import cn.edu.seu.travelapp.model.Spot
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface FavoriteApi {

    @POST("favspot")
    suspend fun favspot(@Query("sid") sid: Int, @Body token: Token): CheckResult

    @POST("favspot/delete")
    suspend fun unfavspot(@Query("sid") sid: Int, @Body token: Token): CheckResult

    @POST("favspot/check")
    suspend fun checkspot(@Query("sid") sid: Int, @Body token: Token): CheckResult

    @POST("favregion")
    suspend fun favregion(@Query("rid") rid: Int, @Body token: Token): CheckResult

    @POST("favregion/delete")
    suspend fun unfavregion(@Query("rid") rid: Int, @Body token: Token): CheckResult

    @POST("favregion/check")
    suspend fun checkregion(@Query("rid") rid: Int, @Body token: Token): CheckResult

    @POST("favspot/get")
    suspend fun getFavSpots(@Body token: Token): Response<List<Spot>>

    @POST("favregion/get")
    suspend fun getFavRegions(@Body token: Token): Response<List<Region>>

    companion object {
        var favoriteApi: FavoriteApi? = null
        fun getInstance(): FavoriteApi {
            if (favoriteApi == null) {
                favoriteApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(FavoriteApi::class.java)
            }
            return favoriteApi!!
        }
    }
}