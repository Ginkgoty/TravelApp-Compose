package cn.edu.seu.travelapp.model

data class NoteUploadDetail(
    val background: String,
    val title: String,
    val rname: String,
    val content: List<NoteItem>
)


data class NoteUpload(
    val token: String,
    val detail: NoteUploadDetail
)
