package dk.mifu.pmos.vegetablegardening.viewgarden

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.mifu.pmos.vegetablegardening.R
import dk.mifu.pmos.vegetablegardening.creategarden.CreateBedActivity
import dk.mifu.pmos.vegetablegardening.databinding.FragmentGardenOverviewBinding
import dk.mifu.pmos.vegetablegardening.enums.Location.*
import dk.mifu.pmos.vegetablegardening.models.Bed
import dk.mifu.pmos.vegetablegardening.viewmodels.BedViewModel
import dk.mifu.pmos.vegetablegardening.viewmodels.GardenViewModel

class GardenOverviewFragment : Fragment() {
    private lateinit var binding: FragmentGardenOverviewBinding

    private val gardenViewModel: GardenViewModel by activityViewModels()
    private val bedViewModel: BedViewModel by activityViewModels()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGardenOverviewBinding.inflate(inflater, container, false)

        val recyclerView = binding.gardensRecyclerView

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        gardenViewModel.beds.observe(viewLifecycleOwner, {
            val adapter = GardenOverviewAdapter(it)
            recyclerView.adapter = adapter
        })

        binding.newLocationBtn.setOnClickListener {
            val createIntent = Intent(context, CreateBedActivity::class.java)
            startActivity(createIntent)
        }

        return binding.root
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bedImage: ImageButton = view.findViewById(R.id.bed_image_button)
        val bedName: TextView = view.findViewById(R.id.bed_name_text)
        var bed: Bed? = null

        init {
            bedImage.setOnClickListener {
                bedViewModel.setBed(bed!!)
                requireView().findNavController().navigate(GardenOverviewFragmentDirections.seeBedAction())
            }
        }
    }

    private inner class GardenOverviewAdapter(private val dataSet: List<Bed>): RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_garden, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bedName.text = dataSet[position].name
            holder.bed = dataSet[position]

            when (dataSet[position].location) {
                Outdoors -> holder.bedImage.setImageResource(R.drawable.outdoors_normal)
                Greenhouse -> holder.bedImage.setImageResource(R.drawable.greenhouse_normal)
            }
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }
}