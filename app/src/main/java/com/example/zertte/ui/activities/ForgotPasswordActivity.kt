package com.example.zertte.ui.activities

import android.os.Bundle
import android.widget.Toast
import com.example.zertte.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
    }

    private fun setupActionBar() {
        binding.submit.setOnClickListener {
            val email: String = binding.enterEmail.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                showErrorSnackBar("Please enter email", true)
            } else {
                showProgressDialog("Please, wait...")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            // Display Toast on the main thread
                            runOnUiThread {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Your password has been reset",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
}