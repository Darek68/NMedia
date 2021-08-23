package ru.darek.nmedia.activity

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.CardPostBinding
import ru.darek.nmedia.databinding.FragmentFeedBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    val post = viewModel.edited.value
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.apply {
            if (post != null) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                like.text = AndroidUtils.getStrCnt(post.likes)
                share.text = AndroidUtils.getStrCnt(post.share)
                views.text = AndroidUtils.getStrCnt(post.views)
                group.visibility = if (post.video.isBlank()) View.GONE else View.VISIBLE
                like.setIconTintResource(if (post.likedByMe) R.color.red else R.color.grey)
                like.isChecked = post.likedByMe
            }
        }
      /*  arguments?.getLong("id").let {
            findPostById
        }
        arguments?.getString("content").let {  binding.edit.setText(it) }
        binding.content.setText(it) */

        val adapter = PostsAdapter(object : PostCallback {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val bundle = Bundle().apply {
                    putString("content",post.content)
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

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
            override fun onVideo(post: Post) {
                if (post.video.isBlank()) {
                    Toast.makeText(
                        context,
                        "Нечего проигрывать!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                val videoIntent = Intent.createChooser(intent, getString(R.string.chooser_video_player))
                startActivity(videoIntent)
            }

            override fun onContent(post: Post) {
                //
            }
        })
        return binding.root
    }
}