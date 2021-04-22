package com.example.tedgram.presentation.ui.post

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tedgram.R
import com.example.tedgram.databinding.FragmentPostBinding
import com.theartofdev.edmodo.cropper.CropImage


class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    private var imageUrii: Uri? = null

    companion object {
        private val URI_KEY = "uri"
    }

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

        imageUrii = savedInstanceState?.getParcelable(URI_KEY)

        if ( imageUrii != null) {
            Log.d("OY", "onViewCreated gak null: $imageUrii")
        }

        Log.d("OY", "onViewCreated: null $imageUrii")

        val radius = resources.getDimension(R.dimen.corner_radius)
        binding?.imageView?.shapeAppearanceModel?.toBuilder()?.setAllCornerSizes(radius)?.build()

        cropActivityResultLauncher =
            activity!!.registerForActivityResult(cropActivityResultContract) {
                it.let {
                    binding?.iconAdd?.visibility = View.GONE
                    imageUrii = it
                    binding?.imageView?.setImageURI(imageUrii)

                }

            }

        binding?.iconAdd?.setOnClickListener {
            cropActivityResultLauncher.launch(null)

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("OY", "onSaveInstanceState: PALING BAWAH $imageUrii")
        outState.putParcelable(URI_KEY, imageUrii)

    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}