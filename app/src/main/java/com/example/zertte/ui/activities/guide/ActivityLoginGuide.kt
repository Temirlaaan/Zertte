package com.example.zertte.ui.activities.guide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.zertte.MainActivityGuide
import com.example.zertte.R
import com.example.zertte.databinding.ActivityLoginGuidesBinding
import com.example.zertte.model.Guide
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.activities.BaseActivity
import com.example.zertte.ui.activities.ForgotPasswordActivity
import com.example.zertte.utils.Constants
import com.google.firebase.auth.FirebaseAuth


class ActivityLoginGuide : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginGuidesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginGuidesBinding.inflate(layoutInflater)
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
    }

    fun guideLoggedInSuccess(guide: Guide){

        hideProgressDialog()

        if(guide.profileCompleted == 0){
            val intent = Intent(this@ActivityLoginGuide, GuideProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_GUIDE_DETAILS, guide)
            startActivity(intent)
        }else{
            startActivity(Intent(this@ActivityLoginGuide, MainActivityGuide::class.java))
        }
        finish()
    }

    override fun onClick(view: View?) {
        if(view!= null){
            when(view.id){

                R.id.forgot_your_password -> {
                    val intent = Intent(this@ActivityLoginGuide, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.loginButton -> {
                    loginRegisteredGuide()
                }

                R.id.tv_register -> {
                    val intent = Intent(this@ActivityLoginGuide, ActivitySignInGuide::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun validateLoginDetails(): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})") // Регулярное выражение для адреса электронной почты
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}") // Регулярное выражение для пароля

        return when {
            TextUtils.isEmpty(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            !emailRegex.matches(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_email), true) // Сообщение об ошибке для неверного формата адреса электронной почты
                false
            }
            TextUtils.isEmpty(binding.passwordEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            !passwordRegex.matches(binding.passwordEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_password), true) // Сообщение об ошибке для неверного формата пароля
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredGuide(){

        if(validateLoginDetails()){

            showProgressDialog("Please, wait...")

            val email =  binding.emailEditText.text.toString().trim{ it <= ' '}
            val password =  binding.passwordEditText.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{task->

                    if(task.isSuccessful){
                        FirestoreClassGuides().getGuideDetails(this@ActivityLoginGuide)
                    }else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }
}