package com.Moter.autocare.Activities.Dealing

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Moter.autocare.Activities.Dealing.BuyCar.SelectRegionActivity
import com.Moter.autocare.Activities.Dealing.SellCar.AddCarForSellActivity
import com.Moter.autocare.databinding.ActivityDealingMenuBinding
import com.bumptech.glide.Glide

class DealingMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDealingMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDealingMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Options"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@DealingMenuActivity).load(imageUrl).into(binding.actionBar.profilePic)

        binding.buyCar.setOnClickListener {
            startActivity(Intent(this@DealingMenuActivity,SelectRegionActivity::class.java))
        }

        binding.sellCar.setOnClickListener {
            startActivity(Intent(this@DealingMenuActivity , AddCarForSellActivity::class.java))
        }

    }
}