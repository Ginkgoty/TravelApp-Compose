/**
 * HomeViewModel.kt
 *
 * This file is ViewModel of home view
 *
 * @author Li Jiawen
 * @mail nmjbh@qq.com
 */
package cn.edu.seu.travelapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.*
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.seu.travelapp.api.ApiConstants.IMG_BASE_URL
import cn.edu.seu.travelapp.api.NoteApi
import cn.edu.seu.travelapp.api.UploadApi
import cn.edu.seu.travelapp.model.*
import cn.edu.seu.travelapp.ui.state.HomeViewContentState
import cn.edu.seu.travelapp.util.HashUtil
import cn.edu.seu.travelapp.util.MessageDigestAlgorithm
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import kotlin.random.Random


data class HomeViewState(
    val homeViewContentState: HomeViewContentState = HomeViewContentState.NOTE_LIST,
    val note: Note?,
    var drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    var lazyListState: LazyListState = LazyListState()
)

class HomeViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeViewState(note = null))
    val uiState: StateFlow<HomeViewState> = _uiState.asStateFlow()


    var noteList: List<Note> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")

    fun getNoteList() {
        viewModelScope.launch {
            val noteApi = NoteApi.getInstance()
            try {
                val noteListResponse = noteApi.getNoteList()
                noteList = noteListResponse
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    init {
        getNoteList()
    }


    var noteDetial: NoteDetail by mutableStateOf(NoteDetail(0, "", "", "", "", arrayListOf()))
    var catalogue: List<Pair<Int, String>> by mutableStateOf(listOf())

    fun getNoteDetail() {
        viewModelScope.launch {
            val noteApi = NoteApi.getInstance()
            try {
                val noteDetailResponse = noteApi.getNoteDetail(uiState.value.note!!.nid)
                noteDetial = noteDetailResponse
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }
        }
    }

    fun indexCatalogue() {
        val tempList: ArrayList<Pair<Int, String>> = arrayListOf()
        noteDetial.content.forEachIndexed { index, noteItem ->
            if (noteItem.kind == 0) {
                tempList.add(Pair(index, noteItem.content))
            }
        }
        catalogue = tempList
    }

    fun updateContentState(newValue: HomeViewContentState) {
        _uiState.update {
            it.copy(
                homeViewContentState = newValue
            )
        }
    }

    fun setCurrentNote(newValue: Note) {
        _uiState.update {
            it.copy(
                note = newValue
            )
        }
    }

    // Content Editor Area
    var editorBackground by mutableStateOf("")
    var editorTitle by mutableStateOf("")
    var editorRname by mutableStateOf("")
    val editorNoteContentList = mutableStateListOf<Pair<MutableState<Int>, MutableState<String>>>()

    fun myNoteContentListInit() {
        editorNoteContentList.add(Pair(mutableStateOf(1), mutableStateOf("")))
    }

    fun dealWithImgList(imgList: List<Uri>) {
        imgList.forEach {
            editorNoteContentList.add(Pair(mutableStateOf(2), mutableStateOf(it.toString())))
        }
        // Remove empty text input area
        editorNoteContentList.removeIf {
            it.first.value == 1 && it.second.value == ""
        }
        // Add a new text input area
        editorNoteContentList.add(Pair(mutableStateOf(1), mutableStateOf("")))
    }

    fun deleteCurrentImage(content: String) {
        editorNoteContentList.removeIf {
            it.first.value == 2 && it.second.value == content
        }
    }

    fun dealWithSectionTitle(st: String) {
        // Insert a section title
        editorNoteContentList.add(Pair(mutableStateOf(0), mutableStateOf(st)))
        // Remove empty text input area
        editorNoteContentList.removeIf {
            it.first.value == 1 && it.second.value == ""
        }
        // Add a new text input area
        editorNoteContentList.add(Pair(mutableStateOf(1), mutableStateOf("")))
    }

    fun updateSectionTitle(index: Int, st: String) {
        editorNoteContentList[index].second.value = st
    }

    fun deleteSectionTitle(index: Int) {
        editorNoteContentList.removeAt(index)
    }

    fun cleanUnusedItems() {
        val tempList = arrayListOf<Int>()
        editorNoteContentList.forEachIndexed { index, pair ->
            if (pair.first.value == 1) {
                tempList.add(index)
            }
        }
        tempList.dropLast(1).forEach {
            if (editorNoteContentList[it].second.value == "") {
                editorNoteContentList.removeAt(it)
            }
        }
    }

    fun clearNoteEditor() {
        editorBackground = ""
        editorTitle = ""
        editorRname = ""
        editorNoteContentList.clear()
    }


    val uploadNoteStatus = mutableStateOf(false)
    val showUploadSnackBar = mutableStateOf(false)
    fun uploadNote(token: String, context: Context) {
        // Deal with background
        viewModelScope.launch {
            val temp_file = uriToFile(context = context, uri = Uri.parse(editorBackground))!!
            val bg_file = Compressor.compress(
                context = context,
                imageFile = temp_file
            ) {
                resolution(width = 1920, height = 1080)
                quality(80)
                format(Bitmap.CompressFormat.JPEG)
                size(maxFileSize = 2_097_152)
            }
            val bg_md5 = HashUtil.getCheckSumFromFile(
                digest = MessageDigest.getInstance(MessageDigestAlgorithm.MD5),
                file = bg_file
            )
            val bg = "$IMG_BASE_URL$bg_md5.jpg"
            val uploadApi = UploadApi.getInstance()
            try {
                val result = uploadApi.uploadSingleImage(
                    type = "jpg",
                    md5 = bg_md5,
                    image = MultipartBody.Part.createFormData(
                        name = "image",
                        filename = bg_file.name,
                        body = bg_file.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                )
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
            }


            val elist: MutableList<NoteItem> = mutableListOf()
            editorNoteContentList.forEach { item ->
                if (item.first.value == 0 || item.first.value == 1) {
                    elist.add(NoteItem(item.first.value, item.second.value))
                } else {
                    val temp_file = uriToFile(context = context, uri = Uri.parse(item.second.value))
                    val i_file =
                        Compressor.compress(
                            context = context,
                            imageFile = temp_file!!
                        ) {
                            resolution(width = 1920, height = 1080)
                            quality(80)
                            format(Bitmap.CompressFormat.JPEG)
                            size(maxFileSize = 2_097_152)
                        }
                    val i_md5 = HashUtil.getCheckSumFromFile(
                        digest = MessageDigest.getInstance(MessageDigestAlgorithm.MD5),
                        file = i_file
                    )
                    elist.add(NoteItem(item.first.value, "$IMG_BASE_URL$i_md5.jpg"))
                    try {
                        val result = uploadApi.uploadSingleImage(
                            type = "jpg",
                            md5 = i_md5,
                            image = MultipartBody.Part.createFormData(
                                name = "image",
                                filename = i_file.name,
                                body = i_file.asRequestBody("image/*".toMediaTypeOrNull())
                            )
                        )
                    } catch (e: Exception) {
                        errorMessage = e.message.toString()
                        Log.d("Error", errorMessage)
                    }
                }
                elist.forEach() {
                    Log.d("elist", it.toString())
                }
            }

            val noteUpload = NoteUpload(
                token = token,
                detail = NoteUploadDetail(
                    background = bg,
                    title = editorTitle,
                    rname = editorRname,
                    content = elist
                )
            )

            val noteApi = NoteApi.getInstance()
            try {
                uploadNoteStatus.value =
                    noteApi.uploadNote(noteUpload = noteUpload).result
                showUploadSnackBar.value = true
            } catch (e: Exception) {
                errorMessage = e.message.toString()
                Log.d("Error", errorMessage)
                uploadNoteStatus.value = false
                showUploadSnackBar.value = true
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

    fun checkBeforeUpload(): Boolean {
        return editorBackground != "" && editorRname != "" && editorTitle != ""
    }
}