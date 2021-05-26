package com.example.rxjava.ui.operators

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.R
import com.rxjava2.android.samples.model.User
import com.rxjava2.android.samples.utils.Utils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class ZipExampleActivity : AppCompatActivity(){

    companion object{
        private const val TAG = "MapExampleActivity"
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

    private fun doSomeWork(){

        Observable.zip(getCricketFansObservable(), getFootballFansObservable(),
            BiFunction<List<User>, List<User>, List<User>> { cricketFans, footballFans ->
                return@BiFunction Utils.filterUserWhoLovesBoth(cricketFans, footballFans)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getObserver())

    }
    private fun getCricketFansObservable(): Observable<List<User>>{
        return Observable.create{ e->
            if(!e.isDisposed){
                e.onNext(Utils.getUserListWhoLovesCricket())
                e.onComplete()
            }
        }
    }

    private fun getFootballFansObservable(): Observable<List<User>>{
        return Observable.create{ e->
            if(!e.isDisposed){
                e.onNext(Utils.getUserListWhoLovesFootball())
                e.onComplete()
            }
        }
    }

    private fun getObserver() : Observer<List<User>>{
        return object : Observer<List<User>>{
            override fun onComplete() {
                textView.append(" onComplete \n")
                Log.d(TAG, "onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe :"+d.isDisposed)
            }

            override fun onNext(userList: List<User>) {
                textView.append("onNext ")
                for(user in userList ){
                    textView.append(" FirstName : ${user.firstname}  LastName : ${user.lastname}\n")
                }
                Log.e(TAG, "onNext : ${userList.size}")
            }

            override fun onError(e: Throwable) {
                textView.append(" onError : ${e.message}\n")
                Log.d(TAG, "onError : ${e.message}")
            }
        }
    }
}