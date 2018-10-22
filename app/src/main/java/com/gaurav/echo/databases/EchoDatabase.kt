package com.gaurav.echo.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.gaurav.echo.Songs
import com.gaurav.echo.databases.EchoDatabase.Staticated.COLUMN_ID
import com.gaurav.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.gaurav.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_PATH
import com.gaurav.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_TITLE
import com.gaurav.echo.databases.EchoDatabase.Staticated.TABLE_NAME

class EchoDatabase: SQLiteOpenHelper{

    var _songList = ArrayList<Songs>()

    object Staticated{
        val DB_NAME = "FavouriteDAtabase"
        var DB_VERSION = 1

        val TABLE_NAME = "FvouriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"

    }

    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL(
        "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_ID + " INTEGER," +
                COLUMN_SONG_ARTIST + " STRING," + COLUMN_SONG_TITLE +
                " STRING," + COLUMN_SONG_PATH + " STRING);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(context: Context?) : super(
        context, Staticated.DB_NAME, null, Staticated.DB_VERSION
    )
    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?){
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(COLUMN_ID, id)
        contentValues.put(COLUMN_SONG_ARTIST, artist)
        contentValues.put(COLUMN_SONG_TITLE, songTitle)
        contentValues.put(COLUMN_SONG_PATH, path)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }
    fun queryDBList(): ArrayList<Songs>? {
        try{
            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + TABLE_NAME
            var cSor = db.rawQuery(query_params, null)
            if(cSor.moveToFirst()){
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(COLUMN_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                }while (cSor.moveToNext())
            } else { return null}
        }catch (e :Exception){
            e.printStackTrace()
        }
        return _songList
    }
    fun checkifIdExists(_id: Int): Boolean{
        var storeId = -1090
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if(cSor.moveToNext()){
            do {

            }while (cSor.moveToNext())
        } else {return false}
        return storeId != -1090
    }
    fun deleteFavourite(_id: Int){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COLUMN_ID + " = " + _id, null)
        db.close()
    }
}