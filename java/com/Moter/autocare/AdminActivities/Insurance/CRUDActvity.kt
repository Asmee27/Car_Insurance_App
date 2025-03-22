package com.Moter.autocare.AdminActivities.Insurance

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.Moter.autocare.AdminActivities.Insurance.Add.AddInsuranceActivity
import com.Moter.autocare.AdminActivities.Insurance.Delete.DeleteInsurance
import com.Moter.autocare.AdminActivities.Insurance.Update.StateSpinnerActivity
import com.Moter.autocare.AdminActivities.Insurance.Update.UpdateInsurance
import com.Moter.autocare.databinding.ActivityCrudactvityBinding

class CRUDActvity : AppCompatActivity() {

    private lateinit var binding : ActivityCrudactvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityCrudactvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Select the Menu"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }

        binding.create.setOnClickListener{
            startActivity(Intent(this@CRUDActvity, AddInsuranceActivity::class.java))
        }

        binding.update.setOnClickListener{
            startActivity(Intent(this@CRUDActvity, StateSpinnerActivity::class.java)
                .putExtra("id","0")
            )
        }

        binding.delete.setOnClickListener{
            startActivity(Intent(this@CRUDActvity, StateSpinnerActivity::class.java)
                .putExtra("id","1")
            )
        }

        }
    }
