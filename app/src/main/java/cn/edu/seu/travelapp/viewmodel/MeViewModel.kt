package cn.edu.seu.travelapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.seu.travelapp.api.ApiConstants
import cn.edu.seu.travelapp.api.UploadApi
import cn.edu.seu.travelapp.api.UserApi
import cn.edu.seu.travelapp.data.DataStorage
import cn.edu.seu.travelapp.model.Token
import cn.edu.seu.travelapp.ui.state.MeContentState
import cn.edu.seu.travelapp.ui.state.TravelAppState
import cn.edu.seu.travelapp.util.HashUtil
import cn.edu.seu.travelapp.util.MessageDigestAlgorithm
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import kotlin.random.Random

data class MeViewState(
    var meContentState: MeContentState = MeContentState.INIT,
    val signUpStatus: Int = 0, // 0 - Not sign up, 1 - Sign up success, 2 - Sign up failed, 3 - Input error.
    val signInStatus: Int = 0, // 0 - Not sign in, 1 - Sign in success, 2 - Sign in failed
    val changePwdStatus: Int = 0, // 0 - Not change, 1 - change success, 2 -change failed, 3 - Input error
    val changeUnameStatus: Int = 0, // 0 - Not change, 1 - change success, 2 -change failed, 3 - Input error
)

class MeViewModel(travelAppState: TravelAppState, dataStore: DataStorage) : ViewModel() {
    private val _uiState = MutableStateFlow(MeViewState())
    val uiState: StateFlow<MeViewState> = _uiState.asStateFlow()

    private val appDataStore = dataStore
    private val appState = travelAppState

//    init {
//        viewModelScope.launch {
//            dataStore.clearToken()
//        }
//    }

    fun updateContentState(newValue: MeContentState) {
        Log.d("update", newValue.toString())
        _uiState.update {
            it.copy(
                meContentState = newValue
            )
        }

    }

    var errorMessage: String by mutableStateOf("")
    fun signUp(uname: String, pwd: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            try {
                opState.value = true
                val t = userApi.signUp(uname, pwd)
                if (t != null) {
                    appDataStore.saveToken(token = t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    appDataStore.saveCurrentUpic(upic = t.upic)
                    updateSignUpStatus(newValue = 1)
                    appState.isLogin.value = true
                    updateContentState(newValue = MeContentState.ME)
                    appState.checkLogin(t.token)
                }
            } catch (e: Exception) {
                updateSignUpStatus(2)
                snackBarShowingState = true
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun signIn(uname: String, pwd: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            try {
                opState.value = true
                val t = userApi.signIn(uname, pwd)
                if (t != null) {
                    appDataStore.saveToken(token = t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    appDataStore.saveCurrentUpic(upic = t.upic)
                    updateSignInStatus(newValue = 1)
                    appState.isLogin.value = true
                    updateContentState(newValue = MeContentState.ME)
                    appState.checkLogin(t.token)
                }
            } catch (e: Exception) {
                updateSignInStatus(newValue = 2)
                snackBarShowingState = true
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun changePwd(pwd: String, token: String) {
        viewModelScope.launch {
            val userApi = UserApi.getInstance()
            val currentToken = Token(token)
            try {
                opState.value = true
                val t = userApi.changePwd(pwd = pwd, token = currentToken)
                Log.d("Change Pwd", t.toString())
                if (t != null && t.result) {
                    snackBarShowingState = true
                    updateChangePwdStatus(newValue = 1)
                    updateContentState(newValue = MeContentState.ME)
                }
            } catch (e: Exception) {
                snackBarShowingState = true
                updateChangePwdStatus(newValue = 2)
            }
        }
    }

    fun changeUname(uname: String, token: String) {
        viewModelScope.launch {
            opState.value = true
            val userApi = UserApi.getInstance()
            val currentToken = Token(token)
            try {
                val t = userApi.changeUname(uname = uname, token = currentToken)
                if (t != null) {
                    appDataStore.saveToken(t.token)
                    appDataStore.saveCurrentUname(uname = uname)
                    snackBarShowingState = true
                    updateChangeUnameStatus(newValue = 1)
                    updateContentState(newValue = MeContentState.ME)
                }
            } catch (e: Exception) {
                snackBarShowingState = true
                updateChangeUnameStatus(newValue = 2)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            appDataStore.clearToken()
        }
        updateContentState(MeContentState.INIT)
    }

    var opState = mutableStateOf(false)
    var snackBarShowingState by mutableStateOf(false)

    fun updateSignUpStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                signUpStatus = newValue
            )
        }
    }

    fun updateSignInStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                signInStatus = newValue
            )
        }
    }

    fun updateChangePwdStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                changePwdStatus = newValue
            )
        }
    }

    fun updateChangeUnameStatus(newValue: Int) {
        _uiState.update {
            it.copy(
                changeUnameStatus = newValue
            )
        }
    }

    var upicUri by mutableStateOf("")
    val uploadStatus = mutableStateOf(false)

    fun changeUpic(context: Context, token: String) {
        viewModelScope.launch {
            opState.value = true
            val temp = uriToFile(context = context, uri = Uri.parse(upicUri))
            val compression =
                Compressor.compress(
                    context = context,
                    imageFile = temp!!
                ) {
                    resolution(width = 300, height = 300)
                    quality(80)
                    format(Bitmap.CompressFormat.JPEG)
                    size(maxFileSize = 500 * 1024)
                }
            val md5 = HashUtil.getCheckSumFromFile(
                digest = MessageDigest.getInstance(MessageDigestAlgorithm.MD5),
                file = compression
            )
            val url = "${ApiConstants.IMG_BASE_URL}$md5.jpg"

            val uploadApi = UploadApi.getInstance()
            val userApi = UserApi.getInstance()
            try {
                uploadStatus.value = uploadApi.uploadSingleImage(
                    type = "jpg",
                    md5 = md5,
                    image = MultipartBody.Part.createFormData(
                        name = "image",
                        filename = compression.name,
                        body = compression.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                ).result
                uploadStatus.value = userApi.changeUpic(
                    upic = url,
                    token = Token(token)
                )!!.result
                if (uploadStatus.value) {
                    appDataStore.saveCurrentUpic(url)
                    updateContentState(MeContentState.ME)
                }
            } catch (e: Exception) {
                uploadStatus.value = false
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val file: File = File(
            context.cacheDir,
            "${System.currentTimeMillis()}${
                Random.nextInt(
                    0,
                    9999
                )
            }.${context.contentResolver.getType(uri)}".replace("image/", "")
        )
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    if (inputStream != null) {
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                    }
                    output.flush()
                    return file
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return null
    }

}