package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Spot
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotApi {
    @GET("region")
    suspend fun getRegionDetail(@Query("rid") rid: Int): List<Spot>

    companion object {
        var spotApi: SpotApi? = null
        fun getInstance(): SpotApi {
            if (spotApi == null) {
                spotApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(SpotApi::class.java)
            }
            return spotApi!!
        }
    }

}