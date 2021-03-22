package com.dohieu19999.firebasechat.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dohieu19999.firebasechat.R
import com.dohieu19999.firebasechat.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private var firePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()

        storageRef = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                etUserName.setText(user!!.userName)
                if (user.profileImage == "") {
                    userImage.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    Glide.with(applicationContext).load(user.profileImage).into(userImage)
                }

            }

        })


        imgBack.setOnClickListener {
            onBackPressed()
        }

        userImage.setOnClickListener {
            chooseImage()
        }
        btnSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            uploadImage()
        }

    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if (firePath != null) {
            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(firePath!!).addOnSuccessListener {

                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("userName", etUserName.text.toString())
                hashMap.put("profileImage", firePath.toString())
                databaseReference.updateChildren(hashMap as Map<String, Any>)
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Uploaded", Toast.LENGTH_SHORT).show()
                btnSave.visibility = View.GONE
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    applicationContext,
                    "Failed " + it.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && requestCode != null) {
            firePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, firePath)
                userImage.setImageBitmap(bitmap)
                btnSave.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}