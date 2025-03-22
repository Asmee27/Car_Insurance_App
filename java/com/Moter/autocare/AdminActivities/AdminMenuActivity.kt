package com.Moter.autocare.AdminActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Moter.autocare.AdminActivities.Insurance.Add.AddInsuranceActivity
import com.Moter.autocare.AdminActivities.DealerShip.ShowRegisterCars
import com.Moter.autocare.AdminActivities.Insurance.CRUDActvity
import com.Moter.autocare.databinding.ActivityAdminMenuBinding

class AdminMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Admin Menu "
        binding.actionBar.backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addInsurance.setOnClickListener {
            startActivity(Intent(this@AdminMenuActivity, CRUDActvity::class.java))
        }

        binding.dealRegisterCars.setOnClickListener{
            startActivity(Intent(this@AdminMenuActivity,ShowRegisterCars::class.java))
        }

    }
}