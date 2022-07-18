package com.stambulo.milestone3.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stambulo.milestone3.databinding.FragmentImageEditingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageEditingFragment : BaseFragment<FragmentImageEditingBinding>()  {

    private lateinit var imageName: String

    override fun inflateMethod(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): FragmentImageEditingBinding {
        return FragmentImageEditingBinding.inflate(inflater, viewGroup, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageName = arguments?.getString("imageName") ?: "Zero"
        Log.i(">>>", "File Name - $imageName")
    }
}
