package com.example.zertte.auth

import android.app.Dialog
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.zertte.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    
    private var doubleBackToExitPressedOne = false

    private lateinit var mProgressDialog: Dialog

        fun showErrorSnackBar(message: String, errorMessage: Boolean){
            val snackBar =
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            val snackBarView = snackBar.view

            if(errorMessage){
                snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        this@BaseActivity,
                        R.color.colorSnackBarError
                    )
                )
            }else{
                snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                        this@BaseActivity,
                        R.color.colorSnackBarSuccess
                    )
                )
            }
            snackBar.show()
        }

    fun showProgressDialog(text: String){

        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)

        val progressTextView = mProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        progressTextView.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){

        if(doubleBackToExitPressedOne){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOne = true

        Toast.makeText(
            this,
            "Click back again to exit",
            Toast.LENGTH_SHORT
        ).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed({doubleBackToExitPressedOne = false}, 2000)
    }
}