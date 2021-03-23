package com.dohieu19999.firebasechat.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dohieu19999.firebasechat.R
import com.dohieu19999.firebasechat.adapter.ChatAdapter
import com.dohieu19999.firebasechat.adapter.UserAdapter
import com.dohieu19999.firebasechat.model.Chat
import com.dohieu19999.firebasechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imdProfile
import kotlinx.android.synthetic.main.activity_chat.imgBack

class ChatActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var intent = getIntent()
        var userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        reference!!.addValueEventListener(object : ValueEventListener {
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
                getChatList(firebaseUser!!.uid, userId)
                etMessage.setText("")
            }
        }
        getChatList(firebaseUser!!.uid, userId)
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)

    }

    fun getChatList(senderId: String, receiverId: String) {

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId)
                        || chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                chatRecyclerView.adapter = chatAdapter
            }

        })
    }
}