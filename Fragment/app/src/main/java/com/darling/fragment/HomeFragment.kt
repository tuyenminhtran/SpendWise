package com.darling.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragment : Fragment(R.layout.fragment_home) {
    //bỏ tất cả các code tự generate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bổ sung hàm f
        val f = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString("username", "Tuyen")
            }
        }

        //nút btnGoProfile nằm ở HomeFragment
        val btnGo = view.findViewById<Button>(R.id.btnGoProfile)
        //bắt sự kiện click vào nút GoProfile
        btnGo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                //lưu ý có thay đổi ProfileFragment thành f
                .replace(R.id.fragmentContainer, f)
                .addToBackStack(null) // để bấm back quay lại Home
                .commit()
        }
    }
}