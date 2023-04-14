package cn.edu.seu.travelapp.model

data class Pictxt(
    val ptid: Int = 0,
    val uname: String = "",
    val upic: String = "",
    val cover: String = "",
    val imagelist: List<String>,
    val title: String = "",
    val text: String = "",
    val datetime: String = "",
    var favcount: Int = 0,
    var isfavorite: Boolean = false
)
