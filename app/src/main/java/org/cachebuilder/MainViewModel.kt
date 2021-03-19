package org.cachebuilder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.cachebuilder.cache.*
import org.cachebuilder.data.City
import org.cachebuilder.data.GenerateData
import org.cachebuilder.sources.SelectSource

@ExperimentalCoroutinesApi
class MainViewModel : ViewModel() {
    val dataLive = MutableLiveData<CacheBuilderState<List<City>>>()
    private val cache = CacheBuilder<List<City>>()
    private val job = Job()

    fun getCity() {
        val source = listOf(SelectSource())
        viewModelScope.launch(Dispatchers.Default + job) {
            cache.getData(CacheBuilderCreate(
                sources = source,
                action = { GenerateData.getCityList() }
            )).collect { dataLive.postValue(it) }
        }
    }

    fun getCityAdd() {
        job.cancelChildren()
        val source = listOf(SelectSource())
        viewModelScope.launch(Dispatchers.Default + job) {
            cache.addData(
                CacheBuilderCreate(sources = source, action = { GenerateData.getCityList() })
            ) { add, current ->
                val mergeList = arrayListOf<City>()
                mergeList.addAll(current ?: emptyList())
                mergeList.addAll(add)
                mergeList.toList()
            }.collect { dataLive.postValue(it) }
        }
    }
}

