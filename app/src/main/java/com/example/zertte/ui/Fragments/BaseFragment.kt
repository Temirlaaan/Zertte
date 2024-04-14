package com.example.zertte.ui.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zertte.R
import com.example.zertte.databinding.FragmentBaseBinding

open class BaseFragment() : Fragment() {

    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!

    private lateinit var mProgressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

            _binding = FragmentBaseBinding.inflate(inflater, container, false)
            return binding.root
        }


        fun showProgressDialog(text: String){
            mProgressDialog = Dialog(requireActivity())

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

        override fun onDestroyView() {
            super.onDestroyView()

            _binding = null
        }
    }
