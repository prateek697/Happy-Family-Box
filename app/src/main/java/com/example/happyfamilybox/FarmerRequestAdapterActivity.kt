package com.example.happyfamilybox

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.happyfamilybox.R
import com.example.happyfamilybox.models.BookedFields
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.text.DateFormat


import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class FarmerRequestAdapterActivity(var context: Context, var ongoingList: MutableList<Cart>):
    RecyclerView.Adapter<FarmerRequestAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var id_ofField: TextView = itemView.findViewById(R.id.id_of_field)
        var booked__for: TextView = itemView.findViewById(R.id.booked_for_details)
        var area__of: TextView = itemView.findViewById(R.id.area_requested)
        var acceptButton : Button = itemView.findViewById(R.id.accept_button)
        var rejecttButton : Button = itemView.findViewById(R.id.reject_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").document(ongoingList[position].id_field).get()
            .addOnSuccessListener {
                holder.FieldName.text = it.getString("fieldname")
            }

        holder.id_ofField.text = "ID : " + ongoingList[position].id_field
        holder.booked__for.text = ongoingList[position].validity + " Months"
        holder.area__of.text = "Area Requested : " + ongoingList[position].areareserved



        var count_area_of_reserved_fields : Int = 0
        var ids: MutableList<String> = mutableListOf()
        val fireSTOre = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser!!.email
        var fireSTore = FirebaseFirestore.getInstance().collection("BookedFields")
        var map_for_area_details = HashMap<String,Int>()

        fireSTOre.collection("Fields").whereEqualTo("email",email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    ids.add(document.data.getValue("id").toString())
                    map_for_area_details.put(document.data.getValue("id").toString(),document.data.getValue("fieldarea").toString().toInt())
                }
            }
            .addOnFailureListener {
                Log.e("No","Error")
            }


        fireSTore.get()
            .addOnSuccessListener {documents->
                for(document in documents)
                {
                    var field__id = document.data.getValue("id_field").toString()
                    var flag : Boolean  = false
                    for(i in ids.indices)
                    {
                        if(ids[i]==field__id)
                        {
                            flag=true
                            break
                        }
                    }
                    if(flag==true)
                    {
                        map_for_area_details.put(field__id,
                            map_for_area_details[field__id]!! - document.data.getValue("areareserved").toString().toInt())
                    }
                }
            }


        holder.acceptButton.setOnClickListener {

            var firestore = FirebaseFirestore.getInstance().collection("Cart").document(ongoingList[position].id)
            var fireStore = FirebaseFirestore.getInstance().collection("BookedFields")

            var calender : Calendar = Calendar.getInstance()
            var currdate : String = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(calender.getTime())
            val cartitem = BookedFields(
                ongoingList[position].id,
                ongoingList[position].id_field,
                ongoingList[position].email_owner,
                ongoingList[position].bookedby,
                ongoingList[position].validity,
                ongoingList[position].areareserved,
                currdate
            )
            AlertDialog.Builder(context)
                .setTitle("Accept Request")
                .setMessage("Are you sure you want to Accept this Request ?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    if (map_for_area_details[ongoingList[position].id_field]!! < ongoingList[position].areareserved.toInt()) {
                        Toast.makeText(
                            context,
                            "You do not have enough land for this",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else
                    {
                    fireStore.document(ongoingList[position].id).set(cartitem)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firestore.delete()
                                    .addOnSuccessListener {
                                        var poss: Int = position
                                        Toast.makeText(
                                            context,
                                            "Successfully Accepted",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        ongoingList.removeAt(poss)
                                        notifyDataSetChanged()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Some Error Occured",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                            } else {
                                Toast.makeText(context, "Some Error Occured", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                }
                .setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()

        }

        holder.rejecttButton.setOnClickListener {
            var firestore = FirebaseFirestore.getInstance().collection("Cart").document(ongoingList[position].id)
            AlertDialog.Builder(context)
                .setTitle("Reject Request")
                .setMessage("Are you sure you want to reject this Request?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    firestore.delete()
                        .addOnSuccessListener {
                            var poss : Int = position
                            Toast.makeText(context, "Request removed from your request section", Toast.LENGTH_LONG).show()
                            ongoingList.removeAt(poss)
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                        }
                }
                .setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.requests_to_farmer_card,parent,false)
        return DetailsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<Cart>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

