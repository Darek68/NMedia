package ru.darek.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.darek.nmedia.BuildConfig
import ru.darek.nmedia.R
import ru.darek.nmedia.databinding.FragmentPicBinding
import ru.darek.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PicFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var fragmentBinding: FragmentPicBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentPicBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding
        val url = arguments?.getString("pic")
        val id = arguments?.getLong("id")
        binding.apply {
            figure.requestFocus()
            // d7dff806-4456-4e35-a6a1-9f2278c5d639.png  привести к http://10.0.2.2:9999/media/d7dff806-4456-4e35-a6a1-9f2278c5d639.png
            //private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"
            //private const val BASE_URL = "http://10.0.2.2:9999/api/"
            figure.visibility = View.GONE
            url?.let {
                val urlPicture = "${BuildConfig.BASE_URL}/media/${it}"
                Glide.with(figure)
                    .load(urlPicture)
                    .placeholder(R.drawable.ic_loading_100dp)
                    .error(R.drawable.ic_error_100dp)
                    .timeout(10_000)
                    .into(figure)
                figure.visibility = View.VISIBLE
               // figure.setImageURI(it.toUri())
              /*  viewModel.data.observe(viewLifecycleOwner) { data ->
                    data.posts.map { post ->
                        if (post.id == id) {
                            post.attachment?.let { att -> figure.setImageURI(att.url.toUri())}
                        }
                    }
                } */

            }
        }

        binding.figure.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}