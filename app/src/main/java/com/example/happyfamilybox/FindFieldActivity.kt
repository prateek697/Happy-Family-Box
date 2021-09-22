package com.example.happyfamilybox

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_find_field.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Math.*
import java.util.*

class FindFieldActivity : AppCompatActivity() {

    companion object {
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
    lateinit var findFieldAdapter: FindFieldAdapterActivity
    var userLatitude = ""
    var userLongitude = ""
    var addr = ""
    var our_distance: Int = 0
    var crop_user_enter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_field)

        fetch__location()

        var searchcard: CardView = findViewById(R.id.cardView)
        var detailscard: CardView = findViewById(R.id.cardView19)
        var ddetails: TextView = findViewById(R.id.details_after)

        detailscard.visibility = GONE
        searchcard.visibility = GONE

        val Cropradio: RadioButton = findViewById(R.id.cropradio)
        val Locationradio: RadioButton = findViewById(R.id.locationradio)
        val Bookradio : RadioButton = findViewById(R.id.bookradio)
        val Search_By_Crop: ImageView = findViewById(R.id.search_start)


        var list: MutableList<Fields> = mutableListOf()
        recyclerView = findViewById(R.id.recyclerHistory)
        findFieldAdapter = FindFieldAdapterActivity(this, list)
        recyclerView.adapter = findFieldAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        Cropradio.setOnClickListener {
            searchcard.visibility = VISIBLE
            detailscard.visibility = GONE
        }

        Locationradio.setOnClickListener {
            searchcard.visibility = VISIBLE
            detailscard.visibility = GONE
        }
        Bookradio.setOnClickListener {
            searchcard.visibility = VISIBLE
            detailscard.visibility = GONE
        }

        Search_By_Crop.setOnClickListener {

            val filled_crop: EditText = findViewById(R.id.fill_the_crop_name)
            val ccrop = filled_crop.text.toString()

            filled_crop.error = null
            if (TextUtils.isEmpty(ccrop)) {
                filled_crop.error = " Enter crop name!"
                return@setOnClickListener
            }


            searchcard.visibility = GONE
            detailscard.visibility = VISIBLE
            ddetails.setText("Crop Entered  :  " + ccrop)

            var temp=0

            if(Locationradio.isChecked) temp=30
            if(Cropradio.isChecked) temp=50
            if(Bookradio.isChecked) temp=100

            our_distance = temp.toInt() * 1000
            crop_user_enter = ccrop
            fetchToDoList_location()



        }

    }


    //Functions
    private fun fetch__location() {
        //Fetching Location
        if (ContextCompat.checkSelfPermission(
                        applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_REQUEST_CODE
            )
        } else {
            userLatitude = ""
            userLongitude = ""
            getCurrentLocation()
        }
        ///end
    }

    private fun isLocationEnabled(): Boolean {
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
        if (requestCode == REQUEST_PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
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
        var addresses: List<Address>

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
                .requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        super.onLocationResult(locationResult)
                        LocationServices.getFusedLocationProviderClient(this@FindFieldActivity)
                                .removeLocationUpdates(this)
                        if (locationResult != null && locationResult.locations.size > 0) {
                            var locIndex = locationResult.locations.size - 1

                            var latitude = locationResult.locations.get(locIndex).latitude
                            var longitude = locationResult.locations.get(locIndex).longitude
                            userLatitude = latitude.toString()
                            userLongitude = longitude.toString()

                            addresses = geocoder.getFromLocation(latitude, longitude, 1)

                            var address: String = addresses[0].getAddressLine(0)
                            addr = address
                        }
                    }
                }, Looper.getMainLooper())

    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / PI
    }


    private fun fetchToDoList_location() {
        doAsync {
            var list: MutableList<Fields> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()

            // list addind logic
            var x = 0.02 * our_distance
            fireStore.collection("Fields").get().addOnSuccessListener { documents ->
                var latt = 0.0
                var longg = 0.0
                var cur_latt = userLatitude.toDouble()
                var cur_longg = userLongitude.toDouble()
                for (document in documents) {
                    var str1 = document.data.getValue("latitude").toString()
                    str1 = str1.substring(11)
                    var str2 = document.data.getValue("longitude").toString()
                    str2 = str2.substring(11)
                    latt = str1.toDouble()
                    longg = str2.toDouble()

                    var distt = distance(cur_latt, cur_longg, latt, longg)

                    distt = distt * 1.60934
                    distt = distt * 1000

//                    Log.d("Diii", distt.toString())

                    if (distt <= x) {
                        var idd = document.data.getValue("id").toString()
                        fireStore.collection("Fields").document(idd).collection("Crops").get().addOnSuccessListener { dd1->
                            for( dd in dd1){
                                var nname = dd.data.getValue("cropname").toString()
                                if(nname==crop_user_enter){
                                    list.add(document.toObject(Fields::class.java))
                                    break
                                }
                            }

                        }

                    }
                }
                (recyclerView.adapter as FindFieldAdapterActivity).notifyDataSetChanged()
            }

                uiThread {
                    findFieldAdapter.setList(list)
                }

        }
    }
}

