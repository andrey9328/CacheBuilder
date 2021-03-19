package org.cachebuilder.cache

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class CacheBuilder<T>: ICacheBuilder<T> {
    private val cacheMap = hashMapOf<String?, CacheBuilderState<T>?>()
    private var currentKey: String? = null

    override fun getData(builder: CacheBuilderCreate<T>): Flow<CacheBuilderState<T>> {
        return flow {
            when {
                cacheMap[builder.key] == null -> {
                    currentKey = builder.key
                    cacheMap[builder.key] = createData(builder)
                    emit(cacheMap[builder.key])
                    attachSources(builder.key, builder.sources).collect { emit(it) }
                }

                currentKey != builder.key -> {
                    emit(cacheMap[builder.key])
                    attachSources(builder.key, builder.sources).collect { emit(it) }
                }

                else -> {
                    val cacheData = CacheBuilderState(
                        key = builder.key,
                        data = cacheMap[builder.key]!!.data,
                        isSource = false
                    )
                    emit(cacheData)
                }
            }
        }.filterNotNull()
    }

    override fun addData(builder: CacheBuilderCreate<T>, action: ((add: T, current: T?) -> T)): Flow<CacheBuilderState<T>> {
        return flow {
            currentKey = builder.key
            val data = action.invoke(createData(builder).data, cacheMap[builder.key]?.data)
            cacheMap[builder.key] = CacheBuilderState(
                key = builder.key,
                data = data,
                isSource = false
            )
            emit(cacheMap[builder.key])
            attachSources(builder.key, builder.sources).collect { emit(it) }
        }.filterNotNull()
    }

    override fun updateData(builder: CacheBuilderCreate<T>): Flow<CacheBuilderState<T>> {
        return flow {
            currentKey = builder.key
            cacheMap[builder.key] = CacheBuilderState(
                key = builder.key,
                data = createData(builder).data,
                isSource = false
            )
            emit(cacheMap[builder.key])
            attachSources(builder.key, builder.sources).collect { emit(it) }
        }.filterNotNull()
    }

    private suspend fun createData(builder: CacheBuilderCreate<T>): CacheBuilderState<T> {
        val data = builder.action.invoke()
        return CacheBuilderState(
            key = builder.key,
            data = data,
            isSource = false
        )
    }

    override fun getDataByKey(key: String?): T? = cacheMap[key]?.data

    override fun removeByKey(key: String?) {
        cacheMap.remove(key)
    }

    override fun clear() {
        cacheMap.clear()
    }

    private suspend fun attachSources(key: String? = null, sources: List<ICacheSource<*, T>>): Flow<CacheBuilderState<T>> {
        if (sources.isEmpty()) return emptyFlow()
        return sources.map { source -> source.createFlow(cacheMap[key]?.data).map { source.updateCache(it, cacheMap[key]?.data) } }
                .merge()
                .filterNotNull()
                .map { CacheBuilderState<T>(
                    key = key,
                    data = it,
                    isSource = true
                ) }
                .onEach { cacheMap[key] = it }
    }
}