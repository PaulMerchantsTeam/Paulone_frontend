package com.paulmerchants.gold.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import com.paulmerchants.gold.R
import com.paulmerchants.gold.adapter.MapLocationAdapter
import com.paulmerchants.gold.common.BaseActivity
import com.paulmerchants.gold.databinding.ActivityMapBinding
import com.paulmerchants.gold.model.newmodel.PmlBranch
import com.paulmerchants.gold.place.BitmapHelper
import com.paulmerchants.gold.security.sharedpref.AppSharedPref
import com.paulmerchants.gold.utility.AppUtility
import com.paulmerchants.gold.viewmodels.CommonViewModel
import com.paulmerchants.gold.viewmodels.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapActivity : BaseActivity<CommonViewModel, ActivityMapBinding>(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    override fun getViewBinding() = ActivityMapBinding.inflate(layoutInflater)
    private var cityName: String = ""
    private val mapLocationAdapter = MapLocationAdapter(::onLocationClicked, ::onMarkLocation)

    //    private val places: ArrayList<com.paulmerchants.gold.place.Place> = arrayListOf()
    private val mapViewModel: MapViewModel by viewModels()


    private fun onMarkLocation(pmlBranch: PmlBranch) {
        val pm22 = LatLng(pmlBranch.branchLat.toDouble(), pmlBranch.branchLng.toDouble())
        Log.d(TAG, "addMarkers: $pm22")
        map?.clear()
        map?.addMarker(
            MarkerOptions()
                .position(pm22)
                .title(pmlBranch.branchCity)
        )
        map?.resetMinMaxZoomPreference()
        map?.moveCamera(CameraUpdateFactory.newLatLng(pm22))
    }

    private fun onLocationClicked(pmlBranch: PmlBranch) {
        val geoUri =
            "geo:${pmlBranch.branchLat.toDouble()},${pmlBranch.branchLng.toDouble()}?z=15&q=${pmlBranch.branchLat.toDouble()},${pmlBranch.branchLng.toDouble()}(Paul Merchants)"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        startActivity(mapIntent)
    }

    var addressList: List<Address>? = null
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)
    override val mViewModel: CommonViewModel by viewModels()

    // [START maps_current_place_on_create]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            try {
                mapViewModel.getBranchWithPaging(AppSharedPref).collectLatest { data ->
                    Log.d(TAG, "onCreate: ..dattttttttt........}")
                    showDataToRv(data)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }


//
//        mViewModel.placesLive.observe(this) {
//            it?.let {
//                Log.d(TAG, "onCreate: .....${it.size}")
//                setupLocationsTile(it)
//                addMarkers(it)
//            }
//        }
        /*
                mapViewModel.branchLocation.observe(this) {
                    it?.let {
                        Log.d(TAG, "onCreate: .....${it.body()?.data?.size}")
                        it.body()?.data?.let {
                            for (i in it) {
                                places.add(
                                    com.paulmerchants.gold.place.Place(
                                        i.branchName,
                                        i.branchLat.toDouble(),
                                        i.branchLng.toDouble(),
                                        i.branchAddress,
                                        i.branchCity
                                    )
                                )
                            }
                        }
                        setupLocationsTile(places)
                        addMarkers(places)
                    }
                }
        */
        binding.headerMap.apply {
            titlePageTv.text = getString(R.string.locate_us_near_u)
        }
        binding.headerMap.backIv.setOnClickListener {
            finish()
        }
        // [START_EXCLUDE silent]
        // Retrieve location and camera position from saved instance state.
        // [START maps_current_place_on_create_save_instance_state]
        if (savedInstanceState != null) {
            lastKnownLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(KEY_LOCATION, Location::class.java)
            } else {
                savedInstanceState.getParcelable(KEY_LOCATION)
            }
            cameraPosition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getParcelable(KEY_CAMERA_POSITION, CameraPosition::class.java)
            } else {
                savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
            }
        }

        binding.viewAllLocation.setOnClickListener {
            lifecycleScope.launch {
                try {
                    mapViewModel.getBranchWithPaging(AppSharedPref).collectLatest { data ->
                        Log.d(TAG, "onCreate: ..dattttttttt........}")
                        showDataToRv(data)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // Retrieve the content view that renders the map.
        setContentView(binding.root)

        // [START_EXCLUDE silent]
        // Construct a PlacesClient
        Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY))

        val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
        val bounds = RectangularBounds.newInstance(
            LatLng(8.4, 68.7),
            LatLng(37.6, 97.25)
        ) // Set the bounds to India
        val requestFind = FindAutocompletePredictionsRequest.builder()
            .setQuery("Paul Merchants") // Set the query to Paul Merchants
            .setCountry("IN") // Limit the results to India
            .setLocationBias(bounds)
            .setTypeFilter(TypeFilter.ESTABLISHMENT) // Only return establishments
            .setSessionToken(token)
            .build()

        placesClient = Places.createClient(this)


        placesClient.findAutocompletePredictions(requestFind)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                // Handle the response here
                val predictions = response.autocompletePredictions
                for (prediction in predictions) {
                    Log.i("Places API", prediction.getFullText(null).toString())
                }
            }.addOnFailureListener { exception: Exception ->
                // Handle the exception here
                Log.e("Places API", exception.message, exception)
            }

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        // [START maps_current_place_map_fragment]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        // [END maps_current_place_map_fragment]
        // [END_EXCLUDE]
    }

    private fun showDataToRv(data: PagingData<PmlBranch>) {
        mapLocationAdapter.submitData(lifecycle, data)
        binding.rvPmLocation.adapter = mapLocationAdapter
        addMarkers(data)
    }

    override fun onStart() {
        super.onStart()
        AppUtility.changeStatusBarWithReqdColor(this, R.color.splash_screen_two)

        binding.searchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d(TAG, "onTextChanged: .......$p0")
                if (p0 != null) {
                    Log.d(TAG, "onTextChanged: ........$p0")
                    lifecycleScope.launch {
                        try {
                            mapViewModel.searchBranchWithPaging(p0.toString(), AppSharedPref)
                                .collectLatest { data ->
                                    Log.d(TAG, "onCreate: ..dattttttttt........}")
                                    showDataToRv(data)
                                }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private val goldIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.splash_screen_one)
        BitmapHelper.vectorToBitmap(this, R.drawable.gold_logo, color)
    }

    private fun addMarkers(places: PagingData<PmlBranch>) {
        places.map { i ->
            val pm22 = LatLng(i.branchLat.toDouble(), i.branchLng.toDouble())
            Log.d(TAG, "addMarkers: $pm22")
            map?.addMarker(
                MarkerOptions()
                    .position(pm22)
                    .title("Paul Merchants: ${i.branchAddress}")
            )!!
        }


    }


    // [START maps_current_place_on_save_instance_state]
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }


    /*
        fun getListLocation(){
            val bounds = LatLngBounds.builder()
                .include(LatLng(8.058430, 68.084473)) // Southwest corner of India
                .include(LatLng(37.090240, 97.344664)) // Northeast corner of India
                .build()

            val filter = AutocompleteFilter.Builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("IN")
                .build()

            val placesClient = Places.createClient(this)

            placesClient.autocomplete(
                "Paul Merchants",
                bounds,
                filter
            ).addOnSuccessListener { response: AutocompleteResponse ->
                // Handle the response here
                val predictions = response.autocompletePredictions
                for (prediction in predictions) {
                    Log.i("Places API", prediction.placeId)
                    // Get the latitude and longitude of the place
                    placesClient.fetchPlace(
                        FetchPlaceRequest.newInstance(prediction.placeId, listOf(Place.Field.LAT_LNG))
                    ).addOnSuccessListener { fetchPlaceResponse: FetchPlaceResponse ->
                        val latLng = fetchPlaceResponse.place.latLng
                        Log.i("Places API", "Latitude: ${latLng?.latitude}, Longitude: ${latLng?.longitude}")
                    }.addOnFailureListener { exception: Exception ->
                        Log.e("Places API", exception.message, exception)
                    }
                }
            }.addOnFailureListener { exception: Exception ->
                // Handle the exception here
                Log.e("Places API", exception.message, exception)
            }

        }
    */

    @Suppress("OverridingDeprecatedMember")
    // [START maps_current_place_on_map_ready]
    override fun onMapReady(map: GoogleMap) {
        this.map = map

//        val pm22 = LatLng(
//            30.737825,
//            76.7753962
//        )
//        val pmMp = LatLng(
//            23.183592463587168, 75.87766528732615
//        )
//        val pmKarnatka = LatLng(
//            13.145759284981244, 77.63917863544266
//        )
//        val pmTelangana = LatLng(
//            17.773004934599374, 78.63262546429902
//        )
//        map.addMarker(
//            MarkerOptions()
//                .position(pm22)
//                .title("Paul Merchants")
//        )
//        map.addMarker(
//            MarkerOptions()
//                .position(pmMp)
//                .title("Royal Ratan Tower, U G-1, 7, Mahatma Gandhi Rd, Indore, Madhya Pradesh 452001")
//        )
//        map.addMarker(
//            MarkerOptions()
//                .position(pmKarnatka)
//                .title("Liberty Plaza, 5&6 Upper Ground Floor, Himayatnagar, Hyderabad, Telangana 500029")
//        )
//        map.addMarker(
//            MarkerOptions()
//                .position(pmTelangana)
//                .title("70, 4th Block, 2nd Floor 27th Cross, 9th Main Rd, Jayanagara, Bengaluru, Karnataka 560041")
//        )

//        addMarkers(map)
        // [START_EXCLUDE silent]
//        map.moveCamera(CameraUpdateFactory.newLatLng(pm22))

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }


            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission()
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
        map.setOnMarkerClickListener(this)
        showCurrentPlace()

    }

//    private fun setupLocationsTile(place: List<com.paulmerchants.gold.place.Place>) {
//        mapLocationAdapter.submitList(place)
//        binding.rvPmLocation.adapter = mapLocationAdapter
//    }

    // [START maps_current_place_get_device_location]
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        Log.d(
                            TAG,
                            "getDeviceLocation: ....${lastKnownLocation?.latitude}-----${lastKnownLocation?.longitude}"
                        )
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false

                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
// [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    /**
     * Prompts the user for permission to use the device location.
     */

// [START maps_current_place_location_permission]

    override fun onResume() {
        super.onResume()
        getLocationPermission()
        if (locationPermissionGranted) {
            try {
                getCityName()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun getCityName() {
        var count = 1
        val geocoder = Geocoder(this, Locale.getDefault())
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f
        ) { location ->
            val addresses =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                cityName = addresses[0]?.locality.toString()
//                Log.d("CityName", "$cityName")
                if (count == 1) {
//                    mViewModel.filterLocation(cityName)
                    count++
                } else {
                    return@requestLocationUpdates
                }
            }
        }
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true

        } else {

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


// [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    /**
     * Handles the result of the request for location permissions.
     */

// [START maps_current_place_on_request_permissions_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    updateLocationUI()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
//        updateLocationUI()
    }
// [END maps_current_place_on_request_permissions_result]

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */

// [START maps_current_place_show_current_place]
    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count =
                        if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                            likelyPlaces.placeLikelihoods.size
                        } else {
                            M_MAX_ENTRIES
                        }
                    var i = 0
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog()
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            map?.addMarker(
                MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .snippet(getString(R.string.default_info_snippet))
            )

            // Prompt the user for permission.
            getLocationPermission()
        }
    }
// [END maps_current_place_show_current_place]

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */

// [START maps_current_place_open_places_dialog]
    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener =
            DialogInterface.OnClickListener { _, which -> // The "which" argument contains the position of the selected item.

                val markerLatLng = likelyPlaceLatLngs[which]
                var markerSnippet = likelyPlaceAddresses[which]

                if (likelyPlaceAttributions[which] != null) {
                    markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
                }

                if (markerLatLng == null) {
                    return@OnClickListener
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                map?.addMarker(
                    MarkerOptions()
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet)
                )

                // Position the map's camera at the location of the marker.
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        markerLatLng,
                        DEFAULT_ZOOM.toFloat()
                    )
                )
            }

        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle(R.string.pick_place)
            .setItems(likelyPlaceNames, listener)
            .show()
    }


    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
                map?.uiSettings?.isZoomControlsEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                map?.uiSettings?.isZoomControlsEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
// [END maps_current_place_update_location_ui]

    companion object {
        private val TAG = MapActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                this,
                "${marker.title} has been clicked $newClickCount times.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }
}
