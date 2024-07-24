package com.example.milestone1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
//import kotlinx.android.synthetic.main.activity_loginpage.*

class loginpage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Declare the button and EditText variables
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)


        loginButton.setOnClickListener {
            // Retrieve email and password from EditText fields inside the OnClickListener
            val userEmail = usernameInput.text.toString()
            val userPassword = passwordInput.text.toString()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                // Call sign in function
                signIn(userEmail, userPassword)
            } else {
                Toast.makeText(this, "email and password cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user information
                    Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show()

                    // Run the intent to move to the next page
                    val intent = Intent(this, MainActivity3::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException) {
                        // User with this email is not registered
                        Toast.makeText(this, "Email not registered. Please sign up first.", Toast.LENGTH_SHORT).show()
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        // Incorrect password
                        Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Other sign in failures
                        Toast.makeText(baseContext, "Authentication failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }

                    // Log the exception for debugging
                    exception?.printStackTrace()
                }
            }
    }

}
