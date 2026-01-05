package si.um.feri.mobilegarden.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import si.um.feri.mobilegarden.R
import si.um.feri.mobilegarden.models.ExtremeEvent
import java.io.File

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    private val fileName = "extreme_events.json"
    private val gson = Gson()
    private var eventsList: List<ExtremeEvent> = listOf()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadEventsFromFile()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadEventsFromFile() {
        val file = File(requireContext().filesDir, fileName)
        if (file.exists()) {
            try {
                val content = file.readText()
                val listType = object : TypeToken<List<ExtremeEvent>>() {}.type
                eventsList = gson.fromJson(content, listType) ?: listOf()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val slovenia = LatLng(46.1512, 14.9955)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(slovenia, 8f))

        enableMyLocation()

        refreshMapMarkers()
    }

    private fun refreshMapMarkers() {
        googleMap?.clear()

        for (event in eventsList) {
            val position = LatLng(event.latitude, event.longitude)

            googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(event.type)
                    .snippet("${event.locationName}: ${event.description}")
            )
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}