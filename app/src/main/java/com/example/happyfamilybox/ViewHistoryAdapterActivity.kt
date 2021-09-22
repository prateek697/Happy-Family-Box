package com.example.happyfamilybox

import android.app.AlertDialog
import android.content.ContentValues.TAG
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
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.example.happyfamilybox.R
import com.example.happyfamilybox.models.BookingCart
import com.example.happyfamilybox.models.Crops
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.forgot_password.view.*
import kotlinx.android.synthetic.main.forgot_password.view.submit_button
import kotlinx.android.synthetic.main.rating_giving_screen.view.*
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class ViewHistoryAdapterActivity(var context: Context, var ongoingList: MutableList<BookingCart>):
    RecyclerView.Adapter<ViewHistoryAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var Rating_or_not : TextView = itemView.findViewById(R.id.rated_or_not)
        var Order_det : TextView = itemView.findViewById(R.id.order_details)
        var Add_det : TextView = itemView.findViewById(R.id.address_details)
        var Rating_button : ImageView = itemView.findViewById(R.id.rating_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        if(ongoingList[position].flag.toInt()!=-1)
        {
            holder.Rating_button.visibility=View.GONE
            holder.Rating_or_not.text = "Rated : " + ongoingList[position].flag + "/" + "5"
        }
        holder.Add_det.text = ongoingList[position].address
        holder.Order_det.text = ongoingList[position].bookingdetails
        var firestore = FirebaseFirestore.getInstance()
        var f_id : String=""
        var f_email: String = ""
        var f_f_fieldname: String = ""
        var f_fieldarea: String = ""
        var f_latitude: String = ""
        var f_longitude: String = ""
        var f_address: String = ""
        var f_rating: String =""
        var f_count: String =""

        firestore.collection("Fields").document(ongoingList[position].id_field).get().addOnSuccessListener {
            f_id = it.getString("id").toString()
            f_email = it.getString("email").toString()
            f_address = it.getString("address").toString()
            f_count = it.getString("count").toString()
            f_rating = it.getString("rating").toString()
            f_fieldarea = it.getString("fieldarea").toString()
            f_latitude = it.getString("latitude").toString()
            f_longitude = it.getString("longitude").toString()
            f_f_fieldname = it.getString("fieldname").toString()
        }

        holder.Rating_button.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.rating_giving_screen,null);
            val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
//                .setTitle("Change Password")
            val mAlertDialog = mBuilder.show()
            mDialogView.submit_button.setOnClickListener {
                mAlertDialog.dismiss()
                val rrating = mDialogView.rBar.rating.toInt().toString()

                var prev_rating = 0
                var cc=0
                prev_rating = f_rating.toInt() + rrating.toInt()
                f_rating = prev_rating.toString()
                cc = f_count.toInt()+1
                f_count=cc.toString()

                val updateitem = BookingCart(
                        ongoingList[position].id,
                        ongoingList[position].id_field,
                        ongoingList[position].email_owner,
                        ongoingList[position].bookedby,
                        ongoingList[position].bookingdetails,
                        ongoingList[position].totalweight,
                        rrating,
                        "Accepted",
                        rrating,
                        ongoingList[position].address
                )

                var nnnewitem = Fields(
                        f_id,
                        f_email,
                        f_f_fieldname,
                        f_fieldarea,
                        f_latitude,
                        f_longitude,
                        f_address,
                        f_rating,
                        f_count
                )
                firestore.collection("BookingCart").document(ongoingList[position].id).set(updateitem)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Rated " + rrating + " / " + "5", Toast.LENGTH_LONG).show()
                            ongoingList[position].rating=rrating.toString()
                            ongoingList[position].flag=rrating.toString()
                            notifyDataSetChanged()
                        }

                firestore.collection("Fields").document(ongoingList[position].id_field).set(nnnewitem).addOnSuccessListener {
                    Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_history_card,parent,false)
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

