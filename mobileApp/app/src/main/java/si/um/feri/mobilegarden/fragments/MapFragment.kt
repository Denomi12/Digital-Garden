package si.um.feri.mobilegarden.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
    private var isPickingLocation = false
    private lateinit var btnPickLocation: Button

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
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        btnPickLocation = view.findViewById(R.id.btnPickLocation)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadEventsFromFile()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnPickLocation.setOnClickListener {
            isPickingLocation = !isPickingLocation
            updateButtonState()
        }
    }

    private fun updateButtonState() {
        if (isPickingLocation) {
            btnPickLocation.text = "Klikni na zemljevid za izbiro..."
            Toast.makeText(context, "Izberi lokacijo na zemljevidu", Toast.LENGTH_SHORT).show()
        } else {
            btnPickLocation.text = "Dodaj nov dogodek"
        }
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

        googleMap?.setOnMapClickListener { latLng ->
            if (isPickingLocation) {
                val bundle = Bundle().apply {
                    putDouble("arg_lat", latLng.latitude)
                    putDouble("arg_lon", latLng.longitude)
                }

                isPickingLocation = false
                updateButtonState()

                findNavController().navigate(R.id.action_mapFragment_to_addExtremeEventFragment, bundle)
            }
        }
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