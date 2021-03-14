package org.cachebuilder.cache

data class CacheStrategy<T>(
        val type: ECacheStrategy,
        private val _action: ((add: T, current: T?) -> T)? = null
) {
    val action: ((add: T, current: T?) -> T)?
    get() {
        if (type == ECacheStrategy.ADD && _action == null)
            throw IllegalArgumentException("With add strategy action not be null")
        return _action
    }
}