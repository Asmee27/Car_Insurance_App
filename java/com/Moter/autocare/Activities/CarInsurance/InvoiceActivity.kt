package com.Moter.autocare.Activities.CarInsurance

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.Moter.autocare.databinding.ActivityInvoiceBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class InvoiceActivity : AppCompatActivity() {

    private lateinit var binding : ActivityInvoiceBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Invoice"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@InvoiceActivity).load(imageUrl).into(binding.actionBar.profilePic)


        val intent = intent
        val companyName = intent.getStringExtra("companyName").toString()
        val paymentId = intent.getStringExtra("paymentId").toString()
        val amount = intent.getStringExtra("amount").toString()
        val address = intent.getStringExtra("address").toString()


        binding.companyNameTextView.text = companyName
        binding.invoiceNo.text = UUID.randomUUID().toString()
        binding.paymentId.text = paymentId
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        binding.date.text = formattedDate
        binding.address.text = address
        binding.amount.text = "$amount INR"
    }
}