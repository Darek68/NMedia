package ru.darek.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.auth.AppAuth
import ru.darek.nmedia.auth.AuthState
import ru.darek.nmedia.databinding.FragmentFeedBinding
import ru.darek.nmedia.dto.Post
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.AuthViewModel
import ru.darek.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private val viewModelAuth: AuthViewModel by viewModels()

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

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                if (!viewModelAuth.authenticated) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_authFragment
                    )
                }
                if (viewModelAuth.authenticated) { viewModel.likeById(post.id)}
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
               // viewModel.edited.value = post
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    bundle
                )
            }
        })
        binding.list.adapter = adapter
        //binding.newerButton.isVisible = false
        binding.newerButton.visibility = View.GONE

        viewModel.data.observe(viewLifecycleOwner, { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
            binding.swiperefresh.isRefreshing
        })
        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
             if(state.error) {
                 Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                     .setAction("Retry"){viewModel.loadPosts()}
                     .show()
             }
            binding.errorGroup.isVisible = state.error
            binding.swiperefresh.isRefreshing
        })
        viewModel.newerCount.observe(viewLifecycleOwner) { state ->
            if (state > 0) {
                viewModel.newerCntSum += state
                binding.newerButton.setText(getString(R.string.newer_Button) + viewModel.newerCntSum.toString())
               // binding.newerButton.isVisible = true
                binding.newerButton.visibility = View.VISIBLE
            }
              //  println("Ответ сервера:  ${state}")
        }
        binding.newerButton.setOnClickListener {
            viewModel.loadNewPosts()
            viewModel.newerCntSum = 0
            binding.list.smoothScrollToPosition(0)
           // binding.newerButton.isVisible = false
            binding.newerButton.visibility = View.GONE
            /* binding.list.submitList {
                 binding.list.smoothScrolltoPosition(0)
             }
             adapter.submitList(state.posts)
             list.submitList {
                 list.smoothScrolltoPosition(0)
             } */
        }
        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.fab.setOnClickListener {
          /*  if (!viewModelAuth.authenticated) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_authFragment
                )
            // Вот эти строки портят работу :-(
              //  AndroidUtils.hideKeyboard(requireView())
              //  findNavController().popBackStack()
            }
            if (viewModelAuth.authenticated) {
                val bundle = Bundle().apply {
                    putString("content", "Укажите текст поста..")
                }
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    bundle
                )
            } else {
                Toast.makeText(
                    context,
                    "Вы должны залогинится!",
                    Toast.LENGTH_SHORT
                ).show()
            } */
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