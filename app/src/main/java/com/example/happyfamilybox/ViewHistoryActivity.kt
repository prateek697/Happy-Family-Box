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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ViewHistoryActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var viewHistoryAdapterActivity: ViewHistoryAdapterActivity
    lateinit var msq : TextView
    lateinit var id_of_field : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_history)

        var unique_id = intent.extras?.get("field_ki_id") as String
        id_of_field=unique_id
        val fireStore = FirebaseFirestore.getInstance()



        val goback: ImageView = findViewById(R.id.backB)
        goback.setOnClickListener {
            val intent = Intent(this, RateFarmerActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        msq = findViewById(R.id.textView4)


        var list: MutableList<BookingCart> = mutableListOf()
        viewHistoryAdapterActivity = ViewHistoryAdapterActivity(this, list)
        recyclerView.adapter = viewHistoryAdapterActivity
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

            fireStore.collection("BookingCart").whereEqualTo("bookedby",email)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if(document.data.getValue("id_field").toString()==id_of_field) {
                            msq.visibility = View.GONE
                            list.add(document.toObject(BookingCart::class.java))
                            count = count + 1
                        }
                    }
//                        list=list.distinctBy { it.id_field }
                    if(count==0)
                    {
                        msq.visibility = View.VISIBLE
                        msq.setText("No booking from this farm yet")
                    }
                    (recyclerView.adapter as ViewHistoryAdapterActivity).notifyDataSetChanged()
                }
                .addOnFailureListener {
                    Log.e("No","Error")
                }

            uiThread {

                viewHistoryAdapterActivity.setList(list)
            }
        }
    }




}