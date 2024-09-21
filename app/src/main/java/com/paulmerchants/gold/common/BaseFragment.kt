package com.paulmerchants.gold.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


open class BaseFragment<T : ViewBinding>(private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> T) :
    Fragment() {

    private var _binding: T? = null

    // This can be accessed by the child fragments
    // Only valid between onCreateView and onDestroyView
    val binding: T get() = _binding!!

    // Make it open, so it can be overridden in child fragments
    open fun T.initialize() {

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)

        // Calling the extension function
        binding.initialize()
        // replaced _binding!! with binding
        return binding.root
    }

}