package com.example.rxjava.ui.operators

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SimpleExampleActivity : AppCompatActivity(){

    companion object{
        private const val TAG = "SimpleExampleActivity"
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
        getObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getObserver())

    }
    private fun getObservable(): Observable<String>{
        return Observable.just("Cricket", "FootBall")
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