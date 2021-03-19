package org.cachebuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.dataLive.observe(this, {
            Log.i("City", "isSource - " + it.isSource.toString())
            it.data.forEach { item -> Log.i("City", item.toString()) }
            Log.i("City", System.currentTimeMillis().toString())
            Log.i("City", "__________________________________")
        })
        viewModel.getCity()
        findViewById<Button>(R.id.first).setOnClickListener {
            viewModel.getCity()
        }

        findViewById<Button>(R.id.second).setOnClickListener {
            Log.i("City", System.currentTimeMillis().toString())
            viewModel.getCityAdd()
        }
    }
}