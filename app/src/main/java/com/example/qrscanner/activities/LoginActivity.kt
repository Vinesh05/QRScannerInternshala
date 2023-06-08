package com.example.qrscanner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.qrscanner.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mauth = FirebaseAuth.getInstance()

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

        binding.apply {

            txtCreateAccount.setOnClickListener {
                Intent(this@LoginActivity, SignUpActivity::class.java).also{
                    startActivity(it)
                }
            }

            btnLogin.setOnClickListener {
                btnLogin.visibility = View.INVISIBLE
                progressBarLogin.visibility = View.VISIBLE
                val email = edtTxtEmail.text.toString()
                val password = edtTxtPassword.text.toString()
                mauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this@LoginActivity) { signIn ->
                        if (signIn.isSuccessful) {
                            btnLogin.visibility = View.VISIBLE
                            progressBarLogin.visibility = View.GONE
                            Intent(this@LoginActivity, MainActivity::class.java).also{
                                startActivity(it)
                            }
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                        }
                    }
            }

        }

    }

}