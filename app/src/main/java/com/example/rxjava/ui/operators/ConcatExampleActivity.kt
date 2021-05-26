package com.example.rxjava.ui.operators

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ConcatExampleActivity : AppCompatActivity(){

    companion object{
        private const val TAG = "ConcatExampleActivity"
    }
    private lateinit var btn : Button;
    private lateinit var textView :TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        btn = findViewById(R.id.btn)
        textView = findViewById(R.id.textView)
        btn.setOnClickListener { doSomeWork() }
    }

    fun doSomeWork(){
        val observableA = Observable.fromArray("A1", "A2", "A3", "A4")
        val observableB = Observable.fromArray("B1", "B2", "B3", "B4")
        Observable.concat(observableA, observableB).subscribe(getObserver())

    }

    private fun getObserver() : Observer<String>{
        return object : Observer<String>{
            override fun onComplete() {
               textView.append(" onComplete \n")
               Log.d(TAG, "onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe :"+d.isDisposed)
            }

            override fun onNext(value: String) {
                textView.append("onNext : value : $value \n")
                Log.d(TAG,"onNext : value : $value \n")
            }

            override fun onError(e: Throwable) {
                textView.append(" onError : ${e.message}\n")
                Log.d(TAG, "onError : ${e.message}")
            }

        }
    }
}