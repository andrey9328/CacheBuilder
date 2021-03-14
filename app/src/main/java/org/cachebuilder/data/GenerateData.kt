package org.cachebuilder.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object GenerateData {
    suspend fun getCityList(): List<City> {
        delay(2000)
        return listOf(
            City(1,"London"),
            City(2,"Kiev"),
            City(3,"New York"),
            City(4,"Mexico"),
            City(5,"Kharkiv")
        )
    }

    fun subscribeSelectUpdate(): Flow<Int> {
        return flow {
            delay(16000)
            emit(2)
        }
    }

    fun subscribeDataUpdate(): Flow<CityUpdate> {
        return flow {
            delay(3000)
            emit(CityUpdate(1,"London1"))
            delay(3000)
            emit(CityUpdate(2,"Kiev new"))
            delay(3000)
            emit(CityUpdate(4,"Mexico!!!"))
        }
    }
}