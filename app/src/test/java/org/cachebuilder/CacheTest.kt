package org.cachebuilder

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.cachebuilder.cache.CacheBuilder
import org.cachebuilder.cache.CacheStrategy
import org.cachebuilder.cache.ECacheStrategy
import org.cachebuilder.cache.ICacheBuilder
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class CacheTest {
    private val cache: ICacheBuilder<String> = CacheBuilder()

    @Test
    fun testSave() {
        val key = "CACHE_KEY"
        val value = "2"
        val emit = cache.getData(key = key) {
            delay(1000)
            value
        }

        runBlocking {
           emit.collect {
                assertEquals(value, it)
            }
        }
    }

    @Test
    fun testGetByKey() {
        val key = "CACHE_KEY"
        val value = "2"
        val emit = cache.getData(key = key) {
            delay(1000)
            value
        }

        runBlocking {
            emit.collect {
                assertEquals(cache.getDataByKey(key), value)
            }
        }
    }

    @Test(timeout = 1500)
    fun testReuseData() {
        val key = "CACHE_KEY"
        val value = "2"
        val emit = cache.getData(key = key) {
            delay(1000)
            value
        }

        runBlocking {
            emit.collect {
                emit.collect { emit -> assertEquals(value, emit) }
            }
        }
    }

    @Test
    fun testReplaceData() {
        val key = "CACHE_KEY"
        val value = "2"
        val newValue = "3"

        runBlocking {
            cache.getData(key = key) {
                delay(1000)
                value
            }.collect {
                assertEquals(value, it)
                cache.getData(key = key, strategy = CacheStrategy(ECacheStrategy.REPLACE)) {
                    delay(1000)
                    newValue
                }.collect { emit -> assertEquals(newValue, emit) }
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExceptionAdd() {
        val value = "2"

        runBlocking {
            cache.getData(strategy = CacheStrategy(ECacheStrategy.ADD)) {
                delay(1000)
                value
            }.collect {
                assertEquals(value, it)
            }
        }
    }

    @Test
    fun testAddStrategy() {
        val key = "CACHE_KEY"
        val value = "2"
        val newValue = "3"

        runBlocking {
            cache.getData(key = key) {
                delay(1000)
                value
            }.collect {
                assertEquals(value, it)
                cache.getData(key = key, strategy = CacheStrategy(ECacheStrategy.ADD)
                { new, current -> new + current }) {
                    delay(1000)
                    newValue
                }.collect { emit -> assertEquals(newValue + value, emit) }
            }
        }
    }
}