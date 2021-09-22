package com.example.happyfamilybox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Cart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class RateFarmerActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var rateFarmerAdapterActivity: RateFarmerAdapterActivity
    lateinit var msq : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_farmer)

        val fireStore = FirebaseFirestore.getInstance()



        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, HomePageCitizenActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)


        var list: MutableList<BookingCart> = mutableListOf()
        rateFarmerAdapterActivity = RateFarmerAdapterActivity(this, list)
        recyclerView.adapter = rateFarmerAdapterActivity
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchToDoList()
        msq.visibility = View.VISIBLE

    }


    private fun fetchToDoList() {
        doAsync {
            var list: MutableList<BookingCart> = mutableListOf()

            val fireStore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val email = auth.currentUser!!.email
            var count : Int = 0
            var map1 : HashMap<String, Boolean> = HashMap<String, Boolean> (1)
            map1[""]=true
            fireStore.collection("BookingCart").whereEqualTo("bookedby",email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            if(map1.containsKey(document.data.getValue("id_field").toString())==false) {
                                map1[document.data.getValue("id_field").toString()]=true
                                msq.visibility = View.GONE
                                list.add(document.toObject(BookingCart::class.java))
                                count = count + 1
                            }
                        }
//                        list=list.distinctBy { it.id_field }
                        if(count==0)
                        {
                            msq.visibility = View.VISIBLE
                            msq.setText("No history yet")
                        }
                        (recyclerView.adapter as RateFarmerAdapterActivity).notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Log.e("No","Error")
                    }

            uiThread {

                rateFarmerAdapterActivity.setList(list)
            }
        }
    }



}