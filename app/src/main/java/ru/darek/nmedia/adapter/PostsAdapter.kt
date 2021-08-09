package ru.darek.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.darek.nmedia.R
import ru.darek.nmedia.databinding.CardPostBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.util.AndroidUtils.getStrCnt

interface PostCallback{
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
}

class PostsAdapter(private val postCallbeck: PostCallback) :
     ListAdapter<Post,PostViewHolder>(PostsDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, postCallbeck)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val postCallbeck: PostCallback
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

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.post_remove -> {
                                postCallbeck.onRemove(post)
                                true
                            }
                            R.id.post_edit -> {
                                postCallbeck.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            like.setOnClickListener{
                postCallbeck.onLike(post)
            }
            share.setOnClickListener{
                postCallbeck.onShare(post)
            }
        }
    }
}
class PostsDiffCallback: DiffUtil.ItemCallback<Post>(){
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}