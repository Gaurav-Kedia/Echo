package com.gaurav.echo.adapter

import android.content.Context
import android.support.constraint.R.id.parent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.accessibility.AccessibilityManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.gaurav.echo.Songs

class MainScreenAdapter (_songDetails: ArrayList<Songs>, _context: Context): RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>(){

    var songDetails: ArrayList<Songs>?=null
    var mContext: Context?=null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist
        holder.contentHolder?.setOnClickListener({
            Toast.makeText(mContext, "Hey" + songObject?.songTitle, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainScreenAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.row_custom_mainscreen_adapter,parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(songDetails == null){
            return 0
        }
        else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var trackTitle: TextView?=null
        var trackArtist: TextView?=null
        var contentHolder: RelativeLayout?=null

        init {
            trackTitle = view.findViewById(R.id.trackTitle) as TextView
            trackArtist = view.findViewById(R.id.trackArtist) as TextView
            contentHolder = view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }
}