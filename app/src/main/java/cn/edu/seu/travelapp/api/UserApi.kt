package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.CheckResult
import cn.edu.seu.travelapp.model.Token
import cn.edu.seu.travelapp.model.UserResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @POST("sign_up")
    suspend fun signUp(@Query("uname") uname: String, @Query("pwd") pwd: String): UserResult?

    @POST("sign_in")
    suspend fun signIn(@Query("uname") uname: String, @Query("pwd") pwd: String): UserResult?

    @POST("change/uname")
    suspend fun changeUname(@Query("uname") uname: String, @Body token: Token): Token?

    @POST("change/pwd")
    suspend fun changePwd(@Query("pwd") pwd: String, @Body token: Token): CheckResult?

    @POST("change/upic")
    suspend fun changeUpic(@Query("upic") upic: String, @Body token: Token): CheckResult?

    companion object {
        var userApi: UserApi? = null
        fun getInstance(): UserApi {
            if (userApi == null) {
                userApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(UserApi::class.java)
            }
            return userApi!!
        }
    }
}