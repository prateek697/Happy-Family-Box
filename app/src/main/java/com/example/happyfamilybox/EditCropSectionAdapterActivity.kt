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
import com.example.happyfamilybox.models.Crops
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class EditCropSectionAdapterActivity(var context: Context, var ongoingList: MutableList<Crops>):
        RecyclerView.Adapter<EditCropSectionAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var cropNAME: TextView = itemView.findViewById(R.id.name_of_crop)
        var durattion: TextView = itemView.findViewById(R.id.duration_of_crop)
        var timeper : TextView = itemView.findViewById(R.id.time_perid)
        var delete_button : ImageView = itemView.findViewById(R.id.delete_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        holder.cropNAME.text ="Crop Name - " + ongoingList[position].cropname
        holder.durattion.text =  ongoingList[position].startmonth + " - " + ongoingList[position].endmonth
        holder.timeper.text = "Time-Peroid of harvest : " + ongoingList[position].timetaken + " " + "Days"

        holder.delete_button.setOnClickListener {
            var firestore = FirebaseFirestore.getInstance().collection("Fields").document(ongoingList[position].id_field)
            AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Are you sure you want to delete this?")
                    .setPositiveButton("Yes") { dialogInterface, i ->
                        firestore.collection("Crops").document(ongoingList[position].id).delete()
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.edit_crop_card,parent,false)
        return DetailsViewHolder(
                itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<Crops>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

