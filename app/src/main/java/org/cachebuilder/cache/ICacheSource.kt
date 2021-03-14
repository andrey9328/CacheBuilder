package org.cachebuilder.cache

import kotlinx.coroutines.flow.Flow

interface ICacheSource<I, O> {
    fun createFlow(current: O?): Flow<I>

    suspend fun mapInputFlow(newData: I, current: O?): O?

    @Suppress("UNCHECKED_CAST")
    suspend fun updateCache(newData: Any?, current: O?): O? {
        return mapInputFlow(newData as I, current)
    }
}