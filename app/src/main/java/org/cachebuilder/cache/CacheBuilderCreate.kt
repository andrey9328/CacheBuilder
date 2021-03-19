package org.cachebuilder.cache

data class CacheBuilderCreate<T>(
    val key: String? = null,
    val sources: List<ICacheSource<*, T>> = emptyList(),
    val action: suspend () -> T
)