package com.Moter.autocare.Activities.Dealing.SellCar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.Moter.autocare.Constants.Constants
import com.Moter.autocare.Constants.CustomDialog
import com.Moter.autocare.R
import com.Moter.autocare.databinding.ActivityAddCarForSellBinding
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.annotations.Nullable
import java.util.UUID


class AddCarForSellActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddCarForSellBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var auth : FirebaseAuth
    private var imageUri: Uri? = null
    private val CarId = UUID.randomUUID().toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCarForSellBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.actionBar.activityName.text = "Sell Car"
        binding.actionBar.backButton.setOnClickListener{
            finish()
        }
        val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("imageUrl", "")
        Glide.with(this@AddCarForSellActivity).load(imageUrl).into(binding.actionBar.profilePic)

        val brandAdapter = ArrayAdapter.createFromResource(this@AddCarForSellActivity,R.array.car_brands,android.R.layout.simple_spinner_item)
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.carBrand.adapter = brandAdapter


        val statesAdapter = ArrayAdapter.createFromResource(this@AddCarForSellActivity,R.array.indian_states,android.R.layout.simple_spinner_item)
        statesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.states.adapter = statesAdapter

        val ownerTypeAdapter= ArrayAdapter.createFromResource(this@AddCarForSellActivity,R.array.owner_Type,android.R.layout.simple_spinner_item)
        ownerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.ownerType.adapter = ownerTypeAdapter

        val fuelTypeAdapter = ArrayAdapter.createFromResource(this@AddCarForSellActivity,R.array.fuelType,android.R.layout.simple_spinner_item)
        fuelTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.fuelType.adapter = fuelTypeAdapter


        binding.choose.setOnClickListener {
            chooseMultipleImages()
        }

        binding.register.setOnClickListener {

            val carName = binding.carName.text.toString()
            val year = binding.year.text.toString()
            val kilometer = binding.kilometerDriven.text.toString()
            val carNo = binding.vechileNum.text.toString()
            val price = binding.price.text.toString()
            val carBrand = binding.carBrand.selectedItem.toString()
            val state = binding.states.selectedItem.toString()
            val ownerType = binding.ownerType.selectedItem.toString()
            val fuelType =  binding.fuelType.selectedItem.toString()

            if (validateInput(carName,year,kilometer,carNo,price,carBrand,state,ownerType,fuelType))
            {
                val loading = CustomDialog(this@AddCarForSellActivity)
                loading.show()

                val map = HashMap<String, Any>()
                map.put("carName",carName)
                map.put("year",year)
                map.put("kilometerDriven",kilometer)
                map.put("vehicleNo",carNo)
                map.put("price",price)
                map.put("carBrand",carBrand)
                map.put("registrationState",state)
                map.put("ownerType",ownerType)
                map.put("fuelType",fuelType)
                map.put("carId",CarId)
                map.put("userId",auth.currentUser!!.uid)

                database.reference.child("SellingCars")
                    .child(binding.states.selectedItem.toString()).child(CarId).updateChildren(map)
                    .addOnCompleteListener{task->
                        if (task.isSuccessful)
                        {
                            Constants.success(this@AddCarForSellActivity,"Car has successfully register")
                            loading.dismiss()
                            onBackPressed()
                        }
                        else{
                            Constants.error(this@AddCarForSellActivity,"Failed to register the car")
                            loading.dismiss()
                        }
                    }
            }

        }

    }

    private fun validateInput(
        carName: String,
        year: String,
        kilometer: String,
        carNo: String,
        price: String,
        carBrand: String,
        state: String,
        ownerType: String,
        fuelType: String
    ) : Boolean {

        if (carName.isEmpty()&&year.isEmpty()&& kilometer.isEmpty() && carNo.isEmpty() && price.isEmpty() && carBrand.isEmpty()
            && state.isEmpty() && ownerType.isEmpty() && fuelType.isEmpty())
        {
            Constants.error(this@AddCarForSellActivity,"Please fill all the credentials and try again")
            return false
        }

        if (imageUri==null)
        {
            Constants.error(this@AddCarForSellActivity,"Please select the images for the car")
            return false
        }



        return true
    }

    private fun chooseMultipleImages() {
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    imageUri = data.clipData!!.getItemAt(i).uri
                    uploadImageToFirebase(imageUri!!)
                }
            } else if (data.data != null) {
                val imageUri = data.data
                uploadImageToFirebase(imageUri!!)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {

        val loading = CustomDialog(this@AddCarForSellActivity)
        loading.show()

        val fileReference: StorageReference = storage.reference.child("SellCarImages").child(auth.currentUser!!.uid).child(CarId).child(UUID.randomUUID().toString())
        fileReference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    saveImageUrlToDatabase(imageUrl , loading)
                }
            }
            .addOnFailureListener { e ->
               Constants.error(
                    this@AddCarForSellActivity,
                    "Upload failed: " + e.message)
                loading.dismiss()
            }
    }

    private fun saveImageUrlToDatabase(imageUrl: String , loading : CustomDialog) {
        var imageId = UUID.randomUUID().toString()
        val map = HashMap<String ,Any>()
        map.put("imageId",imageId)
        map.put("Url",imageUrl)

        val imagesRef: DatabaseReference = database.reference.child("SellingCars")
            .child(binding.states.selectedItem.toString()).child(CarId).child("Images").child(imageId)
        imagesRef.updateChildren(map)
            .addOnSuccessListener {
               Constants.success(
                    this@AddCarForSellActivity,
                    "Image uploaded successfully")
                loading.dismiss()
            }
            .addOnFailureListener {
              Constants.error(
                    this@AddCarForSellActivity,
                    "Failed to upload image URL to database")
                loading.dismiss()
            }
    }

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }


}