package ru.darek.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.darek.nmedia.R
import ru.darek.nmedia.databinding.CardPostBinding
import ru.darek.nmedia.dto.Post
//import ru.darek.nmedia.util

typealias OnLikeListener = (post: Post) -> Unit

class PostsAdapter(private val onLikeListener: OnLikeListener) : RecyclerView.Adapter<PostViewHolder>() {
    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = list.size
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = getStrCnt(post.likes)
            shareCount.text = getStrCnt(post.share)
            viewsCount.text = getStrCnt(post.views)
            like.setImageResource(
                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
            )
          /*  if (post.likedByMe) {
                like.setImageResource(R.drawable.ic_liked_24)
            } */
            like.setOnClickListener{
                onLikeListener(post)
            }
        }
    }
}
fun getStrCnt(inCnt:Int):String{
    if (inCnt >= 1000000) return String.format("%.2f M",(inCnt/1000000).toFloat())
    if (inCnt >= 10000) return String.format("%d K",inCnt/1000)
    if (inCnt >= 1000) return String.format("%.1f K",(inCnt/1000).toFloat())
    return inCnt.toString()
}
