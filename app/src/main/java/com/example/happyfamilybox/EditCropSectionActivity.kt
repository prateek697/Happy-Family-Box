package com.example.happyfamilybox


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.Crops
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditCropSectionActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var editcropSectionAdapter: EditCropSectionAdapterActivity
    lateinit var id_u : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_crop_section)
        var ttextt : TextView = findViewById(R.id.text)
        var addNewCrop : Button = findViewById(R.id.add_new_crop)
        val goback: ImageView = findViewById(R.id.backB)
        val auth = FirebaseAuth.getInstance()
        var type = ""
        val fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    type = it.getString("type").toString()
                    if (type == "Citizen") {
                        ttextt.setText("All Crops")
                        goback.visibility = View.GONE
                        addNewCrop.visibility = View.GONE
                    }
                }

        var unique_id = intent.extras?.get("id_field") as String
        var emailId = intent.extras?.get("email") as String


        id_u = unique_id
        Log.e("MILL",id_u)




        goback.setOnClickListener {
            if(type=="Farmer") {
                val intent = Intent(this, YourFieldActivity::class.java)
                startActivity(intent)
            }
            else
            {
                val intent = Intent(this, HomePageCitizenActivity::class.java)
                startActivity(intent)
            }
        }


        addNewCrop.setOnClickListener {
            val intent = Intent(this,AddNewCropActivity::class.java)
            intent.putExtra("id_field",unique_id)
            intent.putExtra("email",emailId)
            startActivity(intent)
        }

        var list: MutableList<Crops> = mutableListOf()
        recyclerView = findViewById(R.id.recyclerHistory)
        editcropSectionAdapter = EditCropSectionAdapterActivity(this, list)
        recyclerView.adapter = editcropSectionAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchToDoList()


    }
    private fun fetchToDoList() {
        doAsync {
            var list: MutableList<Crops> = mutableListOf()

            var fireStore = FirebaseFirestore.getInstance()
            var auth = FirebaseAuth.getInstance()
            var email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("Fields").document(id_u).collection("Crops").get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.e("MILL",id_u)
                            list.add(document.toObject(Crops::class.java))
                            Log.e("Document",list[count].cropname)
                            count=count+1
                        }
                        (recyclerView.adapter as EditCropSectionAdapterActivity).notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Log.e("No","Error")
                    }

            uiThread {
                editcropSectionAdapter.setList(list)
            }
        }
    }


}