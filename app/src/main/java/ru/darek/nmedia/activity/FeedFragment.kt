package ru.darek.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.FragmentFeedBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : PostCallback {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle().apply {
                    putString("content", post.content)
                    putBoolean("edit", true) // признак редактирования
                }
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    bundle
                )
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                viewModel.shareById(post.id)
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onVideo(post: Post) {
                if (post.video.isNullOrBlank()) {
                    Toast.makeText(
                        context,
                        "Нечего проигрывать!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                val videoIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_video_player))
                startActivity(videoIntent)
            }

            override fun onContent(post: Post) {
                val bundle = Bundle().apply {
                    putLong("id", post.id)
                }
                viewModel.edited.value = post
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    bundle
                )
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            // binding.emptyText.isVisible = state.posts.isEmpty()
        })

        binding.fab.setOnClickListener {
            val bundle = Bundle().apply {
                putString("content", "Укажите текст поста..")
            }
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                bundle
            )
        }
        return binding.root
    }
}