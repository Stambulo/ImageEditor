package com.stambulo.milestone3.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<Binding: ViewBinding>
    (private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> Binding): Fragment() {

    private var _binding: Binding? = null
    val binding: Binding get() = _binding ?: throw NullPointerException()

    open fun Binding.initialize(){}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflateMethod.invoke(inflater, container, false)
        binding.initialize()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
