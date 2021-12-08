package ru.darek.nmedia.activity

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_post.*
import ru.darek.nmedia.R
import ru.darek.nmedia.databinding.FragmentNewPostBinding
import ru.darek.nmedia.util.AndroidUtils
import ru.darek.nmedia.viewmodel.PostViewModel
import java.util.prefs.Preferences

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    val edit = arguments?.getBoolean("edit") == true
    val prefsDraft = context?.getSharedPreferences("myDrafts", Context.MODE_PRIVATE)

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    private var fragmentBinding: FragmentNewPostBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    viewModel.changeContent(it.edit.text.toString())
                    viewModel.save()
                    if (!edit) {
                        prefsDraft?.edit()?.let {
                            it.putString("content", "")
                            it.apply()
                        }
                    }
                    AndroidUtils.hideKeyboard(requireView())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
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
        fragmentBinding = binding
      /*  val edit = arguments?.getBoolean("edit") == true
        // This callback will only be called when MyFragment is at least Started.
        val prefsDraft = context?.getSharedPreferences("myDrafts", Context.MODE_PRIVATE) */
        // для редактирования берем content из bundlу, для нового - из SharedPref
        if (edit) {
            arguments?.getString("content").let { binding.edit.setText(it) }
        } else binding.edit.setText(prefsDraft?.getString("content", ""))
        binding.edit.requestFocus()
        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }
      /*  binding.ok.setOnClickListener {
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
        } */
        viewModel.postCreated.observe(viewLifecycleOwner) {
            /* Toast.makeText(
                 context,
                 "viewModel.postCreated.observe",
                 Toast.LENGTH_SHORT
             ).show() */
           // viewModel.loadPosts()
            findNavController().navigateUp()
        }
        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        return binding.root
    }

}