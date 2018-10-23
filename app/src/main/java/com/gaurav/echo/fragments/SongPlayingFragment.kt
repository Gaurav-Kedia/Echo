package com.gaurav.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.gaurav.echo.CurrentSongHelper
import com.gaurav.echo.R
import com.gaurav.echo.Songs
import com.gaurav.echo.databases.EchoDatabase
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.currentPosition
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.fab
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.favouriteContent
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.loopImageButton
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.mediaplayer
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.nextImageButton
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.previousImageButton
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.songArtistView
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.songTitleView
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.startTimeTExt
import com.gaurav.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.util.*
import java.util.concurrent.TimeUnit

class SongPlayingFragment : Fragment() {



    object Statified{
        var myActivity: Activity?=null
        var mediaplayer: MediaPlayer?=null

        var startTimeTExt :TextView?=null
        var endTimeText: TextView?=null
        var playPauseImageButton: ImageButton?=null
        var previousImageButton: ImageButton?=null
        var nextImageButton: ImageButton?=null
        var loopImageButton: ImageButton?=null
        var shuffleImageButton: ImageButton?=null
        var seekBar: SeekBar?=null
        var songArtistView: TextView?=null
        var songTitleView: TextView?=null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>?=null

        var currentSongHelper: CurrentSongHelper?=null
        var audioVisualization: AudioVisualization?=null
        var glView: GLAudioVisualizationView?=null
        var fab: ImageButton?=null
        var favouriteContent: EchoDatabase?=null

        var mSensorManager: SensorManager?=null
        var mSensorListener: SensorEventListener?=null

        var MY_PREFS_NAME = "ShakeFeature"

        var updateSongTime = object: Runnable{
            override fun run() {
                val getCurrent = mediaplayer?.getCurrentPosition()
                startTimeTExt?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
                Statified.seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }
    }


    object Staticated{
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete(){
            if(currentSongHelper?.isShuffle  as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {
                if(currentSongHelper?.isLoop as Boolean){
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songId = nextSong?.songID as Long
                    updateTextviews(currentSongHelper?.songTitle as String,
                        currentSongHelper?.songArtist as String)

                    mediaplayer?.reset()
                    try {
                        mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaplayer?.prepare()
                        mediaplayer?.start()
                        processInformation(mediaplayer as MediaPlayer)
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }else{
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }
            if((favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)){
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else{
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }
        fun updateTextviews(songTitle: String, songArtist: String){
            songTitleView?.setText(songTitle)
            songArtistView?.setText(songArtist)
        }

        fun processInformation(mediaPlayer: MediaPlayer){
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekBar?.max = finalTime
            startTimeTExt?.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())))
            )

            endTimeText?.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())))
            )
            Statified.seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun playNext(check: String){
            if (check.equals("PlayNextNormal", true)){
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle",true)){
                var randomObject = Random()
                var randomPostion = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPostion
            }

            if (currentPosition == fetchSongs?.size){
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long
            updateTextviews(currentSongHelper?.songTitle as String,
                currentSongHelper?.songArtist as String)
            mediaplayer?.reset()
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaplayer?.prepare()
                mediaplayer?.start()
                processInformation(mediaplayer as MediaPlayer)
            }catch (e: Exception){
                e.printStackTrace()
            }
            if((favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)){
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else{
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }



    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.startTimeTExt = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favoriteIcon)
        Statified.fab?.alpha = 0.8f
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
            Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView() {
        Statified.audioVisualization?.release()
        super.onDestroyView()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect->{
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.
            getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favouriteContent = EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false


        var path: String?=null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            path = arguments?.getString("path")
            _songTitle = arguments?.getString("songTitle")
            _songArtist = arguments?.getString("songArtist")
            songId = arguments?.getInt("songId")!!.toLong()

            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments?.getParcelableArrayList("songData")

            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextviews(Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String)

        }catch (e: Exception){
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if(fromFavBottomBar != null){
            Statified.mediaplayer = FavoriteFragment.Statified.mediaplayer
        } else {
            mediaplayer = MediaPlayer()
            mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaplayer?.setDataSource(myActivity, Uri.parse(path))
                mediaplayer?.prepare()
            }catch (e: Exception){
                e.printStackTrace()
            }
            mediaplayer?.start()
        }

        Staticated.processInformation(mediaplayer as MediaPlayer)
        if(currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else  {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickhandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if(isShuffleAllowed as Boolean){
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if(isLoopAllowed as Boolean){
            currentSongHelper?.isLoop = true
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

        }

        if((favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)){
            fab?.setBackgroundResource(R.drawable.favorite_on)
        }
        else{
            fab?.setBackgroundResource(R.drawable.favorite_off)
        }
    }

    fun clickhandler(){

        fab?.setOnClickListener({
            if((favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)){
                fab?.setBackgroundResource(R.drawable.favorite_off)
                favouriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity, "Removed from favourites", Toast.LENGTH_SHORT).show()
            }
            else{
                fab?.setBackgroundResource(R.drawable.favorite_on)
                favouriteContent?.storeAsFavorite(currentSongHelper?.songId?.toInt(), currentSongHelper?.songArtist,
                    currentSongHelper?.songTitle, currentSongHelper?.songPath)
                Toast.makeText(myActivity, "Added to Favourites", Toast.LENGTH_SHORT).show()
            }
        })

        shuffleImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if (currentSongHelper?.isShuffle as Boolean){
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if(currentSongHelper?.isShuffle as Boolean){
                Staticated.playNext("PlayNextLikeNormalShuffle")
            } else {
                Staticated.playNext("PlayNextNormal")
            }
        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })
        loopImageButton?.setOnClickListener({
            var editorShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()
            if(currentSongHelper?.isLoop as Boolean){
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })

        playPauseImageButton?.setOnClickListener({
            if (mediaplayer?.isPlaying as Boolean){
                mediaplayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaplayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    fun playPrevious(){
        currentPosition = currentPosition -1
        if(currentPosition == -1){
            currentPosition = 0
        }
        if(currentSongHelper?.isPlaying as Boolean){
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }

        currentSongHelper?.isLoop = false
        val nextSong =fetchSongs?.get(currentPosition)
        currentSongHelper?.songPath = nextSong?.songData
        currentSongHelper?.songTitle = nextSong?.songTitle
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId = nextSong?.songID as Long
        Staticated.updateTextviews(currentSongHelper?.songTitle as String,
            currentSongHelper?.songArtist as String)

        mediaplayer?.reset()
        try {
            mediaplayer?.setDataSource(activity, Uri.parse(currentSongHelper?.songPath))
            mediaplayer?.prepare()
            mediaplayer?.start()
            Staticated.processInformation(mediaplayer as MediaPlayer)
        } catch (e: Exception){
            e.printStackTrace()
        }
        if((favouriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)){
            fab?.setBackgroundResource(R.drawable.favorite_on)
        }
        else{
            fab?.setBackgroundResource(R.drawable.favorite_off)
        }

    }
    fun bindShakeListener(){
        Statified.mSensorListener = object: SensorEventListener{
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                mAccelerationLast = mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x)+(y*y)+(z*z)).toDouble()).toFloat()
                val delta = mAcceleration - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if(mAcceleration > 12){
                    val prefs = Statified.myActivity?.getSharedPreferences(
                        Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if(isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }

        }
    }
}
