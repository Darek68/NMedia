package ru.darek.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.*
import ru.darek.nmedia.R
import ru.darek.nmedia.databinding.FragmentAuthBinding
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.FrmViewModel

@AndroidEntryPoint
class AuthFragment: Fragment() {

    private val viewModelFrm: FrmViewModel by viewModels(
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
           val result = viewModelFrm.getToken(binding.textUserName.text.toString(),binding.textPassword.text.toString())
           // AndroidUtils.hideKeyboard(requireView())
           // findNavController().popBackStack()
        }

        viewModelFrm.data.observe(viewLifecycleOwner) {
            when (viewModelFrm.data.value) {
                404 -> {
                    Snackbar.make(binding.root, R.string.error_log_in, Snackbar.LENGTH_LONG)
                        .setAction("Retry"){}
                        .show()
                    println("viewModelFrm.data.observe => Ошибка неверный логин-пароль!!")
                }
                0 -> {
                    println("viewModelFrm.data.observe => Залогинились!!")
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().popBackStack()
                   // findNavController().navigateUp()
                }
                else -> {
                    Snackbar.make(binding.root, R.string.error_signin, Snackbar.LENGTH_LONG)
                        .setAction("Retry"){}
                        .show()
                    println("viewModelFrm.data.observe => Ошибка авторизации!!")
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().popBackStack()
                  //  findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }
}