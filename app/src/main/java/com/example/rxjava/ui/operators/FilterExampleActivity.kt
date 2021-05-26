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

class FilterExampleActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "FilterExampleActivity"
    }

    private lateinit var btn: Button
    private lateinit var textView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        btn = findViewById(R.id.btn)
        textView = findViewById(R.id.textView)

        btn.setOnClickListener { doSomeWork() }
    }

    private fun doSomeWork(){
        getObservable()
            .filter { value ->
                return@filter value%2 ==0
            }
            .subscribe(getObserver())

    }

    private fun getObservable() : Observable<Int>{
        return Observable.just(1,2,3,4,6)
    }

    private fun getObserver() : Observer<Int>{
        return object : Observer<Int>{
            override fun onComplete() {
                textView.append("onComplete ")
                Log.d(TAG, "onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, " onSubscribe "+d.isDisposed)
            }

            override fun onNext(t: Int) {
                textView.append(" onNext : value : $t \n")
                Log.d(TAG, "onNext : $t")
            }

            override fun onError(e: Throwable) {
                textView.append(" onError : ${e.message} \n")
                Log.d(TAG, " onError : ${e.message}")
            }
        }
    }
}