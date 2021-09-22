package com.example.happyfamilybox
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomePageActivity : AppCompatActivity() {
    companion object{
        private val REQUEST_PERMISSION_REQUEST_CODE = 2020
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        //Type Part done
        val textview3: TextView = findViewById(R.id.textView3)
        textview3.setText("Farmer")
        var aauth =FirebaseAuth.getInstance()
        val fireStore = FirebaseFirestore.getInstance()
        var emailll = aauth.currentUser?.email.toString()
        fireStore.collection("Users").whereEqualTo("email",emailll).get()
                .addOnSuccessListener { documents ->

                    for(document in documents) {
                        textview3.setText(document.data.getValue("name").toString())
                    }

                }
        //edit profile done
        val editProfile: Button = findViewById(R.id.editProfileB)
        //logout done
        val logout: ImageView = findViewById(R.id.logout)


        val yourfieldsuser: CardView = findViewById(R.id.your_fields_users)
        yourfieldsuser.setOnClickListener {
            val intent = Intent(this,YourBookingsActivity::class.java)
            startActivity(intent)
        }
        val addCropoption: CardView = findViewById(R.id.add_crop_option)
        addCropoption.setOnClickListener {
            val intent = Intent(this,AddCropFromHomePageActivity::class.java)
            startActivity(intent)
        }
        val yourfields: Button = findViewById(R.id.todoB)
        val requets_section: CardView = findViewById(R.id.request_option)

        val addfield: CardView = findViewById(R.id.add_field)
        addfield.setOnClickListener {
            //check permission
            if(!isLocationEnabled()){
                Toast.makeText(this,"Please Turn on Your device Location",Toast.LENGTH_SHORT).show()
            }
            else {
                if (ContextCompat.checkSelfPermission(
                        applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        , HomePageActivity.REQUEST_PERMISSION_REQUEST_CODE
                    )
                } else {
                    val intent = Intent(
                        this,
                        AddFieldActivity::class.java
                    )
                    startActivity(intent)

                }
            }
        }



        val auth = FirebaseAuth.getInstance()

        requets_section.setOnClickListener {
            val intent = Intent(this,FarmerRequestActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        yourfields.setOnClickListener {
            val intent = Intent(this,YourFieldActivity::class.java)
            startActivity(intent)
        }
        logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }
    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }
}