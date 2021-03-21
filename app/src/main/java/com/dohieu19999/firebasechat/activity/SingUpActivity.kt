package com.dohieu19999.firebasechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dohieu19999.firebasechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SingUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            val userName = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (userName.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "username is required", Toast.LENGTH_SHORT)
                    .show()
            }
            if (email.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "email is required", Toast.LENGTH_SHORT).show()
            }
            if (password.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "password is required", Toast.LENGTH_SHORT)
                    .show()
            }
            if (confirmPassword.isNullOrEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "confirmPassword is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (password != confirmPassword) {
                Toast.makeText(applicationContext, "password not match", Toast.LENGTH_SHORT).show()
            }
            registerUser(userName, email, password)
        }
        btnLogin.setOnClickListener {
            var intent = Intent(this@SingUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser = auth.currentUser
                    val userId: String = user!!.uid

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("emali", email)
                    hashMap.put("profileImage", "")

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
//                            open homne
                            etName.setText("")
                            etEmail.setText("")
                            etPassword.setText("")
                            etConfirmPassword.setText("")
                            var intent = Intent(this@SingUpActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "" + it.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

}