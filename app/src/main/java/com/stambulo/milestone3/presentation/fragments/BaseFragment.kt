package com.stambulo.milestone3.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.stambulo.milestone3.presentation.image.IImageLoader
import javax.inject.Inject

abstract class BaseFragment<Binding : ViewBinding> : Fragment() {

    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    private var _binding: Binding? = null
    val binding: Binding get() = _binding ?: throw NullPointerException()

    abstract fun inflateMethod(inflater: LayoutInflater, viewGroup: ViewGroup?): Binding
    abstract fun setupViewModel()
    abstract fun observeViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
