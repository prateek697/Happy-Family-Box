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
import com.example.happyfamilybox.models.Fields
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


import java.text.SimpleDateFormat
import java.util.*


class YourFieldAdapterActivity(var context: Context, var ongoingList: MutableList<Fields>):
        RecyclerView.Adapter<YourFieldAdapterActivity.DetailsViewHolder>() {

    class DetailsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var FieldName: TextView = itemView.findViewById(R.id.field_name)
        var uniqid: TextView = itemView.findViewById(R.id.unique_ID)
        var Moreinfo: Button = itemView.findViewById(R.id.more_info_button)
        var Editt: Button = itemView.findViewById(R.id.edit_button)
        var addres: TextView = itemView.findViewById(R.id.address)
        var delete_but : ImageView = itemView.findViewById(R.id.delete_button)

    }
    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {

        holder.addres.text = ongoingList[position].address
        var x : String = ""
        if(ongoingList[position].count.toInt()==0) x="5/5"
        else{
            var t1=ongoingList[position].count.toInt()
            var t2=ongoingList[position].rating.toInt()
            var ratt = t2/t1

            x=ratt.toString()+"/5"

        }
        holder.FieldName.text = "Field Name - " + ongoingList[position].fieldname + " , " + x
        holder.uniqid.text ="ID : " + ongoingList[position].id

        holder.Moreinfo.setOnClickListener {
            val intent = Intent(context, MoreInfoAboutYourFieldActivity::class.java)
            intent.putExtra("id_field",ongoingList[position].id.toString())
            context.startActivity(intent)
        }

        holder.Editt.setOnClickListener {
            val intent = Intent(context, EditCropSectionActivity::class.java)
            intent.putExtra("id_field",ongoingList[position].id)
            intent.putExtra("email",ongoingList[position].email)
            context.startActivity(intent)
        }

        holder.delete_but.setOnClickListener {

            var firestore = FirebaseFirestore.getInstance().collection("Fields").document(ongoingList[position].id)
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.your_fields_card,parent,false)
        return DetailsViewHolder(
                itemView
        )
    }

    override fun getItemCount(): Int {
        return ongoingList.size
    }

    fun setList(list: MutableList<Fields>){
        ongoingList = list

        notifyDataSetChanged()
    }
}

