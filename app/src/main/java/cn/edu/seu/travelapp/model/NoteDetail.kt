package cn.edu.seu.travelapp.model

/**
 * Note Item:
 *  0 - Chapter title
 *  1 - Text content
 *  2 - Image content
 */
data class NoteItem(
    val kind: Int,
    var content: String
)


data class NoteDetail(
    val nid: Int,
    val background: String,
    val uname: String,
    val upic: String,
    val time: String,
    val title: String,
    val content: ArrayList<NoteItem>
)
