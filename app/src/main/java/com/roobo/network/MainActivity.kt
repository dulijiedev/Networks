package com.roobo.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.dol.rxlifecycle.lifeOnMain
import com.roobo.network.api.getBannerJson
import com.roobo.network.api.getMusicList
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {
    lateinit var tvInfo: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvInfo = findViewById(R.id.tv_info)

        testNetwork()
    }

    private fun testNetwork() {
        getBannerJson()
            .lifeOnMain(this)
            .subscribe(Consumer {
                Log.d("Networklib", "subscribe ${it.get()}")
                tvInfo.text = "${it.get().firstOrNull()?.desc}"
            }, Consumer {
                Log.d("Networklib", "throw ${it.cause} ${it.message} ")
            }, Action {
                Log.d("Networklib", "complete")
            })
    }
}