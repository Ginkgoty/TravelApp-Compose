package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.Detail
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DetailApi {
    @GET("spot")
    suspend fun getDetail(@Query("sid") sid: Int): Detail

    companion object {
        var detailApi: DetailApi? = null
        fun getInstance(): DetailApi {
            if (detailApi == null) {
                detailApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(DetailApi::class.java)
            }
            return detailApi!!
        }
    }
}