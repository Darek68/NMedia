package ru.darek.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import okhttp3.*
import ru.darek.nmedia.api.PostsApi
import ru.darek.nmedia.databinding.FragmentAuthBinding
import ru.darek.nmedia.databinding.FragmentNewPostBinding
import ru.darek.nmedia.entity.PostEntity
import ru.darek.nmedia.error.ApiError
import ru.darek.nmedia.error.NetworkError
import ru.darek.nmedia.error.UnknownError
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.AuthViewModel
import ru.darek.nmedia.viewmodel.FrmViewModel
import ru.darek.nmedia.viewmodel.PostViewModel

class AuthFragment: Fragment() {

    private val viewModel: FrmViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonReset.setOnClickListener {
            binding.textPassword.setText("")
            binding.textUserName.setText("")
        }

        binding.buttonSubmit.setOnClickListener {
            println("Вызов viewModel.getToken ${binding.textUserName.toString()}  ${binding.textPassword.toString()}")
           viewModel.getToken(binding.textUserName.toString(),binding.textPassword.toString())
        }

        return binding.root
    }
}