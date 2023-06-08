package com.example.qrscanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrscanner.R
import com.example.qrscanner.model.QrCode
import java.text.SimpleDateFormat
import java.util.Date

class PreviousScansAdapter(private val qrCodesList: List<QrCode>) : RecyclerView.Adapter<PreviousScansAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.previous_scan_single_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val qrCode = qrCodesList[position]

        holder.txtContent.text = qrCode.content
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm")
        val dateString = formatter.format(Date(qrCode.time))

        holder.txtTime.text = dateString

    }

    override fun getItemCount(): Int {
        return qrCodesList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtContent: TextView = itemView.findViewById(R.id.txtContent)
        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
    }
}