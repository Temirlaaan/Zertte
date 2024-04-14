package com.example.zertte.ui.activities.guide

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.zertte.R
import com.example.zertte.databinding.ActivitySignInGuidesBinding
import com.example.zertte.model.Guide
import com.example.zertte.network.Firestore.FirestoreClassGuides
import com.example.zertte.ui.activities.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ActivitySignInGuide : BaseActivity() {
    private lateinit var binding: ActivitySignInGuidesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInGuidesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        binding.signInButton.setOnClickListener {
            registerGuide()
        }
    }

    private fun validateRegisterDetails(): Boolean {
        val nameRegex = Regex("^[\\p{L} .'-]+$") // Регулярное выражение для имени
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})") // Регулярное выражение для адреса электронной почты
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}") // Регулярное выражение для пароля

        return when {
            TextUtils.isEmpty(binding.enterName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            !nameRegex.matches(binding.enterName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_name), true) // Сообщение об ошибке для неверного формата имени
                false
            }
            TextUtils.isEmpty(binding.enterSurname.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            !nameRegex.matches(binding.enterSurname.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_last_name), true) // Сообщение об ошибке для неверного формата фамилии
                false
            }
            TextUtils.isEmpty(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            !emailRegex.matches(binding.emailEditText.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_email), true) // Сообщение об ошибке для неверного формата адреса электронной почты
                false
            }
            TextUtils.isEmpty(binding.password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            !passwordRegex.matches(binding.password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_valid_password), true) // Сообщение об ошибке для неверного формата пароля
                false
            }
            TextUtils.isEmpty(binding.confirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }
            binding.password.text.toString().trim { it <= ' ' } != binding.confirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerGuide(){

        showProgressDialog("Please, wait...")

        if (validateRegisterDetails()){
            val email: String = binding.emailEditText.text.toString().trim{ it <= ' '}
            val password: String = binding.password.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult>{ task ->

                        if(task.isSuccessful){
                            val firebaseGuide: FirebaseUser = task.result!!.user!!

                            val guide = Guide(
                                firebaseGuide.uid,
                                binding.enterName.text.toString().trim{ it <= ' '},
                                binding.enterSurname.text.toString().trim{ it <= ' '},
                                binding.emailEditText.text.toString().trim{ it <= ' '}
                            )

                            FirestoreClassGuides().registerGuide(this@ActivitySignInGuide, guide)
//                            FirebaseAuth.getInstance().signOut()
//                            finish()

                        }else{
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }else {
            hideProgressDialog() // Добавляем вызов hideProgressDialog() в случае невалидных данных
        }
    }

    fun guideRegistrationSuccess(){

        hideProgressDialog()

        Toast.makeText(
            this@ActivitySignInGuide,
            "You have registered successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@ActivitySignInGuide, ActivityLoginGuide::class.java)
        startActivity(intent)
        finish()
    }
}