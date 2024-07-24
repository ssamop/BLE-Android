package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val nameInput = findViewById<EditText>(R.id.Name)
        val emailInput = findViewById<EditText>(R.id.Email)
        val passwordInput = findViewById<EditText>(R.id.passwordsignup)
        val signUpButton = findViewById<Button>(R.id.signupButton)

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signUp(name, email, password)
            } else {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-up user's information
                    Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()

                    // Run the intent to move to the next page (replace NextActivity::class.java with your desired destination)
                    val intent = Intent(this, scan::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign up fails, display a message to the user.
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        // User with this email already exists
                        Toast.makeText(this, "Email is already registered. Try signing in.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Other sign up failures
                        Toast.makeText(this, "Account creation failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}