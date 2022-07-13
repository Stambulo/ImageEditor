package com.stambulo.milestone3.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

//TODO: base class should be abstract, not open (instance of this class does not make sense)
abstract class BaseFragment<Binding : ViewBinding>(
    //TODO: should not use lambda, use abstract function instead
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> Binding
) : Fragment() {

    private var _binding: Binding? = null
    val binding: Binding get() = _binding ?: throw NullPointerException()

    open fun Binding.initialize() {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)
        binding.initialize()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
