package si.um.feri.mobilegarden.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import si.um.feri.mobilegarden.databinding.EventRowItemBinding

class EventAdapter(private val events: MutableList<ExtremeEvent>):
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = EventRowItemBinding.bind(view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        val binding = EventRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding.root)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

    }

    override fun getItemCount(): Int {
        return events.size
    }

}