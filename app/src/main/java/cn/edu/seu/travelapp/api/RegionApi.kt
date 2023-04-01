/**
 * RegionApi.kt
 *
 * This file is api about get info of region.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Region
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RegionApi {
    @GET("region/info")
    suspend fun getRegion(@Query("rid") rid: Int): Region

    companion object {
        var regionApi: RegionApi? = null
        fun getInstance(): RegionApi {
            if (regionApi == null) {
                regionApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RegionApi::class.java)
            }
            return regionApi!!
        }
    }
}