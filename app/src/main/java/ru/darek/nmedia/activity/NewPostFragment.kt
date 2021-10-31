package ru.darek.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_post.*
import ru.darek.nmedia.databinding.FragmentNewPostBinding
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.PostViewModel
import java.util.prefs.Preferences

class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        val edit = arguments?.getBoolean("edit") == true
        // This callback will only be called when MyFragment is at least Started.
        val prefsDraft = context?.getSharedPreferences("myDrafts", Context.MODE_PRIVATE)
        // для редактирования берем content из bundlу, для нового - из SharedPref
        if (edit) {
            arguments?.getString("content").let { binding.edit.setText(it) }
        } else binding.edit.setText(prefsDraft?.getString("content", ""))

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            if (!edit) {
                prefsDraft?.edit()?.let {
                    it.putString("content", "")
                    it.apply()
                }
            }
            AndroidUtils.hideKeyboard(requireView())
            // findNavController().popBackStack()
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            /* Toast.makeText(
                 context,
                 "viewModel.postCreated.observe",
                 Toast.LENGTH_SHORT
             ).show() */
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }

}