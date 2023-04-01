/**
 * MainApi.kt
 *
 * This file is api about user functions, such as sign-up/sign-in.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.CheckResult
import cn.edu.seu.travelapp.model.Token
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @POST("sign_up")
    suspend fun signUp(@Query("uname") uname: String, @Query("pwd") pwd: String): Token?

    @POST("sign_in")
    suspend fun signIn(@Query("uname") uname: String, @Query("pwd") pwd: String): Token?

    @POST("change/uname")
    suspend fun changeUname(@Query("uname") uname: String, @Body token: Token): Token?

    @POST("change/pwd")
    suspend fun changePwd(@Query("uname") pwd: String, @Body token: Token): CheckResult?

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