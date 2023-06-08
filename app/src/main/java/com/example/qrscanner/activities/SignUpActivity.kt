package com.example.qrscanner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.qrscanner.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mauth = FirebaseAuth.getInstance()

        binding.apply {

            btnSignUp.setOnClickListener {
                btnSignUp.visibility = View.INVISIBLE
                progressBarSignUp.visibility = View.VISIBLE
                val email = edtTxtEmail.text.toString()
                val password = edtTxtPassword.text.toString()
                mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@SignUpActivity) { signUp ->
                        if(signUp.isSuccessful) {
                            btnSignUp.visibility = View.VISIBLE
                            progressBarSignUp.visibility = View.GONE
                            Toast.makeText(baseContext, "Sign Up Successful.", Toast.LENGTH_LONG).show()
                            Intent(this@SignUpActivity, LoginActivity::class.java).also{
                                startActivity(it)
                            }
                        } else {
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }

    }

}