package org.cachebuilder.sources

import kotlinx.coroutines.flow.Flow
import org.cachebuilder.cache.ICacheSource
import org.cachebuilder.data.City
import org.cachebuilder.data.GenerateData

class SelectSource: ICacheSource<Int, List<City>> {
    override fun createFlow(current: List<City>?): Flow<Int> {
        return GenerateData.subscribeSelectUpdate()
    }

    override suspend fun mapInputFlow(newData: Int, current: List<City>?): List<City>? {
        return current?.map { it.copy(isSelected = it.id == newData) }
    }
}