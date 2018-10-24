package com.gaurav.echo.Utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.gaurav.echo.R
import com.gaurav.echo.activities.MainActivity
import com.gaurav.echo.fragments.SongPlayingFragment
import java.lang.Exception

class CaptureBroadcast: BroadcastReceiver(){
    override fun onReceive(p1: Context?, p0: Intent?) {
        if(p0?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statified.notificationManager?.cancel(1999)
            }catch (e: Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception){
                e.printStackTrace()
            }

        }else {
            val tn: TelephonyManager = p1?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tn?.callState){
                TelephonyManager.CALL_STATE_RINGING -> {
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1999)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaplayer?.pause()
                            SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                else ->{

                }
            }
        }
    }

}