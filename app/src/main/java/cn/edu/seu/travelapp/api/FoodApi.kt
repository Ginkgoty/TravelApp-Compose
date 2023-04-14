package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Food
import cn.edu.seu.travelapp.model.Region
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApi {
    @GET("food")
    suspend fun getFood(@Query("rid") rid: Int): List<Food>?

    companion object {
        var foodApi: FoodApi? = null
        fun getInstance(): FoodApi {
            if (foodApi == null) {
                foodApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(FoodApi::class.java)
            }
            return foodApi!!
        }
    }
}