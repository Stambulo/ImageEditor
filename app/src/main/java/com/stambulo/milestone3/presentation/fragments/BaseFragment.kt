package com.stambulo.milestone3.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<Binding : ViewBinding>(
) : Fragment() {

    private var _binding: Binding? = null
    val binding: Binding get() = _binding ?: throw NullPointerException()

    //TODO: this fun should not be open, but abstract
    open fun Binding.initialize() {}

    //TODO: instead of lambda
    abstract fun inflateMethod(inflater: LayoutInflater, viewGroup: ViewGroup?) : Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod(inflater, container)
        binding.initialize()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
