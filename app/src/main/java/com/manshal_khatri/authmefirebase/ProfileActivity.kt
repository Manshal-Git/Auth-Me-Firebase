package com.manshal_khatri.authmefirebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manshal_khatri.authmefirebase.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {


    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}