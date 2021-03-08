package dk.mifu.pmos.vegetablegardening.fragments.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.mifu.pmos.vegetablegardening.R
import dk.mifu.pmos.vegetablegardening.databinding.FragmentChoosePlantBinding
import dk.mifu.pmos.vegetablegardening.viewmodels.BedViewModel
import dk.mifu.pmos.vegetablegardening.helpers.predicates.LocationPredicate
import dk.mifu.pmos.vegetablegardening.models.Plant
import dk.mifu.pmos.vegetablegardening.viewmodels.PlantViewModel
import java.util.*

class ChoosePlantDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentChoosePlantBinding

    private val plantViewModel: PlantViewModel by activityViewModels()
    private val bedViewModel: BedViewModel by activityViewModels()

    private val args: ChoosePlantDialogFragmentArgs by navArgs()

    private var adapter : PlantAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentChoosePlantBinding.inflate(inflater, container, false)

        val recyclerView = binding.choosePlantRecyclerView
        createList(recyclerView)

        val search = binding.searchPlantEdittext
        search.requestFocus()
        setupSearch(search)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val params = dialog!!.window!!.attributes

        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT

        dialog!!.window!!.attributes = params
    }

    private fun createList(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        plantViewModel.plants.observe(viewLifecycleOwner, {
            val plants = it
                    .filter(LocationPredicate(bedViewModel.bedLocation))
                    .filter(args.predicate)
            adapter = PlantAdapter(plants)
            recyclerView.adapter = adapter
        })
    }

    private fun setupSearch(search: EditText) {
        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { /* Do nothing */ }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { /* Do nothing */ }

            override fun afterTextChanged(s: Editable?) {
                adapter?.filter?.filter(s.toString())
            }
        })
    }

    // Recyclerview functionality

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plantName: TextView = view.findViewById(R.id.choose_plant_row_item_text)
        lateinit var plant : Plant

        init {
            view.setOnClickListener {
                bedViewModel.plants?.set(args.coordinate, plant)
                navigateBack()
            }
        }
    }

    private inner class PlantAdapter(private val dataSet: List<Plant>) : RecyclerView.Adapter<ViewHolder>(), Filterable {
        private var flowingData: List<Plant> = dataSet


        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.list_item_plant, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.plantName.text = flowingData[position].name
            viewHolder.plant = flowingData[position]
        }

        override fun getItemCount() = flowingData.size

        // Search functionality

        private val filter: Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint == null || constraint.isEmpty())
                    results.values = dataSet
                else {
                    val pattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    results.values = dataSet.filter { plant -> plant.name.toLowerCase(Locale.getDefault()).contains(pattern) }
                }
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                flowingData = results!!.values as List<Plant>
                notifyDataSetChanged()
            }
        }

        override fun getFilter(): Filter {
            return filter
        }
    }

    private fun navigateBack() {
        dialog?.dismiss()
    }
}