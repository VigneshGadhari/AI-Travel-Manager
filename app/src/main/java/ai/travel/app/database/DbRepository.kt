package ai.travel.app.database

import ai.travel.app.database.travel.TripsDao
import ai.travel.app.database.travel.TripsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DatabaseRepo(private val tripsDao: TripsDao) {
    fun getTrips(day: String, destination: String): Flow<List<TripsEntity?>> =
        tripsDao.getTrips(day, destination)

    fun getMoreInfo( destination: String): Flow<List<TripsEntity?>> =
        tripsDao.getMoreInfo(destination)

    val allTrips: Flow<List<TripsEntity?>> = tripsDao.getAllTrips()
    fun getCurrentTrip(destination: String): Flow<List<TripsEntity?>> = tripsDao.getCurrentTrip(destination)

    fun distinctDays(destination: String): Flow<List<String?>> = tripsDao.getUniqueDays(destination)

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertTrip(tourDetails: TripsEntity) {
        coroutineScope.launch {
            tripsDao.insertTrip(tourDetails)
        }
    }

    fun insertAllTrips(trips: List<TripsEntity>) {
        coroutineScope.launch {
            tripsDao.insertAllTrips(trips)
        }
    }


}