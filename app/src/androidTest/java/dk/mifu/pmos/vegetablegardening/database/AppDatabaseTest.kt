package dk.mifu.pmos.vegetablegardening.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dk.mifu.pmos.vegetablegardening.TestUtils.InstantExecutorExtension
import dk.mifu.pmos.vegetablegardening.TestUtils.TestLifeCycleOwner
import dk.mifu.pmos.vegetablegardening.enums.BedLocation
import dk.mifu.pmos.vegetablegardening.enums.BedLocation.Greenhouse
import dk.mifu.pmos.vegetablegardening.enums.BedLocation.Outdoors
import dk.mifu.pmos.vegetablegardening.models.Bed
import dk.mifu.pmos.vegetablegardening.models.Coordinate
import dk.mifu.pmos.vegetablegardening.models.MyPlant
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.*
import java.io.IOException


class AppDatabaseTest {
    private lateinit var bedDao: BedDao
    private lateinit var db: AppDatabase

    companion object {
        // Globally available test parameters
        private val pair1 = Pair(Coordinate(0, 0), MyPlant("Plant1"))
        private val pair2 = Pair(Coordinate(1, 1), MyPlant("Plant2"))
    }

    @BeforeEach
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        bedDao = db.bedDao()
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Nested
    inner class CreateBedTest {
        private fun createBedNameParameters(): Iterable<Pair<Bed, String>> {
            return listOf(
                    Pair(Bed("Test1", Outdoors, HashMap(), columns = 0, rows = 0), "Test1"),
                    Pair(Bed("Test2", Outdoors, HashMap(), columns = 0, rows = 0), "Test2"),
            )
        }

        @TestFactory
        fun createBedWithNameTest() = createBedNameParameters().map { (bed, name) ->
            DynamicTest.dynamicTest("Create bed with a name specified stores a bed with that name") {
                bedDao.insert(bed)
                val byName = bedDao.findByName(bed.name)
                Assertions.assertEquals(name, byName.name)
            }
        }

        private fun createBedLocationParameters(): Iterable<Pair<Bed, BedLocation>> {
            return listOf(
                    Pair(Bed("Test1", Outdoors, HashMap(), columns = 0, rows = 0), Outdoors),
                    Pair(Bed("Test2", Greenhouse, HashMap(), columns = 0, rows = 0), Greenhouse),
            )
        }

        @TestFactory
        fun createBedWithLocationTest() = createBedLocationParameters().map { (bed, location) ->
            DynamicTest.dynamicTest("Create bed with a location specified stores a bed with that location") {
                bedDao.insert(bed)
                val byName = bedDao.findByName(bed.name)
                Assertions.assertEquals(location, byName.bedLocation)
            }
        }

        private fun createBedPlantsParameters(): Iterable<Pair<Bed, Map<Coordinate, MyPlant>>> {
            return listOf(
                    Pair(Bed("Test1", Greenhouse, columns = 0, rows = 0), HashMap()),
                    Pair(Bed("Test2", Greenhouse, mapOf(pair1), columns = 0, rows = 0), mapOf(pair1)),
                    Pair(Bed("Test3", Greenhouse, mapOf(pair1, pair2), columns = 0, rows = 0), mapOf(pair1, pair2)),
            )
        }

        @TestFactory
        fun createBedWithPlantsTest() = createBedPlantsParameters().map { (bed, plants) ->
            DynamicTest.dynamicTest("Create bed with plants specified stores a bed with those plants") {
                bedDao.insert(bed)
                val byName = bedDao.findByName(bed.name)
                Assertions.assertEquals(plants, byName.plants)
            }
        }

        private fun createBedSizeParameters(): Iterable<Pair<Bed, Pair<Int, Int>>>{
            return listOf(
                    Pair(Bed("Test1", Greenhouse, columns = 0, rows = 0), Pair(0,0)),
                    Pair(Bed("Test2", Greenhouse, columns = 1, rows = 0), Pair(1,0)),
                    Pair(Bed("Test3", Greenhouse, columns = 0, rows = 1), Pair(0,1)),
                    Pair(Bed("Test4", Greenhouse, columns = 1, rows = 1), Pair(1,1)),
                    Pair(Bed("Test5", Greenhouse, columns = 2, rows = 1), Pair(2,1))
            )
        }

        @TestFactory
        fun createBedWithSizeTest() = createBedSizeParameters().map { (bed, size) ->
            DynamicTest.dynamicTest("Create bed with columns and rows specified stores a bed with those columns and rows") {
                bedDao.insert(bed)
                val byName = bedDao.findByName(bed.name)
                Assertions.assertEquals(size.first, byName.columns)
                Assertions.assertEquals(size.second, byName.rows)
            }
        }
    }

    @Nested
    inner class UpdateBedTest {
        private fun updateBedWithLocationParameters(): Iterable<Pair<Bed, BedLocation>> {
            return listOf(
                    Pair(Bed("Test1", Greenhouse, columns = 0, rows = 0), Greenhouse),
                    Pair(Bed("Test2", Outdoors, columns = 0, rows = 0), Outdoors),
                    Pair(Bed("Test3", Greenhouse, columns = 0, rows = 0), Outdoors),
                    Pair(Bed("Test4", Outdoors, columns = 0, rows = 0), Greenhouse),
            )
        }

        @TestFactory
        fun updateBedWithLocationTest() = updateBedWithLocationParameters().map { (bed, newLocation) ->
            DynamicTest.dynamicTest("Update bed with new location updates the bed to have the new location") {
                val newBed = Bed(bed.name, newLocation, columns = 0, rows = 0)
                bedDao.insert(bed)

                bedDao.update(newBed)
                val byName = bedDao.findByName(bed.name)

                Assertions.assertEquals(newLocation, byName.bedLocation)
            }
        }

        private fun updateBedWithPlantsParameters(): Iterable<Pair<Bed, Map<Coordinate, MyPlant>>> {
            return listOf(
                    Pair(Bed("Test1", Greenhouse, columns = 0, rows = 0), mapOf(pair1)),
                    Pair(Bed("Test2", Greenhouse, mapOf(pair2), columns = 0, rows = 0), mapOf(pair1, pair2)),
            )
        }

        @TestFactory
        fun updateBedWithPlantsTest() = updateBedWithPlantsParameters().map { (bed, newPlants) ->
            DynamicTest.dynamicTest("Update bed with new plants updates the bed to have the new plants") {
                val map = bed.plants.toMutableMap()
                map[pair1.first] = pair1.second
                val newBed = Bed(bed.name, bed.bedLocation, map, columns = 0, rows = 0)
                bedDao.insert(bed)

                bedDao.update(newBed)
                val byName = bedDao.findByName(bed.name)

                Assertions.assertEquals(newPlants, byName.plants)
            }
        }

        private fun updateBedWithSizeParameters(): Iterable<Pair<Bed, Pair<Int, Int>>> {
            return listOf(
                    Pair(Bed("Test1", Greenhouse, columns = 0, rows = 0), Pair(1,1)),
                    Pair(Bed("Test2", Greenhouse, columns = 2, rows = 1), Pair(1,0))
            )
        }

        @TestFactory
        fun updateBedWithSizeTest() = updateBedWithSizeParameters().map { (bed, newSize) ->
            DynamicTest.dynamicTest("Update bed with new size updates the bed to have the new size") {
                bedDao.insert(bed)
                val newBed = Bed(bed.name, Greenhouse, columns = newSize.first, rows = newSize.second)

                bedDao.update(newBed)
                val byName = bedDao.findByName(bed.name)

                Assertions.assertEquals(newSize.first, byName.columns)
                Assertions.assertEquals(newSize.second, byName.rows)
            }
        }
    }

    @Nested
    @DisplayName("Delete bed")
    inner class DeleteBedTest {
        @Test
        @DisplayName("with name deletes bed from database")
        fun deleteBedTest() {
            val name = "Test1"
            val bed = Bed(name, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed)

            bedDao.delete(name)
            val byName = bedDao.findByName(name)

            Assertions.assertNull(byName)
        }

        @Test
        @DisplayName("with name deletes only that bed from database")
        fun deleteBedDoesNotDeleteWrongTest() {
            // Bed 1
            val name1 = "Test1"
            val bed1 = Bed(name1, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed1)
            // Bed 2
            val name2 = "Test2"
            val bed2 = Bed(name2, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed2)

            // Delete bed 1
            bedDao.delete(name1)
            val byName = bedDao.findByName(name2)

            // Ensure bed 2 still in database
            Assertions.assertNotNull(byName)
        }

        @Test
        @DisplayName("that does not exist does nothing")
        fun deleteBedNoExistDoesNothingTest() {
            val nameToDelete = "Delete"
            val nameToKeep = "Keep"
            val bed = Bed(nameToKeep, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed)

            bedDao.delete(nameToDelete)
            val byName = bedDao.findByName(nameToKeep)

            Assertions.assertNotNull(byName)
        }
    }

    @Nested
    @DisplayName("Find bed")
    inner class FindBedTest {
        @Test
        @DisplayName("that exists returns not null")
        fun findBedThatExistsTest() {
            val name = "Test1"
            val bed = Bed(name, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed)

            val byName = bedDao.findByName(name)

            Assertions.assertNotNull(byName)
        }

        @Test
        @DisplayName("that does not exists returns null")
        fun findBedThatDoesNotExistTest() {
            val byName = bedDao.findByName("Test")

            Assertions.assertNull(byName)
        }
    }

    @Nested
    @DisplayName("Get all beds")
    @ExtendWith(InstantExecutorExtension::class)
    inner class GetAllTest {
        @Test
        @DisplayName("when empty returns empty list")
        fun getAllBedsEmptyTest() {
            val beds = bedDao.getAll()

            beds.observe(TestLifeCycleOwner(), {
                Assertions.assertTrue(it.isEmpty())
            })
        }

        @Test
        @DisplayName("when not empty returns list with beds")
        fun getAllBedsNotEmptyTest() {
            val name = "Test1"
            val bed = Bed(name, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed)

            val beds = bedDao.getAll()

            beds.observe(TestLifeCycleOwner(), {
                Assertions.assertTrue(it.isNotEmpty())
            })
        }

        @Test
        @DisplayName("when multiple returns list with multiple beds")
        fun getAllBedsMultipleTest() {
            val name1 = "Test1"
            val bed1 = Bed(name1, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed1)

            val name2 = "Test2"
            val bed2 = Bed(name2, Outdoors, columns = 0, rows = 0)
            bedDao.insert(bed2)

            val beds = bedDao.getAll()

            beds.observe(TestLifeCycleOwner(), {
                Assertions.assertEquals(2, it.size)
            })
        }
    }
}