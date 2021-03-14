package org.cachebuilder.cache

import kotlinx.coroutines.flow.Flow

interface ICacheBuilder<T> {
    fun getData(
            sources: List<ICacheSource<*, T>> = emptyList(),
            key: String? = null,
            strategy: CacheStrategy<T> = CacheStrategy(ECacheStrategy.DEFAULT),
            action: suspend () -> T
    ): Flow<T>

    fun getDataByKey(key: String?): T?

    fun removeByKey(key: String?)

    fun clear()
}