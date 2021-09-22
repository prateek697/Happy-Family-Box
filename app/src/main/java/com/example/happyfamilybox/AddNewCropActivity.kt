package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.happyfamilybox.models.Crops
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_new_crop.view.*
import java.util.*

class AddNewCropActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_crop)


        val months = resources.getStringArray(R.array.Months)
        val months1 = resources.getStringArray(R.array.Months)
        val spinner1 : Spinner = findViewById(R.id.sowing_time)
        val spinner2 : Spinner = findViewById(R.id.harvesting_time)

        var value1 : String = ""
        var value2 : String = ""

        if (spinner1 != null) {
            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_list_item_checked, months)
            spinner1.adapter = adapter

            spinner1.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                        value1 = months.get(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    value1="Please select an Option"
                }
            }
        }

        if (spinner2 !=  null) {
            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_list_item_checked, months1)
            spinner2.adapter = adapter

            spinner2.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    value2 = months1.get(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    value2="Please select an Option"
                }
            }
        }



        var uuid : UUID = UUID.randomUUID()
        var uuuuid : String = uuid.toString()

        var unique_id = intent.extras?.get("id_field") as String
        var emailId = intent.extras?.get("email") as String


        var id__ : TextView = findViewById(R.id.unique_ID)
        id__.setText("ID : " + unique_id)

        var ps11 : TextView = findViewById(R.id.crop_name)
        var ps22 : Spinner = findViewById(R.id.sowing_time)
        var ps33 : Spinner = findViewById(R.id.harvesting_time)
        var ps44 : TextView = findViewById(R.id.harvesting_time_taken)
        val signUpProgress: ProgressBar = findViewById(R.id.sign_up_progress)
        var sbutton : Button = findViewById(R.id.submit_button)
        sbutton.setOnClickListener {



            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email


            val ps1 = ps11.text.toString()
            val ps2 = value1
            val ps3 = value2
            val ps4 = ps44.text.toString()


            ps11.error=null
            ps44.error=null

            if (TextUtils.isEmpty(ps1)) {
                ps11.error = "Crop Name is required"
                return@setOnClickListener
            }
            if(ps2=="Please select an Option")
            {
                Toast.makeText(this, "Please select sowing month", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(ps3=="Please select an Option")
            {
                Toast.makeText(this, "Please select harvesting month", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(ps4)) {
                ps44.error = "This field is required"
                return@setOnClickListener
            }

            val newcrop = Crops(
                uuuuid,
                    unique_id,
                emailId,
                ps1,
                ps2,
                ps3,
                ps4
            )
            signUpProgress.visibility = View.VISIBLE
            fireStore.collection("Fields").document(unique_id).collection("Crops").document(uuuuid).set(newcrop)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show()
                    signUpProgress.visibility = View.GONE
                    val intent = Intent(this, EditCropSectionActivity::class.java)
                    intent.putExtra("id_field",unique_id)
                    intent.putExtra("email",emailId)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    signUpProgress.visibility = View.GONE
                    Toast.makeText(this, "Some Error Occured", Toast.LENGTH_LONG).show()
                    Log.e("No","Error")
                }




        }
    }
}