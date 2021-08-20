package ru.darek.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
//import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.ActivityMainBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.PostViewModel
import ru.darek.nmedia.activity.NewPostResultContract

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()

        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }

        intent?.let {
            if (it.action != Intent.ACTION_SEND){
                return@let
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()){
                Snackbar.make(binding.root,"Да что это такое?",
                    BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("ОК"){finish()}.show()
                return@let
            }
            newPostLauncher.launch(text)
        }

        val adapter = PostsAdapter(object : PostCallback {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }
            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }
            /*  override fun onShare(post: Post) {
                  viewModel.shareById(post.id)
              } */
            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }
            override fun onVideo(post: Post) {
                if (post.video.isBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Нечего проигрывать!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(post.video))
                val videoIntent = Intent.createChooser(intent, getString(R.string.chooser_video_player))
                startActivity(videoIntent)
            }
        })
        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)

            // binding.group.visibility = View.GONE // перестаёт занимать место на экране
            //binding.group.visibility = View.INVISIBLE // невидима, но занимает место на экране
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch("")
        }
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) return@observe
            newPostLauncher.launch(post.content)
            /* binding.group.visibility = View.VISIBLE
             with(binding.content) {
                 requestFocus()
                 setText(post.content)
                 binding.textCont.text = post.content
             } */
        }
/*
        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()
                binding.textCont.setText("")
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }
        binding.content.setOnClickListener {
            binding.group.visibility = View.VISIBLE
        }
        binding.cancel.setOnClickListener {
            binding.textCont.setText("")
            binding.content.setText("")
            binding.content.clearFocus()
            AndroidUtils.hideKeyboard(binding.content)
            binding.group.visibility = View.GONE // перестаёт занимать место на экране
        } */

    }
}