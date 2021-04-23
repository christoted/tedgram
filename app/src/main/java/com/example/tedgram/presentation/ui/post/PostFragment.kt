package com.example.tedgram.presentation.ui.post

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.example.tedgram.R
import com.example.tedgram.core.data.local.entity.Post
import com.example.tedgram.databinding.FragmentPostBinding
import com.example.tedgram.util.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage


class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    private var imageUrii: Uri? = null

    companion object {
        private val URI_KEY = "uri"
        private val CAPTION_KEY = "caption"
        private val PICK_IMAGE = 100
    }

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity == null) {
            return
        }

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        imageUrii = savedInstanceState?.getParcelable(URI_KEY)

        if (imageUrii != null) {
            Log.d("OY", "onViewCreated gak null: $imageUrii")
        }

        Log.d("OY", "onViewCreated: null $imageUrii")

        val radius = resources.getDimension(R.dimen.fab_margin)
        val shapeAppearanceModel =
            binding?.imageView?.shapeAppearanceModel?.toBuilder()?.setAllCornerSizes(radius)
                ?.build()

        binding?.imageView?.shapeAppearanceModel = shapeAppearanceModel!!

        cropActivityResultLauncher =
            activity!!.registerForActivityResult(cropActivityResultContract) {
                it.let {
                    binding?.iconAdd?.visibility = View.GONE
                    imageUrii = it
                    binding?.imageView?.setImageURI(imageUrii)

                }

            }

        binding?.iconAdd?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)

        }

        binding?.buttonPost?.setOnClickListener {

            binding?.progressBar?.visibility = View.VISIBLE

            if (binding?.etCaption?.text.toString().isEmpty() || imageUrii.toString().isEmpty()) {
                binding?.etCaption?.error = "Please Add Caption"
            } else {
                val caption = binding?.etCaption?.text.toString()
                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {
                    this?.putString(CAPTION_KEY, caption)
                    this?.apply()

                }


                val postModel = Post("", imageUrii.toString(), caption)

                if (imageUrii != null) {
                    addPost(postModel, imageUrii!!)
                }
            }
        }
    }

    private fun addPost(post: Post, imageUri: Uri) {

        val userId = mAuth?.currentUser?.uid

        val storageRef: StorageReference = storage?.reference!!.child(userId!! + Constant.POSTPATH)

        storageRef.putFile(imageUri).addOnSuccessListener {

            val downloadURL = it.storage.downloadUrl

            downloadURL.addOnCompleteListener { task ->

                val imagePath = task.result.toString()

                val postMap: MutableMap<String, Any> = HashMap()
                postMap["postId"] = post.postId.toString()
                postMap["postURL"] = imagePath
                postMap["postCaption"] = post.postCaption.toString()

                db?.collection("content")?.document(userId)?.collection("post")?.add(
                    postMap
                )?.addOnCompleteListener {
                    Toast.makeText(activity, "Posted!", Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.visibility = View.GONE
                }?.addOnFailureListener {
                    Toast.makeText(activity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.visibility = View.GONE
                }?.addOnSuccessListener {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.imageView?.setImageURI(null)
                    binding?.etCaption?.setText("")
                    Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                Log.d("TAG", "addPost: FAILED")
                binding?.progressBar?.visibility = View.GONE
            }

            binding?.progressBar?.visibility = View.GONE
            Toast.makeText(activity, "Success!", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUrii = data?.data
            binding?.imageView?.setImageURI(imageUrii)

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString(URI_KEY, imageUrii.toString())
                apply()

            }

            Log.d("URI", "onActivityResult: ${imageUrii.toString()}")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("OY", "onSaveInstanceState: PALING BAWAH $imageUrii")

        if (imageUrii != null) {
            outState.putParcelable(URI_KEY, imageUrii)
        }

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val imageUri = savedInstanceState?.get(URI_KEY)

        if (imageUri != null) {
            imageUrii = imageUri as Uri?
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val imageUriString = sharedPref.getString(URI_KEY, null)
        val textCaption = sharedPref.getString(CAPTION_KEY, null)


        Log.d("URI", "onResume: $textCaption")

        val imageUri = Uri.parse(imageUriString)
        binding?.imageView?.setImageURI(imageUri)
    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}