package org.cachebuilder.cache

data class CacheBuilderState<T>(
    val key: String?,
    val data: T,
    val isSource: Boolean)