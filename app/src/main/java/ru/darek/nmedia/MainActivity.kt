package ru.darek.nmedia

import android.os.Bundle
//import android.util.Log
import androidx.activity.viewModels
//import dto.Post
//import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import ru.darek.nmedia.databinding.ActivityMainBinding
import ru.darek.nmedia.viewmodel.PostViewModel

class MainActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likeCount.text = getStr(post.likes) //!!!
                shareCount.text = getStr(post.share)
                viewsCount.text = getStr(post.views)

                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
            }
        }
        binding.like.setOnClickListener {
            viewModel.like()
        }
          binding.share.setOnClickListener {
              viewModel.share()
        }
    }
}
fun getStr(inCnt:Int):String{
    if (inCnt >= 1000000) return String.format("%.2f M",(inCnt/1000000).toFloat())
    if (inCnt >= 10000) return String.format("%d K",inCnt/1000)
    if (inCnt >= 1000) return String.format("%.1f K",(inCnt/1000).toFloat())
    return inCnt.toString()
}