package dto

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    var likes:Int,
    var share:Int,
    var views:Int
)