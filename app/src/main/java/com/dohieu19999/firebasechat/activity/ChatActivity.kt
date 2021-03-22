package com.dohieu19999.firebasechat.activity

import android.content.BroadcastReceiver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dohieu19999.firebasechat.R
import com.dohieu19999.firebasechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var firebaseUser: FirebaseUser? = null
        var reference: DatabaseReference? = null

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var intent = getIntent()
        var userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                tvUserName.text = user!!.userName
                if (user!!.profileImage == "") {
                    imdProfile.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    Glide.with(this@ChatActivity).load(user.profileImage).into(imdProfile)
                }

            }

        })

        imgBack.setOnClickListener {
            onBackPressed()
        }
        btnSendMessage.setOnClickListener {
            var message: String = etMessage.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(applicationContext, "Message is Empty", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(firebaseUser!!.uid, userId, message)
            }
        }

    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)

    }
}