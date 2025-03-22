package com.Moter.autocare

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Moter.autocare.Activities.CarInsurance.InsuranceMenuActivity
import com.Moter.autocare.Activities.Dealing.DealingMenuActivity
import com.Moter.autocare.databinding.ActivityMainBinding
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Auto Care"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@MainActivity).load(imageUrl).into(binding.actionBar.profilePic)


        binding.insurence.setOnClickListener {
            startActivity(Intent(this@MainActivity,InsuranceMenuActivity::class.java))
        }

        binding.deal.setOnClickListener {
            startActivity(Intent(this@MainActivity,DealingMenuActivity::class.java))
        }


    }
}