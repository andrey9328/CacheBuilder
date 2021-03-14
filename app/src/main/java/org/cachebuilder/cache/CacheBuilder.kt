package org.cachebuilder.cache

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.cachebuilder.data.City

@ExperimentalCoroutinesApi
class CacheBuilder<T>: ICacheBuilder<T> {
    private val cacheMap = hashMapOf<String?, T?>()

    override fun getData(
            sources: List<ICacheSource<*, T>>,
            key: String?,
            strategy: CacheStrategy<T>,
            action: suspend () -> T
    ): Flow<T> {
        return flow {
            emit(createData(key, strategy, action))
            if (sources.isNotEmpty()) attachSources(key, sources).collect { emit(it) }
        }.filterNotNull()
    }

    override fun getDataByKey(key: String?): T? = cacheMap[key]

    override fun removeByKey(key: String?) {
        cacheMap.remove(key)
    }

    override fun clear() {
        cacheMap.clear()
    }

    private suspend fun createData(
            key: String? = null,
            strategy: CacheStrategy<T>,
            action: suspend () -> T
    ): T? {
        return when {
            strategy.type == ECacheStrategy.DEFAULT && cacheMap[key] == null -> {
                cacheMap.applyValue(key) { action.invoke() }
            }
            strategy.type == ECacheStrategy.ADD -> {
                cacheMap.applyValue(key) { strategy.action?.invoke(action.invoke(), cacheMap[key]) }
            }
            strategy.type == ECacheStrategy.REPLACE && strategy.action == null -> {
                cacheMap.applyValue(key) { action.invoke() }
            }
            strategy.type == ECacheStrategy.REPLACE -> {
                cacheMap.applyValue(key) { strategy.action?.invoke(action.invoke(), cacheMap[key]) }
            }
            else -> cacheMap[key]
        }
    }

    private suspend fun attachSources(key: String? = null, sources: List<ICacheSource<*, T>>): Flow<T> {
        if (sources.isEmpty()) return emptyFlow()
        return sources.map { source -> source.createFlow(cacheMap[key]).map { source.updateCache(it, cacheMap[key]) } }
                .merge()
                .filterNotNull()
                .onEach { cacheMap[key] = it }
    }

    private suspend fun <T>HashMap<String?, T?>.applyValue(
            key: String?,
            value: suspend () -> T
    ): T? {
        this[key] = value.invoke()
        return this[key]
    }
}