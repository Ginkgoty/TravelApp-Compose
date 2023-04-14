package cn.edu.seu.travelapp.model

data class Detail(
    val sid: Int,
    val intro: String,
    val tel: String,
    val consumption: String,
    val traffic: String,
    val ticket: String,
    val openness: String,
    val pic1: String,
    val pic2: String,
    val pic3: String,
    val location: String,
    val lat: Double,
    val lng: Double
)
