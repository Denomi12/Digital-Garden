package si.um.feri.mobilegarden.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import si.um.feri.mobilegarden.databinding.FragmentExtremeEventBinding
import si.um.feri.mobilegarden.models.CityLocation
import si.um.feri.mobilegarden.models.ExtremeEvent
import java.io.File
import java.util.UUID
import kotlin.random.Random

class ExtremeEventFragment : Fragment() {

    private var _binding: FragmentExtremeEventBinding? = null
    private val binding get() = _binding!!

    private val fileName = "extreme_events.json"
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtremeEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGenerate.setOnClickListener {
            generateAndSaveEvent()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun generateAndSaveEvent() {
        val eventType = listOf("Vročinski val", "Mraz", "Toča", "Neurje").random()
        var level = 1
        var description = ""

        when (eventType) {
            "Vročinski val" -> {
                val temp = Random.nextInt(30, 45)
                level = when {
                    temp >= 41 -> 3
                    temp >= 38 -> 2
                    else -> 1
                }
                description = "Temperatura zraka je dosegla $temp°C."
            }
            "Mraz" -> {
                val temp = Random.nextInt(-25, -5)
                level = when {
                    temp <= -20 -> 3
                    temp <= -10 -> 2
                    else -> 1
                }
                description = "Temperatura se je spustila na $temp°C."
            }
            "Toča" -> {
                val sizeCm = Random.nextInt(1, 10)
                level = when {
                    sizeCm >= 7 -> 3
                    sizeCm >= 4 -> 2
                    else -> 1
                }
                description = "Premer toče ocenjen na $sizeCm cm."
            }
            else -> {
                level = Random.nextInt(1, 4)
                description = "Splošni izjemni dogodek."
            }
        }

        // Bounding box za Slovenijo
        val randomLat = 45.4 + Random.nextDouble() * (46.9 - 45.4)
        val randomLon = 13.3 + Random.nextDouble() * (16.6 - 13.3)

        val knownCities = listOf(
            CityLocation("Ljubljana", 46.0569, 14.5058),
            CityLocation("Maribor", 46.5547, 15.6459),
            CityLocation("Celje", 46.2397, 15.2677),
            CityLocation("Kranj", 46.2389, 14.3555),
            CityLocation("Koper", 45.5481, 13.7302),
            CityLocation("Novo Mesto", 45.8011, 15.1703),
            CityLocation("Murska Sobota", 46.6621, 16.1706),
            CityLocation("Nova Gorica", 45.9535, 13.6484),
            CityLocation("Ptuj", 46.4199, 15.8697),
            CityLocation("Velenje", 46.3621, 15.1100)
        )

        val nearestCity = knownCities.minByOrNull { city ->
            val latDiff = city.lat - randomLat
            val lonDiff = city.lon - randomLon
            latDiff * latDiff + lonDiff * lonDiff
        } ?: knownCities[0]

        val newEvent = ExtremeEvent(
            id = UUID.randomUUID().toString(),
            time = System.currentTimeMillis(),
            type = eventType,
            level = level,
            description = description,
            locationName = nearestCity.name,
            latitude = randomLat,
            longitude = randomLon
        )

        saveEventToFile(newEvent)

        binding.tvOutput.text = "GENERIRANO:\nTip: ${newEvent.type}\nLokacija: ${newEvent.locationName} (blizu)\nKoordinate: ${"%.4f".format(newEvent.latitude)}, ${"%.4f".format(newEvent.longitude)}"
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

            Toast.makeText(context, "Dogodek shranjen (Pretty JSON)!", Toast.LENGTH_SHORT).show()

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