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
import ru.darek.nmedia.R
import ru.darek.nmedia.adapter.PostCallback
import ru.darek.nmedia.adapter.PostsAdapter
import ru.darek.nmedia.databinding.CardPostBinding
import ru.darek.nmedia.databinding.FragmentFeedBinding
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
        savedInstanceState: Bundle?
    ): View {
        val binding = CardPostBinding.inflate(
            inflater,
            container,
            false
        )
        val id = arguments?.getLong("id") // id отображаемого поста
        if (id == null) {
            Toast.makeText(
                context,
                "Не указан id поста!",
                Toast.LENGTH_SHORT
            ).show()
            return binding.root
        }
        Toast.makeText(
            context,
            "id = " + id.toString(),
            Toast.LENGTH_SHORT
        ).show()
     //  val post = getPostById(id)
      //  binding.content.setText(viewModel.edited.value?.content)
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            posts.map {post ->
                if (post.id == id) {
                    thisPost = post
                    binding.apply {
                        author.text = post.author
                        published.text = post.published
                        content.text = post.content
                        like.text = getStrCnt(post.likes)
                        share.text = getStrCnt(post.share)
                        views.text = getStrCnt(post.views)
                        group.visibility = if (post.video.isBlank()) View.GONE else View.VISIBLE
                        like.setIconTintResource(if (post.likedByMe) R.color.red else R.color.grey)
                        like.isChecked = post.likedByMe

                    }
                }
            }
        }
        if (thisPost == null) {
            Toast.makeText(
                context,
                "Не найден пост!",
                Toast.LENGTH_SHORT
            ).show()
           // findNavController().popBackStack()
        }
        binding.menu.setOnClickListener {
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
                                putString("content",thisPost!!.content)
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
      /*  viewModel.edited.observe(viewLifecycleOwner) { post ->
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                like.text = getStrCnt(post.likes)
                share.text = getStrCnt(post.share)
                views.text = getStrCnt(post.views)
                group.visibility =  if (post.video.isBlank()) View.GONE else View.VISIBLE
                like.setIconTintResource(if (post.likedByMe) R.color.red else R.color.grey)
                like.isChecked = post.likedByMe
            }

        binding.menu.setOnClickListener {
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
                            viewModel.removeById(post.id)
                            findNavController().popBackStack()
                            true
                        }
                        R.id.post_edit -> {
                            val bundle = Bundle().apply {
                                putString("content",post.content)
                            }
                            viewModel.edited.value = post
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
        } */


       /*
       // binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        } */

        return binding.root
    }
  /*  fun getPostById(id: Long): Post {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe)
        }
        return post
    } */
}