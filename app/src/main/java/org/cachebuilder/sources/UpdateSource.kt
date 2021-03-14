package org.cachebuilder.sources

import kotlinx.coroutines.flow.Flow
import org.cachebuilder.cache.ICacheSource
import org.cachebuilder.data.City
import org.cachebuilder.data.CityUpdate
import org.cachebuilder.data.GenerateData

class UpdateSource: ICacheSource<CityUpdate, List<City>> {
    override fun createFlow(current: List<City>?): Flow<CityUpdate> {
        return GenerateData.subscribeDataUpdate()
    }

    override suspend fun mapInputFlow(newData: CityUpdate, current: List<City>?): List<City>? {
        return current?.map { if (it.id == newData.id) it.copy(name = newData.name) else it }
    }
}