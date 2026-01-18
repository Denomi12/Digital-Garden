package si.um.feri.mobilegarden.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import si.um.feri.mobilegarden.databinding.FragmentAddExtremeEventBinding
import si.um.feri.mobilegarden.models.ExtremeEvent
import java.io.File
import java.util.UUID

class AddExtremeEventFragment : Fragment() {

    private var _binding: FragmentAddExtremeEventBinding? = null
    private val binding get() = _binding!!

    private var selectedLat: Double = 0.0
    private var selectedLon: Double = 0.0

    private var eventIdToEdit: String? = null

    private val fileName = "extreme_events.json"
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedLat = it.getDouble("arg_lat", 0.0)
            selectedLon = it.getDouble("arg_lon", 0.0)
            eventIdToEdit = it.getString("arg_event_id") // Preberemo ID za urejanje
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExtremeEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventTypes = listOf("Mraz", "Vročinski val", "Toča", "Drugo")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spEventType.adapter = adapter

        if (eventIdToEdit != null) {
            loadEventData(eventIdToEdit!!, eventTypes)
            binding.tvTitle.text = "Uredi dogodek"
            binding.btnSaveEvent.text = "Shrani spremembe"
        } else {
            if (selectedLat != 0.0 && selectedLon != 0.0) {
                Toast.makeText(context, "Lokacija izbrana iz zemljevida!", Toast.LENGTH_SHORT).show()
                if (binding.etEventLocation.text.isEmpty()) {
                    binding.etEventLocation.setText("Označen položaj na zemljevidu")
                }
            }
        }

        binding.btnSaveEvent.setOnClickListener {
            val type = binding.spEventType.selectedItem.toString()
            val desc = binding.etEventDescription.text.toString()
            val loc = binding.etEventLocation.text.toString()

            if (desc.isNotEmpty()) {
                val finalId = eventIdToEdit ?: UUID.randomUUID().toString()

                val newEvent = ExtremeEvent(
                    id = finalId,
                    time = System.currentTimeMillis(),
                    type = type,
                    level = 1,
                    description = desc,
                    locationName = loc,
                    latitude = selectedLat,
                    longitude = selectedLon
                )

                saveOrUpdateEvent(newEvent)

                Thread {
                    sendToBlockchain(newEvent)
                }.start()
                Toast.makeText(context, "Uspešno shranjeno!", Toast.LENGTH_SHORT).show()

                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Prosim vnesite opis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadEventData(id: String, eventTypes: List<String>) {
        val file = File(requireContext().filesDir, fileName)
        if (file.exists()) {
            try {
                val content = file.readText()
                val listType = object : TypeToken<List<ExtremeEvent>>() {}.type
                val events: List<ExtremeEvent> = gson.fromJson(content, listType) ?: listOf()

                val event = events.find { it.id == id }
                if (event != null) {
                    binding.etEventDescription.setText(event.description)
                    binding.etEventLocation.setText(event.locationName)

                    val spinnerIndex = eventTypes.indexOf(event.type)
                    if (spinnerIndex >= 0) {
                        binding.spEventType.setSelection(spinnerIndex)
                    }

                    selectedLat = event.latitude
                    selectedLon = event.longitude
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendToBlockchain(event: ExtremeEvent) {
        val brokerUrl = "tcp://10.0.2.2:1883"
        val clientId = "MobileGardenClient"
        val topic = "mobilegarden/events"

        try {
            val client = MqttClient(brokerUrl, clientId, MemoryPersistence())
            client.connect()
            val content = Gson().toJson(event)
            val message = MqttMessage(content.toByteArray())
            message.qos = 2
            client.publish(topic, message)
            client.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveOrUpdateEvent(event: ExtremeEvent) {
        val context = requireContext()
        val file = File(context.filesDir, fileName)

        try {
            val eventsList: MutableList<ExtremeEvent> = if (file.exists()) {
                val content = file.readText()
                if (content.isNotEmpty()) {
                    val listType = object : TypeToken<MutableList<ExtremeEvent>>() {}.type
                    gson.fromJson(content, listType) ?: mutableListOf()
                } else mutableListOf()
            } else mutableListOf()

            val index = eventsList.indexOfFirst { it.id == event.id }
            if (index != -1) {
                eventsList[index] = event
            } else {
                eventsList.add(event)
            }

            val jsonString = gson.toJson(eventsList)
            file.writeText(jsonString)

            Toast.makeText(context, "Uspešno shranjeno!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Napaka: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}