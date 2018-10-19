package com.gaurav.echo.activities

import com.gaurav.echo.R
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import com.gaurav.echo.activities.MainActivity.Statified.drawerLayout
import com.gaurav.echo.adapter.NavigationDrawerAdapter
import com.gaurav.echo.fragments.mainScreenFragment

class MainActivity : AppCompatActivity(){

    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()

    var images_for_naavdrawer = intArrayOf(R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites, R.drawable.navigation_settings,
        R.drawable.navigation_aboutus)
    object Statified{
        var drawerLayout: DrawerLayout?=null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favourites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Us")


        val toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout?.setDrawerListener(toggle)
        toggle.syncState()

        val mainScreenFragment = mainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "mainScreenFragment")
            .commit()
        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconsList, images_for_naavdrawer, this)
        _navigationAdapter.notifyDataSetChanged()

        var navigation_recycle_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycle_view.layoutManager = LinearLayoutManager(this)
        navigation_recycle_view.itemAnimator = DefaultItemAnimator()

        navigation_recycle_view.adapter = _navigationAdapter
        navigation_recycle_view.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
    }
}