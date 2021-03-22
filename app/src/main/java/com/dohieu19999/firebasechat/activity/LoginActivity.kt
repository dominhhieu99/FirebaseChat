package com.dohieu19999.firebasechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.dohieu19999.firebasechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        try {
            firebaseUser = auth.currentUser!!
            if (firebaseUser != null) {
                var intent = Intent(this@LoginActivity, UsersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }catch (e:Exception){}



        etEmail.setText("dohieu19999@gmail.com")
        etPassword.setText("123456")

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "email and password are required",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        etEmail.setText("")
                        etPassword.setText("")
                        var intent = Intent(this@LoginActivity, UsersActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "email and password invalid",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        btnSignUp.setOnClickListener {
            var intent = Intent(this@LoginActivity, SingUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}