package com.Moter.autocare.Activities.CarInsurance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityCarRegisterBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class CarRegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCarRegisterBinding
    private var rcCopy : Uri? = null
    private var identityProof : Uri?=null
    private var addressProof : Uri?=null
    private var drivingLicense : Uri?=null
    private val carId = UUID.randomUUID().toString()
    private val documentId = UUID.randomUUID().toString()
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.actionBar.activityName.text = "Register Vehicle"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@CarRegisterActivity).load(imageUrl).into(binding.actionBar.profilePic)

        val carBrandAdapter = ArrayAdapter.createFromResource(this@CarRegisterActivity  ,  R.array.car_brands  , android.R.layout.simple_spinner_item )
        carBrandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.carBrand.adapter = carBrandAdapter

        val states = ArrayAdapter.createFromResource(this@CarRegisterActivity  ,  R.array.indian_states  , android.R.layout.simple_spinner_item )
        states.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.states.adapter = states


        val calendar: Calendar = Calendar.getInstance()
        val date: Date = calendar.getTime()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate: String = dateFormat.format(date)


        binding.rcUpload.setOnClickListener{
           openGallery(101)
        }

        binding.identityUpload.setOnClickListener{
            openGallery(102)
        }

        binding.addressUpload.setOnClickListener{
            openGallery(103)
        }

        binding.licUpload.setOnClickListener{
            openGallery(104)
        }

        val VehicleType = arrayOf("Sedan","Suv","PickUp","HatchBack","SportsCar","CompactSedan","Commercial Vehicle","Electric Vehicle","Hybrid","Jeep")
        val adapter = ArrayAdapter(this@CarRegisterActivity,android.R.layout.simple_spinner_item,VehicleType)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.vechileType.adapter = adapter

        binding.register.setOnClickListener {

            val ownerName = binding.ownerName.text.toString()
            val carBrand = binding.carBrand.selectedItem.toString()
            val carModel = binding.carModel.text.toString()
            val carNo = binding.carNo.text.toString()
            val registrationState = binding.states.selectedItem.toString()
            val vehicleType = binding.vechileType.selectedItem.toString()


            if(EmptyCheck(ownerName,carBrand,carModel,carNo,registrationState,vehicleType))
            {
                val loading = CustomDialog(this@CarRegisterActivity)
                loading.show()
                val map = HashMap<String,Any>()
                map.put("userId",auth.currentUser!!.uid)
                map.put("carId",carId)
                map.put("documentId",documentId)
                map.put("registrationDate",formattedDate)
                map.put("ownerName",ownerName)
                map.put("carBrand",carBrand)
                map.put("carModel",carModel)
                map.put("carNo",carNo)
                map.put("registrationState",registrationState)
                map.put("vechileType",vehicleType)
                database.reference.child("RegisteredCars").child(auth.currentUser!!.uid).child(carId).updateChildren(map)
                    .addOnCompleteListener{upload->

                        if (upload.isSuccessful)
                        {
                            Constants.success(this@CarRegisterActivity,"Car Registered Succfully")
                            loading.dismiss()
                            startActivity(Intent(this@CarRegisterActivity,InsuranceMenuActivity::class.java))
                            finish()
                        }
                        else
                        {
                            Constants.error(this@CarRegisterActivity,"Failed to register the car ${upload.exception!!.message}")
                            loading.dismiss()
                        }
                    }

            }
        }
    }

    private fun EmptyCheck(ownerName: String, carBrand: String, carModel: String, carNo: String, registrationState: String, vehicleType: String) : Boolean{

        if (ownerName.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"Please enter the OwnerName")
            return false
        }

        if (carBrand.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"Please enter the car Brand")
            return false
        }

        if (carModel.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"Please enter the car model")
            return false
        }

        if (carNo.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"Please enter the carNo")
            return false
        }

        if (registrationState.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"Please enter the state")
            return false
        }

        if (vehicleType.isEmpty())
        {
            Constants.error(this@CarRegisterActivity,"please select Vechile type")
            return false
        }
        if (rcCopy==null)
        {
            Constants.error(this@CarRegisterActivity,"Please select the RC Copy")
            return false
        }
        if (identityProof==null)
        {
            Constants.error(this@CarRegisterActivity,"Please select the identity proof")
            return false
        }
        if (addressProof==null)
        {
            Constants.error(this@CarRegisterActivity,"Please select the address proof")
            return false
        }
        if (drivingLicense==null)
        {
            Constants.error(this@CarRegisterActivity,"Please select the driving License")
            return false
        }

        return true
    }

    private fun openGallery(requestCode: Int) {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    private fun uploadDocument(documentName: String, uri: Uri , pathName : String) {
        val loading = CustomDialog(this@CarRegisterActivity)
        loading.show()
        val fileRef = storage.reference.child("CarDocuments").child(auth.currentUser!!.uid).child(documentId).child(documentName)
        fileRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val documentUrl = uri.toString()
                    val imageId = uri.toString()
                    val map = HashMap<String,Any>()
                    map.put(pathName,documentUrl)
                    database.reference.child("RegisteredCars").child(auth.currentUser!!.uid).child(carId)
                        .child("Documents").child(documentId).updateChildren(map)
                        .addOnCompleteListener{upload->

                            if (upload.isSuccessful)
                            {
                                Constants.success(this@CarRegisterActivity,"File Successfully uploaded")
                                loading.dismiss()
                            }
                            else
                            {
                             Constants.error(this@CarRegisterActivity,"Failed to store documents ${upload.exception!!.message}")
                             loading.dismiss()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Constants.error(this@CarRegisterActivity,"Failed to upload documents ${exception.message}")
                loading.dismiss()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            rcCopy = data.data
            val filename = getFileName(rcCopy!!)
            binding.rcCopy.setText(filename)
            uploadDocument(filename.toString(),rcCopy!!,"rcCopy")

        }

        if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            identityProof = data.data
            val filename = getFileName(identityProof!!)
            binding.proofIdentity.setText(filename)
            uploadDocument(filename.toString(),identityProof!!,"identityProof")
        }

        if (requestCode == 103 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            addressProof = data.data
            val filename = getFileName(addressProof!!)
            binding.addressProof.setText(filename)
            uploadDocument(filename.toString(),addressProof!!,"addressProof")

        }

        if (requestCode == 104 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            drivingLicense = data.data
            val filename = getFileName(drivingLicense!!)
            binding.drivingLicience.setText(filename)
            uploadDocument(filename.toString(),drivingLicense!!,"drivingLicense")
        }
    }


    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

}