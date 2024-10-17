package com.gamzeuysal.seyahatkitabimharitalarroomdatabase.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.R
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.adapter.PlaceAdapter
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.databinding.ActivityMainBinding
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.model.Place
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.roomdb.PlaceDao
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.roomdb.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    //View Binding
    private  lateinit var binding : ActivityMainBinding
    //Composite Disposable
    private val compositeDisposable = CompositeDisposable()
    //room database
    private  lateinit var  db :PlaceDatabase
    private  lateinit var placeDao: PlaceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initialization
        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao = db.placeDao()

        compositeDisposable.add(
            placeDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(placeList: List<Place>)
    {
        //veri tabanından gelen placeList'ler recyclerView da gösterilecek
        //recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PlaceAdapter(placeList)
        binding.recyclerView.adapter = adapter
    }

    //eklediğimiz menuyu Main activity de bağlayalım

    //menu layout baglama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.place_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu item larına tıklama
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_place)
        {
            //Yeni yer eklendiyse intent ile diğer aktiviteye gidecegiz
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}