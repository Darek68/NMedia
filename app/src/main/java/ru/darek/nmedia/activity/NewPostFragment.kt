package ru.darek.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_new_post.*
import ru.darek.nmedia.databinding.FragmentNewPostBinding
import ru.darek.nmedia.util.AndroidUtils
//import ru.darek.nmedia.util.StringArg
import ru.darek.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

   /* companion object {
        var Bundle.textArg: String? by StringArg
    }  */

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        // arguments?.textArg?.let(binding.edit::setText)
        arguments?.getString("content").let {  binding.edit.setText(it) }

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            //findNavController().navigateUp()
            findNavController().popBackStack()
        }
        return binding.root
    }
}