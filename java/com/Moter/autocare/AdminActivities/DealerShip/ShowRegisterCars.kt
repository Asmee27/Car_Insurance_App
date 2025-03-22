package com.Moter.autocare.AdminActivities.DealerShip

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Moter.autocare.Adapter.DeleteDealerAdapter
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.Constants.WrapContentLinearLayoutManager
import com.Moter.autocare.Model.SellCarModel
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityShowRegisterCarsBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
class ShowRegisterCars : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityShowRegisterCarsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var deleteAdapter: DeleteDealerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowRegisterCarsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.actionBar.activityName.text = "Delete Dealers"
        binding.actionBar.backButton.setOnClickListener {
            finish()
        }

        val adapter = ArrayAdapter.createFromResource(
            this@ShowRegisterCars,
            R.array.indian_states,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.state.adapter = adapter

        binding.recView.layoutManager = WrapContentLinearLayoutManager(this@ShowRegisterCars)

        binding.select.setOnClickListener{
            val loading  = CustomDialog(this@ShowRegisterCars)
            loading.show()
            val state = binding.state.selectedItem.toString()
            val query = database.reference.child("SellingCars").child(state)
            val options = FirebaseRecyclerOptions.Builder<SellCarModel>()
                .setQuery(query, SellCarModel::class.java)
                .build()
            deleteAdapter = object : DeleteDealerAdapter(this@ShowRegisterCars, options)
            {
                override fun onDataChanged() {
                    super.onDataChanged()
                    loading.dismiss()
                }
            }
            binding.recView.adapter = deleteAdapter
            deleteAdapter.startListening()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Not implemented
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Not implemented
    }
}