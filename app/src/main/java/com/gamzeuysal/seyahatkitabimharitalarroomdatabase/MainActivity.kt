package com.gamzeuysal.seyahatkitabimharitalarroomdatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //eklediğimiz menuyu Main activity de bağlayalım

    //menu layout baglama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.place_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu item larına tıklama
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_place)
        {
            //Yeni yer eklendiyse intent ile diğer aktiviteye gidecegiz
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }
        return super.onContextItemSelected(item)
    }
}