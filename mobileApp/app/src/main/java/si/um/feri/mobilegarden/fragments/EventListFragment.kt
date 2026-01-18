package si.um.feri.mobilegarden.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import si.um.feri.mobilegarden.databinding.FragmentEventListBinding
import si.um.feri.mobilegarden.fileUtils.EventFileUtils
import si.um.feri.mobilegarden.models.EventAdapter

class EventListFragment : Fragment() {
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
        val adapter = EventAdapter(eventsList)

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
    }
}