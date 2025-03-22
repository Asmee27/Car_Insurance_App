package com.Moter.autocare.Activities.Dealing.BuyCar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivitySelectRegionBinding
import com.bumptech.glide.Glide

class SelectRegionActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelectRegionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivitySelectRegionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Select the Region"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@SelectRegionActivity).load(imageUrl).into(binding.actionBar.profilePic)

        val adapter = ArrayAdapter.createFromResource(this@SelectRegionActivity, R.array.indian_states , android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.allStates.adapter = adapter

         binding.submit.setOnClickListener {
             val state = binding.allStates.selectedItem.toString()

             startActivity(Intent(this@SelectRegionActivity,ShowCarsActivity::class.java)
                 .putExtra("state",state)
             )
         }
    }
}