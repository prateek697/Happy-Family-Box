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
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Cart
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.text.DateFormat


import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CompletedBookingAdapterActivity(var context: Context, var ongoingList: MutableList<BookingCart>):
    RecyclerView.Adapter<CompletedBookingAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var id_ofField: TextView = itemView.findViewById(R.id.id_of_field)
        var Order_D: TextView = itemView.findViewById(R.id.order_details)
        var Add_D: TextView = itemView.findViewById(R.id.address_details)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        var fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("Fields").document(ongoingList[position].id_field).get()
            .addOnSuccessListener {
                holder.FieldName.text = it.getString("fieldname")
            }

        holder.id_ofField.text = "ID : " + ongoingList[position].id_field
        holder.Order_D.text = "Order Details : " + ongoingList[position].bookingdetails
        holder.Add_D.text = "Delivery Address : " + ongoingList[position].address



    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.completed_booking_card,parent,false)
        return DetailsViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<BookingCart>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

