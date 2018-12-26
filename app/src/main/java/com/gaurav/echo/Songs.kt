package com.gaurav.echo

import android.os.Parcel
import android.os.Parcelable

class Songs(var songID: Long, var songTitle: String, var artist: String,
            var songData: String, var dateAdded: Long): Parcelable{
    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    var msongDateAdded: Long = 0


    init {
        this.msongDateAdded = dateAdded
    }

    object Statified{
        var nameComparator: Comparator<Songs> = Comparator<Songs>{song1, song2 ->
            val nameOne = song1.songTitle.toUpperCase()
            val nameTwo = song2.songTitle.toUpperCase()
            nameOne.compareTo(nameTwo)
        }

        var dateComparator: Comparator<Songs> = Comparator<Songs>{song1, song2 ->
            val dataOne = song1.dateAdded.toDouble()
            val dataTwo = song2.dateAdded.toDouble()
            dataTwo.compareTo(dataOne)
        }
    }

}