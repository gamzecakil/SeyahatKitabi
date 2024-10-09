package com.gamzeuysal.seyahatkitabimharitalarroomdatabase

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.databinding.ActivityMapsBinding
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    //güncel konumu alma
    private lateinit var locationManager:LocationManager
    private  lateinit var locationListener : LocationListener
    //verilen izni yakalama  --> sistemde izinler string olarak saklanıyor.-->ACCESS_FINE_LOCATION"
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*
        val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this,permission,0)
         */

        //sistem ilk başladığnda gerekli izinler var mı diye kontrol et
        registerLauncher()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //casting
         locationManager = this.getSystemService(Context.LOCALE_SERVICE) as LocationManager
        //locationManager = (LocationManager)this.getSystemService(Context.LOCALE_SERVICE)
        //kullanıcı konum degistirdikce sen de degistir
        locationListener = object : LocationListener {
            override fun onLocationChanged(location : Location) {
              println("location :$location")
            }
        }
        //ilk önce gerekli izin verilmiş mi kontrol edelim.
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //not permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)) //--> calısma kararı androidin sitemi karar verir.
            {
                //kullanıcıya mesaj gönder onunla birlikte yine izin iste
                Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //setAction ile  belirlenen butona tıklanırsa ne olacak
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

        }else{
            //permission granted
            //konum güncellemelerini al
            locationManager?.let {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            }

        }






        // Add a marker in Sydney and move the camera
        /*
        val kartalBridge = LatLng(40.90688808788227, 29.210851480028133)
        mMap.addMarker(MarkerOptions().position(kartalBridge).title("Kartal Köprüsü"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kartalBridge,15f))

         */
    }
    private fun registerLauncher()
    {
      permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->



          if(result)
          {
              //permission granted
              if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
              {
                  locationManager?.let {
                      locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                  }
              }

          }else{
              //permission  denied
              Toast.makeText(this@MapsActivity,"Permission needed!",Toast.LENGTH_LONG).show()
          }
      }
    }
}