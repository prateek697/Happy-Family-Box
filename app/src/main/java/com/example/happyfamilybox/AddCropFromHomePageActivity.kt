package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddCropFromHomePageActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var msq : TextView
    lateinit var addCropFromHomePageAdapterActivity: AddCropFromHomePageAdapterActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_crop_from_home_page)

        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)

        var list: MutableList<Fields> = mutableListOf()
        addCropFromHomePageAdapterActivity = AddCropFromHomePageAdapterActivity(this, list)
        recyclerView.adapter = addCropFromHomePageAdapterActivity
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchToDoList()

        msq.visibility= View.VISIBLE
    }


    private fun fetchToDoList() {
        doAsync {
            var list: MutableList<Fields> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0

            fireStore.collection("Fields")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        msq.visibility = View.GONE
                        var whattocheck  = document.data.getValue("email").toString()
                        if(whattocheck==email) {
                            list.add(document.toObject(Fields::class.java))
                            count = count + 1
                        }
                    }
                    if(count==0)
                    {
                        msq.visibility = View.VISIBLE
                        msq.setText("No Fields")
                    }
                    (recyclerView.adapter as AddCropFromHomePageAdapterActivity).notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.e("No","Error")
                }

            uiThread {

                addCropFromHomePageAdapterActivity.setList(list)
            }
        }
    }



}