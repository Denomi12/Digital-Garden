package si.um.feri.mobilegarden.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import si.um.feri.mobilegarden.R
import si.um.feri.mobilegarden.models.ExtremeEvent
import si.um.feri.mobilegarden.models.Garden
import si.um.feri.mobilegarden.utils.FetchGardens
import si.um.feri.mobilegarden.utils.showWeatherNotification
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var isPickingLocation = false
    private lateinit var btnPickLocation: Button

    private val fileName = "extreme_events.json"
    private val gson = Gson()
    private var eventsList: MutableList<ExtremeEvent> = mutableListOf()

    private val markerMap = mutableMapOf<Marker, ExtremeEvent>()

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

        FetchGardens.getAllGardens(
            backendUrl = "http://10.0.2.2:3001",
            callback = object : FetchGardens.GardensCallback {

                override fun onSuccess(gardens: List<Garden>) {
                    requireActivity().runOnUiThread {
                        fetchWeatherForAllGardensTomorrow(gardens)
                    }
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()
                }
            }
        )


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
                val listType = object : TypeToken<MutableList<ExtremeEvent>>() {}.type
                eventsList = gson.fromJson(content, listType) ?: mutableListOf()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val slovenia = LatLng(46.1512, 14.9955)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(slovenia, 8f))

        enableMyLocation()
        refreshMapMarkers()

        googleMap?.setOnMarkerClickListener { marker ->
            val event = markerMap[marker]
            if (event != null) {
                showEventDetailsDialog(event)
            }
            true
        }

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

    private fun showEventDetailsDialog(event: ExtremeEvent) {
        AlertDialog.Builder(requireContext())
            .setTitle(event.type)
            .setMessage("Lokacija: ${event.locationName}\nOpis: ${event.description}")
            .setPositiveButton("Uredi") { _, _ ->
                val bundle = Bundle().apply {
                    putString("arg_event_id", event.id)
                }
                findNavController().navigate(R.id.action_mapFragment_to_addExtremeEventFragment, bundle)
            }
            .setNegativeButton("Izbriši") { _, _ ->
                confirmDelete(event)
            }
            .setNeutralButton("Zapri", null)
            .show()
    }

    private fun confirmDelete(event: ExtremeEvent) {
        AlertDialog.Builder(requireContext())
            .setTitle("Izbris dogodka")
            .setMessage("Ali ste prepričani, da želite izbrisati ta dogodek?")
            .setPositiveButton("Da") { _, _ ->
                deleteEventFromFile(event)
            }
            .setNegativeButton("Ne", null)
            .show()
    }

    private fun deleteEventFromFile(eventToDelete: ExtremeEvent) {
        try {
            eventsList.removeAll { it.id == eventToDelete.id }

            val file = File(requireContext().filesDir, fileName)
            val jsonString = gson.toJson(eventsList)
            file.writeText(jsonString)

            refreshMapMarkers()
            Toast.makeText(context, "Dogodek izbrisan.", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Napaka pri brisanju: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun refreshMapMarkers() {
        googleMap?.clear()
        markerMap.clear()

        for (event in eventsList) {
            val position = LatLng(event.latitude, event.longitude)

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(event.type)
                    .snippet("${event.locationName}: ${event.description}")
            )

            if (marker != null) {
                markerMap[marker] = event
            }
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

    private fun fetchWeatherForAllGardensTomorrow(gardens: List<Garden>) {
        Log.d("WeatherDebug", "Fetching weather for ${gardens.size} gardens")

        for (garden in gardens) {
            fetchTomorrowWeather(
                latitude = garden.latitude,
                longitude = garden.longitude,
                garden = garden
            )
        }
    }


    private fun fetchTomorrowWeather(
        latitude: Double,
        longitude: Double,
        garden: Garden
    ) {
        Thread {
            try {
                val urlString =
                    "https://api.open-meteo.com/v1/forecast" +
                            "?latitude=$latitude" +
                            "&longitude=$longitude" +
                            "&hourly=precipitation,windspeed" +
                            "&forecast_days=2"

                val response = URL(urlString).readText()
                val json = JSONObject(response)
                val hourly = json.getJSONObject("hourly")

                val precipArray = hourly.getJSONArray("precipitation")
                val windArray = hourly.getJSONArray("windspeed")
                val timeArray = hourly.getJSONArray("time")

                val calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)

                val sdfDay = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                sdfDay.timeZone = TimeZone.getTimeZone("UTC")
                val tomorrowStr = sdfDay.format(calendar.time)

                var maxPrecip = 0.0
                var maxWind = 0.0

                for (i in 0 until timeArray.length()) {
                    val timeStr = timeArray.getString(i)
                    if (timeStr.startsWith(tomorrowStr)) {
                        maxPrecip = maxOf(maxPrecip, precipArray.getDouble(i))
                        maxWind = maxOf(maxWind, windArray.getDouble(i))
                    }
                }

                val isStorm = maxPrecip >= 10.0 || maxWind >= 15.0
//                val isStorm = true;
                if (isStorm) {
                    requireActivity().runOnUiThread {
                        showWeatherNotification(
                            requireContext(),
                            garden,
                            maxPrecip,
                            maxWind
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


}
