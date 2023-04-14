package cn.edu.seu.travelapp.model

data class PictxtUpload(
    var token: String = "",
    var title: String = "",
    var text: String = "",
    val imglist: MutableList<String> = mutableListOf()
)
