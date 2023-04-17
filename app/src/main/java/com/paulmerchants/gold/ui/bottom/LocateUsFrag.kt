package com.paulmerchants.gold.ui.bottom

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.paulmerchants.gold.R
import com.paulmerchants.gold.common.BaseFragment
import com.paulmerchants.gold.databinding.LocateUsScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocateUsFrag :
    BaseFragment<LocateUsScreenFragmentBinding>(LocateUsScreenFragmentBinding::inflate),
    OnMapReadyCallback {

    private var mapFragment: SupportMapFragment? = null

    override fun LocateUsScreenFragmentBinding.initialize() {


    }

    override fun onStart() {
        super.onStart()
        mapFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this@LocateUsFrag)
    }

    override fun onMapReady(gMap: GoogleMap) {
        val pm22 = LatLng(30.741077093726833, 76.77548923820267)
        gMap.addMarker(
            MarkerOptions()
                .position(pm22)
                .title("Marker in Sydney")
        )
        // [START_EXCLUDE silent]
        gMap.moveCamera(CameraUpdateFactory.newLatLng(pm22))
        // [END_EXCLUDE]
//        val paulMerchantsSec22  = LatLng(30.7196304, 76.7203943)
//        gMap.addMarker(
//            MarkerOptions().position(paulMerchantsSec22).title("Paul+Merchants+(पॉल+मर्चेंट्स)")
//        )
//        // [START_EXCLUDE silent]
//        gMap.moveCamera(CameraUpdateFactory.newLatLng(paulMerchantsSec22))
//        // [END_EXCLUDE]
    }

}