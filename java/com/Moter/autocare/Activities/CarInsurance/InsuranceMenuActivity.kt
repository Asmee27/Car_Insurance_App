package com.Moter.autocare.Activities.CarInsurance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Moter.autocare.databinding.ActivityInsurenceMenuBinding
import com.bumptech.glide.Glide

class InsuranceMenuActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInsurenceMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsurenceMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Options"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@InsuranceMenuActivity).load(imageUrl).into(binding.actionBar.profilePic)

        
        binding.register.setOnClickListener{
            startActivity(Intent(this@InsuranceMenuActivity,CarRegisterActivity::class.java))
        }

        binding.buyInsurance.setOnClickListener{
            startActivity(Intent(this@InsuranceMenuActivity,BuyCarInsurance::class.java))
        }
    }


}