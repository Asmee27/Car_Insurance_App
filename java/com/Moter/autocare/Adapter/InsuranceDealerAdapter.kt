package com.Moter.autocare.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.Moter.autocare.Activities.CarInsurance.PaymentIntegration
import com.Moter.autocare.Model.AddInsuranceModel
import com.Moter.autocare.R

open class InsuranceDealerAdapter(val context : Context , options: FirebaseRecyclerOptions<AddInsuranceModel> , val userId : String) :
    FirebaseRecyclerAdapter<AddInsuranceModel, InsuranceDealerAdapter.onViewHolder>(options) {

    class onViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val companyName : MaterialTextView = itemView.findViewById(R.id.companyName)
        val carName : MaterialTextView = itemView.findViewById(R.id.carName)
        val carModel : MaterialTextView = itemView.findViewById(R.id.carModel)
        val layout : MaterialCardView = itemView.findViewById(R.id.layout)
         val standard : MaterialButton = itemView.findViewById(R.id.standardPlan)
         val premium : MaterialButton = itemView.findViewById(R.id.premiumPlan)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): onViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.insurance_dealer_layout , parent , false)
        return onViewHolder(view)
    }

    override fun onBindViewHolder(holder: onViewHolder, position: Int, model: AddInsuranceModel) {

        holder.companyName.text = model.companyName
        holder.carName.text = model.carName
        holder.carModel.text = model.carModel
        holder.standard.setText("Standard ₹ ${model.standardInsurance}")
        holder.standard.setOnClickListener {
            context.startActivity(Intent(context,PaymentIntegration::class.java)
                .putExtra("planName","Standard Plan")
                .putExtra("amount" , model.standardInsurance)
                .putExtra("dealerId",model.dealerId)
                .putExtra("carBrand",model.carName)
                .putExtra("userId",userId)
            )
        }

        holder.premium.setText("Premium ₹ ${model.premiumInsurance}")
        holder.premium.setOnClickListener {
            context.startActivity(Intent(context,PaymentIntegration::class.java)
                .putExtra("planName","Premium Plan")
                .putExtra("amount" , model.premiumInsurance)
                .putExtra("dealerId",model.dealerId)
                .putExtra("carBrand",model.carName)
                .putExtra("userId",userId)
            )
        }


    }
}