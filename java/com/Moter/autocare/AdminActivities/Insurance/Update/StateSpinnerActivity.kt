package com.Moter.autocare.AdminActivities.Insurance.Update

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Moter.autocare.AdminActivities.Insurance.Delete.DeleteInsurance
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityStateSpinnerBinding

class StateSpinnerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityStateSpinnerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateSpinnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Select the Car brand"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        val intent = intent
        val id = intent.getStringExtra("id").toString()

        val adapter = ArrayAdapter.createFromResource(this@StateSpinnerActivity,R.array.car_brands,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.submit.setOnClickListener{
            val carbrand = binding.spinner.selectedItem.toString()

            if (id.equals("0"))
            {
                startActivity(Intent(this@StateSpinnerActivity,UpdateInsurance::class.java)
                    .putExtra("carBrand",carbrand)
                )
            }
            if (id.equals("1"))
            {
                startActivity(Intent(this@StateSpinnerActivity,DeleteInsurance::class.java)
                    .putExtra("carBrand",carbrand)
                )
            }


        }

    }
}