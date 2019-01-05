package com.gaurav.echo.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.gaurav.echo.R
import com.gaurav.echo.models.Songs
import com.gaurav.echo.fragments.SongPlayingFragment

class FavouriteContentAdapter(ctx: Context?, _songs: ArrayList<Songs>) : RecyclerView.Adapter<FavouriteContentAdapter.FavContentViewHolder>() {
    var _getSongs: ArrayList<Songs>? = null
    var mContext: Context? = null
    var mediaPlayer: MediaPlayer? = null


    init {
        this._getSongs = _songs
        this.mContext = ctx
        this.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
    }

    override fun onBindViewHolder(holder: FavContentViewHolder, position: Int) {

        val mSongs = _getSongs?.get(position)
        if (mSongs?.artist.equals("<unknown>", ignoreCase = true)) {
            holder.text_Artist?.setText("unknown")
        } else {
            holder.text_Artist?.setText(mSongs?.artist)
        }
        holder.text_Title?.setText(mSongs?.songTitle)
        holder.contentHolder?.setOnClickListener(View.OnClickListener {
            try {
                if (mediaPlayer?.isPlaying() as Boolean) {
                    mediaPlayer?.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val args = Bundle()
            args.putString("path", mSongs?.songData)
            args.putString("songTitle", mSongs?.songTitle)
            args.putString("songArtist", mSongs?.artist)
            args.putInt("songPosition", position)
            args.putString("from", "favorite")
            args.putInt("SongId", mSongs?.songID?.toInt() as Int)
            args.putParcelableArrayList("songsData", _getSongs)
            val songPlayingFragment = SongPlayingFragment()
            songPlayingFragment.arguments = args
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("FavoriteToBackStack")
                .commit()
        })

    }

    override fun getItemCount(): Int {
        return _getSongs?.size as Int
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavContentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_mainrecycler, parent, false)

        return FavContentViewHolder(itemView)

    }

    inner class FavContentViewHolder(view: View): RecyclerView.ViewHolder(view){
        var text_Title: TextView?=null
        var text_Artist: TextView?=null
        var contentHolder: RelativeLayout?=null

        init {
            text_Title = view.findViewById(R.id.trackTitle) as TextView
            text_Artist = view.findViewById(R.id.trackArtist) as TextView
            contentHolder = view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }
}