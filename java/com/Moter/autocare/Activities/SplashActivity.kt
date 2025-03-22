package com.Moter.autocare.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.Moter.autocare.Authentication.Login
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Model.UserModel
import com.Moter.autocare.databinding.ActivitySplashBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import com.permissionx.guolindev.PermissionX

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        Handler().postDelayed(object : Runnable{
            override fun run() {
                PermissionX.init(this@SplashActivity)
                    .permissions(Manifest.permission.CALL_PHONE , Manifest.permission.SEND_SMS)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            startActivity(Intent(this@SplashActivity,Login::class.java))
                            finish()
                        } else {
                            Constants.error(this@SplashActivity,"Please give the necessary Permission $deniedList")
                        }
                    }
            }

        },1000)
    }
}