package com.gamzeuysal.seyahatkitabimharitalarroomdatabase.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.R
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.databinding.RecyclerRowBinding
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.model.Place
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.view.MapsActivity

class PlaceAdapter( val placeList:List<Place>): RecyclerView.Adapter<PlaceAdapter.PlaceHolder> (){
    class PlaceHolder(val recyclerRowBinding: RecyclerRowBinding):RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHolder(recyclerRowBinding)
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.recyclerRowBinding.recyclerViewTextView.text = placeList.get(position).name
        //item clicked
        holder.itemView.setOnClickListener {
             val intent = Intent(holder.itemView.context,MapsActivity::class.java)
             //seçilen item i gönderelim.
             intent.putExtra("selectedPlace",placeList.get(position))//Serializable
             intent.putExtra("info","old")
             holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return  placeList.size
    }
}