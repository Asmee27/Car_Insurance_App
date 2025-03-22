package com.Moter.autocare.Activities.Dealing.BuyCar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityContactBinding
import com.bumptech.glide.Glide

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.activityName.text = "Contact Us"
        binding.actionBar.backButton.setOnClickListener {
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@ContactActivity).load(imageUrl).into(binding.actionBar.profilePic)

        binding.whatsapp.setOnClickListener {
            val phoneNumber = "7517915400"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$phoneNumber")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Constants.error(this, "WhatsApp is not installed")
            }
        }


        binding.email.setOnClickListener {
            openEmailApp("insuranceapp268@gmail.com", "Need Car", "Hello")
        }

        binding.phone.setOnClickListener {
            val phoneNumber = "7517915400"
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            if (dialIntent.resolveActivity(packageManager) != null) {
                startActivity(dialIntent)
            }
        }
    }

    private fun openEmailApp(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

}
