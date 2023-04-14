package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface NoteApi {
    @GET("note")
    suspend fun getNoteList(): List<Note>

    @GET("note/detail")
    suspend fun getNoteDetail(@Query("nid") nid: Int): NoteDetail

    @POST("note/upload")
    suspend fun uploadNote(@Body noteUpload: NoteUpload): CheckResult

    companion object {
        var noteApi: NoteApi? = null
        fun getInstance(): NoteApi {
            if (noteApi == null) {
                noteApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(NoteApi::class.java)
            }
            return noteApi!!
        }
    }
}