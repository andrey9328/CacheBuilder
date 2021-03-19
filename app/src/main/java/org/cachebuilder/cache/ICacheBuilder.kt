package org.cachebuilder.cache

import kotlinx.coroutines.flow.Flow

interface ICacheBuilder<T> {
    fun getData(builder: CacheBuilderCreate<T>): Flow<CacheBuilderState<T>>

    fun addData(builder: CacheBuilderCreate<T>, action: ((add: T, current: T?) -> T)): Flow<CacheBuilderState<T>>

    fun updateData(builder: CacheBuilderCreate<T>): Flow<CacheBuilderState<T>>

    fun getDataByKey(key: String?): T?

    fun removeByKey(key: String?)

    fun clear()
}