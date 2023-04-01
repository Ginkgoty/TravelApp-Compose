/**
 * Detail.kt
 *
 * Data class of spot detail.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.model

data class Detail(
    val sid: Int,
    val intro: String,
    val tel: String,
    val consumption: String, // 预计游览时间
    val traffic: String,
    val ticket: String,
    val openness: String, // 开放时间
    val pic1: String,
    val pic2: String,
    val pic3: String,
    val location: String, // 景点位置
    val lat: Double, // 纬度
    val lng: Double // 经度
)
