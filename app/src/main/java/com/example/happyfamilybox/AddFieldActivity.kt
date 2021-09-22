package com.example.happyfamilybox

import java.util.UUID
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.Fields
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_field.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

@Suppress("DEPRECATION")
class AddFieldActivity : AppCompatActivity() {

    companion object{
        private val REQUEST_PERMISSION_REQUEST_CODE = 2020
    }

    class Run {
        companion object {
            fun after(delay: Long, process: () -> Unit) {
                Handler().postDelayed({
                    process()
                }, delay)
            }
        }
    }

    lateinit var recyclerView: RecyclerView

    var userLatitude = ""
    var userLongitude = ""

    lateinit var Lat : TextView
    lateinit var Lon : TextView
    lateinit var addr : TextView

    /*
    // Center coor of M1 Classroom
    var m1Latitude = 23.8365571
    var m1Longitude = 80.3879593

    var meters = 10
    // number of km per degree = ~111km (111.32 in google maps, but range varies between 110.567km at the equator and 111.699km at the poles)
    // 1km in degree = 1 / 111.32km = 0.0089
    // 1m in degree = 0.0089 / 1000 = 0.0000089
    var coef = meters * 0.0000089
    var new_lat_x1 = m1Latitude - coef
    var new_lat_x2 = m1Latitude + coef
    // pi / 180 = 0.018
    var new_long_y1 = m1Longitude - coef / cos(m1Longitude.toDouble()*0.018)
    var new_long_y2 = m1Longitude + coef / cos(m1Longitude.toDouble()*0.018)
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_field)
        var uniqueid : TextView = findViewById(R.id.unique_ID)
        var fieldname : TextView = findViewById(R.id.name_of_the_field)
        var fieldarea : TextView = findViewById(R.id.area_of_the_field)
        val auth =FirebaseAuth.getInstance()
        val signUpProgress: ProgressBar = findViewById(R.id.sign_up_progress)
        var uuid : UUID = UUID.randomUUID()
        uniqueid.setText(
                 uuid.toString()
        )
        Lat  = findViewById(R.id.tvLatitude)
        Lon  = findViewById(R.id.tvLongitude)
        addr = findViewById(R.id.tvAddress)
        var get_location : Button = findViewById(R.id.get_current_location)
        var submitB : Button = findViewById(R.id.submit_button)

        get_location.setOnClickListener {
            Lat.text = "Latitude :  Fetching.."
            Lon.text = "Longitude : Fetching.."
            addr.text = "Address :  Fetching.."
            //check permission
            if (ContextCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    , REQUEST_PERMISSION_REQUEST_CODE
                )
            } else {
                Lat.text = ""
                Lon.text = ""
                addr.text = ""
                userLatitude = ""
                if (isLocationEnabled()) {
                    Toast.makeText(this, "Fetching Location...", Toast.LENGTH_SHORT).show()
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Please Turn on Your device Location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        submitB.setOnClickListener {

            val user_email = auth.currentUser?.email.toString()
            val uniqq = uuid.toString()
            val FieldName = fieldname.text.toString()
            val FieldArea = fieldarea.text.toString()
            val latt = Lat.text.toString()
            val longg = Lon.text.toString()
            val adddrr = addr.text.toString()

            fieldname.error=null
            fieldarea.error=null
            Lat.error=null
            Lon.error=null
            addr.error=null

            if (TextUtils.isEmpty(FieldName)) {
                fieldname.error = "Field Name is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(FieldArea)) {
                fieldarea.error = "Field area is required"
                return@setOnClickListener
            }

            if (latt=="Latitude :  Click the button") {
                Lat.error = "Click the get current location button"
                return@setOnClickListener
            }

            if (longg=="Longitude : Click the button") {
                Lat.error = "Click the get current location button"
                return@setOnClickListener
            }

            if (adddrr=="Address :  Click the button") {
                addr.error = "Click the get current location button"
                return@setOnClickListener
            }

            var x = "0"
            val newfield = Fields(
                    uniqq,
                    user_email,
                    FieldName,
                    FieldArea,
                    latt,
                    longg,
                    adddrr,
                    x,
                    "0"
            )
            signUpProgress.visibility = View.VISIBLE
            val firestore = FirebaseFirestore.getInstance()
           firestore.collection("Fields").document(uniqq).set(newfield)
                   .addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           Toast.makeText(
                                   this,
                                   "SuccessFully Added",
                                   Toast.LENGTH_LONG
                           ).show()
                           signUpProgress.visibility = View.GONE
                           val intent = Intent(this, HomePageActivity::class.java)
                           startActivity(intent)
                           finish()
                       } else {
                           signUpProgress.visibility = View.GONE
                           Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                       }
                   }

        }

    }
    private fun isLocationEnabled():Boolean{
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE && grantResults.size > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation()
            }else{
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getCurrentLocation() {

        var locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        //now getting address from latitude and longitude

        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses:List<Address>

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,object : LocationCallback(){
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(this@AddFieldActivity)
                                .removeLocationUpdates(this)
                        if ( locationResult.locations.size > 0){
                            var locIndex = locationResult.locations.size-1

                            var latitude = locationResult.locations.get(locIndex).latitude
                            var longitude = locationResult.locations.get(locIndex).longitude
                            Lat.text = "Latitude :  "+latitude
                            userLatitude = latitude.toString()
                            Lon.text = "Longitude : "+longitude
                            userLongitude = longitude.toString()

                            addresses = geocoder.getFromLocation(latitude,longitude,1)

                            var address:String = addresses[0].getAddressLine(0)
                            addr.text = "Address :  "+address
                        }
                    }
                }, Looper.getMainLooper())

    }

}
