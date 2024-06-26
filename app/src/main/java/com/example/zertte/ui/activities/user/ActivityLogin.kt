package com.example.zertte.ui.activities.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.zertte.MainActivity
import com.example.zertte.R
import com.example.zertte.databinding.ActivityLoginBinding
import com.example.zertte.model.User
import com.example.zertte.network.Firestore.FirestoreClass
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.ui.activities.ForgotPasswordActivity
import com.example.zertte.ui.activities.guide.ActivityLoginGuide
import com.example.zertte.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class ActivityLogin : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

    @Suppress("DEPRECATION")
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    }else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
        binding.forgotYourPassword.setOnClickListener(this)
        binding.loginButton.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
        binding.guide.setOnClickListener(this)
    }

    fun userLoggedInSuccess(user: User){

        hideProgressDialog()

        if(user.profileCompleted == 0){
            val intent = Intent(this@ActivityLogin, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }else{
            startActivity(Intent(this@ActivityLogin, MainActivity::class.java))
        }
        finish()
    }

    override fun onClick(view: View?) {
        if(view!= null){
            when(view.id){

                R.id.forgot_your_password -> {
                    val intent = Intent(this@ActivityLogin, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.loginButton -> {
                    loginRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent = Intent(this@ActivityLogin, ActivitySignIn::class.java)
                    startActivity(intent)
                }

                R.id.guide -> {
                    val intent = Intent(this@ActivityLogin, ActivityLoginGuide::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun validateLoginDetails(): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}")

        return when {
            TextUtils.isEmpty(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            !emailRegex.matches(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_email), true)
                false
            }
            TextUtils.isEmpty(binding.passwordEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            !passwordRegex.matches(binding.passwordEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser(){

        if(validateLoginDetails()){

            showProgressDialog("Please, wait...")

            val email =  binding.emailEditText.text.toString().trim{ it <= ' '}
            val password =  binding.passwordEditText.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task->

                    if(task.isSuccessful){
                        FirestoreClass().getUserDetails(this@ActivityLogin)
                    }else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}
