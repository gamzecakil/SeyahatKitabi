package com.gamzeuysal.seyahatkitabimharitalarroomdatabase.view

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.databinding.ActivityMapsBinding
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.model.Place
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.roomdb.PlaceDao
import com.gamzeuysal.seyahatkitabimharitalarroomdatabase.roomdb.PlaceDatabase
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback ,GoogleMap.OnMapLongClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    //güncel konumu alma
    private lateinit var locationManager:LocationManager
    private  lateinit var locationListener : LocationListener
    //verilen izni yakalama  --> sistemde izinler string olarak saklanıyor.-->ACCESS_FINE_LOCATION"
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private val TAG = "MapsActivity"
    //Güncel konumu almayı bir kez çalıştırma
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean : Boolean? = null
    //marker eklenen konumu alma
    private var selectedLatitude : Double? = null
    private var selectedLongitude :Double? = null
    //room database
    private lateinit var db : PlaceDatabase
    private lateinit var placeDao : PlaceDao

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

        selectedLatitude = 0.0
        selectedLongitude = 0.0

        sharedPreferences = this.getSharedPreferences("com.gamzeuysal.seyahatkitabimharitalarroomdatabase", MODE_PRIVATE)
        trackBoolean = false


        //sistem ilk başladığnda gerekli izinler var mı diye kontrol et
        registerLauncher()

        //room database
        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao = db.placeDao()

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener (this) //GoogleMap.OnMapLongClickListener implementasyonu ile benim Activityde listener oldu.this --> MapsActivity

        //casting
         locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //locationManager = (LocationManager)this.getSystemService(Context.LOCALE_SERVICE)
        //kullanıcı konum degistirdikce sen de degistir
        locationListener = object : LocationListener {
            override fun onLocationChanged(location : Location) {

                //on Location changed uygulama açıldığında sadece bir kez çalıştırılacak.
                trackBoolean = sharedPreferences.getBoolean("trackBoolean",false)

                if(!trackBoolean!!)
                {
                    Log.d(TAG,"LOCATION : $location")
                    val userLocation = LatLng(location.latitude,location.longitude)
                    // mMap.addMarker(MarkerOptions().position(userLocation).title("Guncel Konum"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                }
            }
        }
        //ilk önce gerekli izin verilmiş mi kontrol edelim.
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
             Log.d(TAG,"PERMISSION DENIED onMapReady.")
            //not permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)) //--> calısma kararı androidin sitemi karar verir.
            {
                //kullanıcıya mesaj gönder onunla birlikte yine izin iste
                Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    //setAction ile  belirlenen butona tıklanırsa ne olacak
                    //request permission
                    //izin isteme mesaj kutusu
                    permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //request permission
                Log.d(TAG,"PERMISSION LAUNCH.")
                //izin isteme mesaj kutusu
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

        }else{
            //permission granted
            //konum güncellemelerini al
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastLocation?.let {
                val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
            }

            //konumu etkinleştirme
            mMap.isMyLocationEnabled = true

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


       Log.d(TAG,"RESULT : ${result}")
          if(result)
          {
              //permission granted
              if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
              {
                      locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
              }
              Log.d(TAG,"PERMISSION GRANTED onCreate.")

              val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
              lastLocation?.let {
                  val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
              }

              //konumu etkinleştirme
              mMap.isMyLocationEnabled = true

          }else{
              //permission  denied
              Toast.makeText(this@MapsActivity,"Permission needed!",Toast.LENGTH_LONG).show()
              Log.d(TAG,"PERMISSION DENIED onCreate.")



          }
      }
    }

    //her uzun tıkladıgımızda çalısır.
    override fun onMapLongClick(p0: LatLng) {

        //daha önce eklenmiş marker varsa sil
        mMap.clear()
       //po --> uzun tıklanıldıgında verilen LatLng yani location
        mMap.addMarker(MarkerOptions().position(p0))
        
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude
    }
    fun save(view: View)
    {
        if(selectedLatitude != null && selectedLongitude != null)
        {
            val place = Place(binding.placeEditText.text.toString(),selectedLatitude!!,selectedLongitude!!)
            placeDao.insert(place)
        }
    }
    fun delete(view:View)
    {

    }
}