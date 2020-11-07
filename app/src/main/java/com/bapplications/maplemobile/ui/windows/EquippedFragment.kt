package com.bapplications.maplemobile.ui.windows

import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.ui.view_models.EquippedViewModel
import com.bapplications.maplemobile.databinding.FragmentEquippedBinding

class EquippedFragment(val player: Player) : Fragment() {

    private val viewModel: EquippedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentEquippedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_equipped, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner { lifecycle }

        viewModel.setEquippedInventory(player.getEquippedInventory())
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(player: Player) = EquippedFragment(player)
    }
}