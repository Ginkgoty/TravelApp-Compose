package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PictxtApi {

    @POST("pictxt")
    suspend fun getPictxtList(@Body token: Token): List<Pictxt>

    @POST("pictxt/upload")
    suspend fun uploadPictxt(@Body pictxtUpload: PictxtUpload): CheckResult

    @POST("pictxt/fav")
    suspend fun favPictxt(@Query("ptid") ptid: Int, @Body token: Token): CheckResult

    @POST("pictxt/unfav")
    suspend fun unfavPictxt(@Query("ptid") ptid: Int, @Body token: Token): CheckResult

    @GET("ptcomment")
    suspend fun fetchComments(@Query("ptid") ptid: Int): List<PtComment>

    @POST("ptcomment")
    suspend fun comment(@Body ptCommentUpload: PtCommentUpload) : CheckResult

    companion object {
        var pictxtApi: PictxtApi? = null
        fun getInstance(): PictxtApi {
            if (pictxtApi == null) {
                pictxtApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(PictxtApi::class.java)
            }
            return pictxtApi!!
        }
    }


}