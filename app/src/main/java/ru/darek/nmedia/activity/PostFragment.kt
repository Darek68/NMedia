package ru.darek.nmedia.activity

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.CardPostBinding
import ru.darek.nmedia.databinding.FragmentFeedBinding
import ru.darek.nmedia.databinding.FragmentPostBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.util.AndroidUtils.getStrCnt
import ru.darek.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    var thisPost: Post? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )
        //addView(binding.root)
        val id = arguments?.getLong("id") // id отображаемого поста
        if (id == null) {
            Toast.makeText(
                context,
                "Не указан id поста!",
                Toast.LENGTH_SHORT
            ).show()
            return binding.root
        }

        viewModel.data.observe(viewLifecycleOwner) { data ->
            data.posts.map { post ->
                if (post.id == id) {
                    thisPost = post
                    binding.post.apply {
                        author.text = post.author
                        published.text = post.published
                        content.text = post.content
                        like.text = getStrCnt(post.likes)
                        share.text = getStrCnt(post.share)
                        views.text = getStrCnt(post.views)
                        //group.visibility = if (post.video.isBlank()) View.GONE else View.VISIBLE
                        group.visibility =
                            if (post.video.isNullOrBlank()) View.GONE else View.VISIBLE
                        like.setIconTintResource(if (post.likedByMe) R.color.red else R.color.grey)
                        like.isChecked = post.likedByMe

                    }
                    val url = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                    Glide.with(binding.post.avatar)
                        .load(url)
                        .placeholder(R.drawable.ic_loading_100dp)
                        .error(R.drawable.ic_error_100dp)
                        .circleCrop()
                        .timeout(10_000)
                        .into(binding.post.avatar)
                }
            }
        }

        binding.post.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.post_options)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.post_remove -> {
                            Toast.makeText(
                                context,
                                "Тут будем удалять пост!",
                                Toast.LENGTH_SHORT
                            ).show()
                            //postCallbeck.onRemove(post)
                            viewModel.removeById(id)
                            findNavController().popBackStack()
                            true
                        }

                        R.id.post_edit -> {
                            val bundle = Bundle().apply {
                                putString("content", thisPost!!.content)
                                putBoolean("edit", true) // признак редактирования
                            }
                            //viewModel.edited.value = thisPost
                            viewModel.edit(thisPost!!)
                            findNavController().navigate(
                                R.id.action_postFragment_to_newPostFragment,
                                bundle
                            )
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.post.like.setOnClickListener {
            thisPost?.let { post -> viewModel.likeById(post.id) }
        }
        binding.post.share.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, thisPost?.content)
                type = "text/plain"
            }
            thisPost?.let { post -> viewModel.shareById(post.id) }
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }
        return binding.root
    }
}