package com.Moter.autocare.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.Moter.autocare.Activities.CarInsurance.InsuranceDealerActivity
import com.Moter.autocare.Model.CarRegisterModel
import com.Moter.autocare.R

open class BuyInsuranceAdapter(val context : Context, options: FirebaseRecyclerOptions<CarRegisterModel>) :
    FirebaseRecyclerAdapter<CarRegisterModel, BuyInsuranceAdapter.onViewHolder>(options) {

    class onViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val carModel : MaterialTextView = itemView.findViewById(R.id.carModel)
        val carBrand : MaterialTextView = itemView.findViewById(R.id.carName)
        val vechileType : MaterialTextView = itemView.findViewById(R.id.vechile_type)
        val layout : MaterialCardView = itemView.findViewById(R.id.layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.car_list_layout , parent , false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(holder: onViewHolder, position: Int, model: CarRegisterModel) {

        holder.carModel.text = model.carModel
        holder.carBrand.text = model.carBrand
        holder.vechileType.text = model.vechileType
        holder.layout.setOnClickListener {
            context.startActivity(Intent(context,InsuranceDealerActivity::class.java)
                .putExtra("carBrand",model.carBrand)
                .putExtra("userId",model.userId)
            )
        }

    }

}