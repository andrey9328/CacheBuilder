package org.cachebuilder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.cachebuilder.cache.CacheBuilder
import org.cachebuilder.cache.CacheStrategy
import org.cachebuilder.cache.ECacheStrategy
import org.cachebuilder.data.City
import org.cachebuilder.data.GenerateData

@ExperimentalCoroutinesApi
class MainViewModel : ViewModel() {
    val dataLive = MutableLiveData<List<City>>()
    private val cache = CacheBuilder<List<City>>()
    private val job = Job()

    fun getCity() {
        job.cancelChildren()
        viewModelScope.launch(Dispatchers.Default + job) {
            cache.getData { GenerateData.getCityList() }.collect { dataLive.postValue(it) }
        }
    }

    fun getCityAdd() {
        job.cancelChildren()
        viewModelScope.launch(Dispatchers.Default + job) {
            val strategy = CacheStrategy<List<City>>(
                    type = ECacheStrategy.ADD
            ) { new, current ->
                val mergeList = arrayListOf<City>()
                mergeList.addAll(current ?: emptyList())
                mergeList.addAll(new)
                mergeList.toList()
            }

            cache.getData(strategy =  strategy) {
                GenerateData.getCityList() }.collect { dataLive.postValue(it) }
        }
    }
}

