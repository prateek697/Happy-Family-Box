package com.example.happyfamilybox

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class CitizenCartAdapterActivity(var context: Context, var ongoingList: MutableList<Cart>):
    RecyclerView.Adapter<CitizenCartAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var request_: TextView = itemView.findViewById(R.id.request)
        var booked__for: TextView = itemView.findViewById(R.id.booked_for_details)
        var area__of: TextView = itemView.findViewById(R.id.area_requested)
        var delete_but : ImageView = itemView.findViewById(R.id.delete_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").document(ongoingList[position].id_field).get()
            .addOnSuccessListener {
                holder.FieldName.text = it.getString("fieldname")
            }

        holder.request_.text = "Request : Pending"
        holder.booked__for.text = ongoingList[position].validity + " Months"
        holder.area__of.text = "Area Requested : " + ongoingList[position].areareserved




        holder.delete_but.setOnClickListener {

            var firestore = FirebaseFirestore.getInstance().collection("Cart").document(ongoingList[position].id)
            AlertDialog.Builder(context)
                .setTitle("Delete item")
                .setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes") { dialogInterface, i ->
                    firestore.delete()
                        .addOnSuccessListener {
                            var poss : Int = position
                            Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_LONG).show()
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.your_cart_card,parent,false)
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

