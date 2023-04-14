package cn.edu.seu.travelapp.api

import cn.edu.seu.travelapp.model.CheckResult
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {

    @Multipart
    @POST("upload")
    suspend fun uploadSingleImage(
        @Part("type") type: String,
        @Part("md5") md5: String,
        @Part image: MultipartBody.Part
    ): CheckResult

    companion object {
        var uploadApi: UploadApi? = null
        fun getInstance(): UploadApi {
            if (uploadApi == null) {
                uploadApi = Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(UploadApi::class.java)
            }
            return uploadApi!!
        }
    }
}