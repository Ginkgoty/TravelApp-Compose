/**
 * Note.kt
 *
 * Data class of note
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.model

data class Note(
    val nid: Int,
    val img: String,
    val title: String,
    val intro: String,
    val rname: String
)
