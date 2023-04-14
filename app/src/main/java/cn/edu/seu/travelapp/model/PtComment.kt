package cn.edu.seu.travelapp.model

data class PtComment(
    val cid: Int = 0,
    val uname: String = "",
    val upic: String = "",
    val comment: String = "",
    val belong: Int = 0,
    val datetime: String = "",
    val replies: List<PtComment> = listOf()
)
