package ru.darek.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.ActivityMainBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
/*
        val adapter = PostsAdapter {
            viewModel.likeById(it.id)
        } */
        val adapter = PostsAdapter(object : PostCallback {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }

        })
        binding.list.adapter = adapter
       // binding.list.itemAnimator = null

        viewModel.data.observe(this) { posts ->
            //adapter.list = posts
            adapter.submitList(posts)
        }
    }
}