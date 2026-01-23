package si.um.feri.mobilegarden.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import si.um.feri.mobilegarden.databinding.FragmentEventListBinding
import si.um.feri.mobilegarden.fileUtils.EventFileUtils
import si.um.feri.mobilegarden.models.EventAdapter
import si.um.feri.mobilegarden.R


class EventListFragment : Fragment(R.layout.fragment_event_list) {
    private lateinit var binding: FragmentEventListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventsList = EventFileUtils.loadEvents(requireContext(), "extreme_events.json")

        val navController = findNavController()

        val adapter = EventAdapter(eventsList) {
            event ->
            val bundle = Bundle().apply {
                putString("arg_event_id", event.id)
            }
            navController.navigate(R.id.action_eventListFragment_to_addExtremeEventFragment, bundle)
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
    }
}