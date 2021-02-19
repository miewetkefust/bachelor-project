package dk.mifu.pmos.vegetablegardening.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dk.mifu.pmos.vegetablegardening.dao.GardenRepository
import dk.mifu.pmos.vegetablegardening.database.AppDatabase
import dk.mifu.pmos.vegetablegardening.models.Garden

class GardenViewModel(application: Application) : AndroidViewModel(application) {
    private val gardenDb = AppDatabase.getDatabase(getApplication()).gardenDao()
    private val repository = GardenRepository(gardenDb)
    val gardens: LiveData<List<Garden>> = repository.getAllGardens()
}