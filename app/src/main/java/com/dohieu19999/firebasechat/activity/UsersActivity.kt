package com.dohieu19999.firebasechat.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dohieu19999.firebasechat.R
import com.dohieu19999.firebasechat.adapter.UserAdapter
import com.dohieu19999.firebasechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {
    var userList = ArrayList<User>()

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)


        userRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


        imgBack.setOnClickListener {
            onBackPressed()
        }
        imdProfile.setOnClickListener {
            var intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        getUsersList()
    }

    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.userImage == "") {
                    imdProfile.setImageResource(R.drawable.ic_launcher_background)
                } else {
                    Glide.with(this@UsersActivity).load(currentUser!!.userImage).into(imdProfile)
                }



                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {

                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userList)

                userRecyclerView.adapter = userAdapter
            }

        })
    }
}