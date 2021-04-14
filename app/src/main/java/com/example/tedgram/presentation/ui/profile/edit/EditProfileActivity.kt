package com.example.tedgram.presentation.ui.profile.edit

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.tedgram.R
import com.example.tedgram.databinding.ActivityEditProfileBinding
import com.example.tedgram.presentation.ui.login.LoginActivity
import com.example.tedgram.util.Constant
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditProfileActivity : AppCompatActivity() {

    companion object {
        val TAG: String? = EditProfileActivity::class.simpleName
    }

    private var imageUri: Uri? = null
    private var imagePath: String? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

    }

    private var _binding: ActivityEditProfileBinding? = null
    private val binding get() = _binding

    private var mAuth: FirebaseAuth? = null

    private var storage: FirebaseStorage? = null

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>


    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        db = FirebaseFirestore.getInstance()

        mAuth = FirebaseAuth.getInstance()

        storage = FirebaseStorage.getInstance()

        binding?.progressBar?.visibility = View.VISIBLE

        retrieveUserProfile()

        binding?.toolbar?.setNavigationOnClickListener {
            showAlertDialog()
        }

        binding?.btnLogout?.setOnClickListener {
            mAuth?.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it.let {
                imageUri = it
                binding?.imageView?.setImageURI(it)
            }
        }

        binding?.editPhoto?.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        binding?.toolbar?.setOnMenuItemClickListener {
            val bio = binding?.etBio?.text.toString()
            val username = binding?.etUsername?.text.toString()
            val fullName = binding?.etFullName?.text.toString()
            Log.d(TAG, "IMAGE URI $imageUri")
            when (it.itemId) {
                R.id.done -> {
                    Log.d(TAG, "onCreate: bio $bio username $username fullname $fullName")
                    updateUserProfile(bio, username, fullName, imageUri)

                    Snackbar.make(binding!!.root, "Update Success", Snackbar.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 3000)

                }
            }
            true
        }
    }


    private fun updateUserProfile(bio: String?, fullName: String?, username: String?, uri: Uri?) {

        val storageRef: StorageReference = storage?.reference!!.child(mAuth?.uid + Constant.PATH)

        if (uri != null) {
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    val downloadUrl = it.storage.downloadUrl
                    downloadUrl.addOnCompleteListener { task ->
                        imagePath = task.result.toString()

                        val userRef = db!!.collection("users").document(mAuth?.currentUser!!.uid)
                        userRef.update(
                            mapOf(
                                "bio" to bio,
                                "fullName" to fullName,
                                "username" to username,
                                "imageUrl" to imagePath

                            )
                        )
                            .addOnSuccessListener {
                                Log.d(TAG, "updateUserProfile: Success")
                            }.addOnFailureListener {
                                Log.d(TAG, "updateUserProfile: Failed")
                            }

                    }
                }
                .addOnFailureListener {

                }
        }


    }


    private fun retrieveUserProfile() = GlobalScope.launch(Dispatchers.IO) {
        db?.collection("users")
            ?.document(mAuth?.currentUser!!.uid)
            ?.get()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result
                    val email: String = result?.get("email").toString()
                    val bio: String = result?.get("bio").toString()
                    val fullName: String = result?.get("fullName").toString()
                    val imageUrl: String = result?.get("imageUrl").toString()
                    val username: String = result?.get("username").toString()

                    binding?.etFullName?.setText(fullName)
                    binding?.etUsername?.setText(username)
                    binding?.etBio?.setText(bio)

                    binding?.imageView?.let { imageHolder ->
                        Glide.with(this@EditProfileActivity)
                            .load(imageUrl)
                            .into(imageHolder)
                    }

                    binding?.progressBar?.visibility = View.GONE

                } else {
                    Log.w(
                        TAG,
                        "Error getting documents.",
                        it.exception
                    )
                    binding?.progressBar?.visibility = View.GONE
                }
            }
            ?.addOnFailureListener {
                Log.d(TAG, "retrieveUserProfile: ")
                binding?.progressBar?.visibility = View.GONE
            }

            withContext(Dispatchers.Main) {
               
            }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure to discard the edit profile? You're edit progress won't save")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "Yes"
        ) { _, _ -> finish() }

        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog?.cancel()
            }
        })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}