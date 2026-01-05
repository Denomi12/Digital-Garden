package si.um.feri.mobilegarden.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import si.um.feri.mobilegarden.databinding.FragmentAddExtremeEventBinding
import si.um.feri.mobilegarden.models.ExtremeEvent
import java.io.File
import java.util.UUID

class AddExtremeEventFragment : Fragment() {

    private var _binding: FragmentAddExtremeEventBinding? = null
    private val binding get() = _binding!!

    private val fileName = "extreme_events.json"
    private val gson = GsonBuilder().setPrettyPrinting().create()

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

        binding.btnSaveEvent.setOnClickListener {
            val type = binding.spEventType.selectedItem.toString()

            val desc = binding.etEventDescription.text.toString()
            val loc = binding.etEventLocation.text.toString()

            if (desc.isNotEmpty()) {
                val newEvent = ExtremeEvent(
                    id = UUID.randomUUID().toString(),
                    time = System.currentTimeMillis(),
                    type = type,
                    level = 1,
                    description = desc,
                    locationName = loc,
                    latitude = 0.0,
                    longitude = 0.0
                )

                saveEventToFile(newEvent)
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Prosim vnesite opis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEventToFile(event: ExtremeEvent) {
        val context = requireContext()
        val file = File(context.filesDir, fileName)

        try {
            val eventsList: MutableList<ExtremeEvent> = if (file.exists()) {
                val content = file.readText()
                if (content.isNotEmpty()) {
                    val listType = object : TypeToken<MutableList<ExtremeEvent>>() {}.type
                    gson.fromJson(content, listType) ?: mutableListOf()
                } else {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }

            eventsList.add(event)
            val jsonString = gson.toJson(eventsList)
            file.writeText(jsonString)

            Toast.makeText(context, "Dogodek uspešno shranjen!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Napaka pri shranjevanju: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}