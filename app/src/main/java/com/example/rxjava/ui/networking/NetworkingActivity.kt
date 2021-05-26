package com.example.rxjava.ui.networking

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.rxjava.R
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.rxjava2.android.samples.model.ApiUser
import com.rxjava2.android.samples.model.User
import com.rxjava2.android.samples.model.UserDetail
import com.rxjava2.android.samples.utils.Utils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class NetworkingActivity  : AppCompatActivity() {

    companion object{
        private const val TAG = "NetworkingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_networking)
    }

    private fun getCricketFansObservable() : Observable<List<User>>{
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
                .build()
                .getObjectListObservable(User::class.java)
                .subscribeOn(Schedulers.io())
    }

    private fun getFoodballFansObservable() : Observable<List<User>>{
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
                .build()
                .getObjectListObservable(User::class.java)
                .subscribeOn(Schedulers.io())
    }

    private fun getAllMyFriendObservable() : Observable<List<User>>{
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectListObservable(User::class.java)
    }

    private fun getUserListObservable() : Observable<List<User>>{
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getObjectListObservable(User::class.java)
    }

    fun map(view : View){
        Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectObservable(ApiUser::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {apiUser->

                    User(apiUser.id, apiUser.firstname, apiUser.lastname)
                }
                .subscribe(object :  Observer<User> {
                    override fun onComplete() {
                        Log.d(TAG, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe "+d.isDisposed)
                    }

                    override fun onNext(user: User) {
                        Log.d(TAG, "user : $user")
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(TAG, e)
                    }

                })
    }

    private fun getUserWhoLoveBoth(){
        Observable.zip(getCricketFansObservable(), getFoodballFansObservable(),
        BiFunction<List<User>, List<User>, List<User>>{ crickeFans, foolballFans->
            return@BiFunction Utils.filterUserWhoLovesBoth(crickeFans, foolballFans)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :Observer<List<User>>{
                    override fun onComplete() {
                        Log.d(TAG, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe "+d.isDisposed)
                    }

                    override fun onNext(users: List<User>) {
                        // do anything with user who loves both
                        Log.d(TAG, "userList size : " + users.size)
                        for (user in users) {
                            Log.d(TAG, "user : $user")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(TAG, e)
                    }
                })

    }

    fun zip(view : View){
        getUserWhoLoveBoth()
    }

    fun flatMapAndFilter(view : View){
        getAllMyFriendObservable()
                .flatMap { userList->
                    Observable.fromIterable(userList)
                }
                .filter { user->
                    return@filter user.isFollowing
                }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<User>>{
                    override fun onComplete() {
                        Log.d(TAG, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe "+d.isDisposed)
                    }

                    override fun onNext(users: List<User>) {
                        // only the user who is following will be in the list
                        Log.d(TAG, "userList size : " + users.size)
                        for (user in users) {
                            Log.d(TAG, "user : $user")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(TAG, e)
                    }

                })

    }

    fun flatMap(view: View){
        getUserListObservable()
                .flatMap { userList ->
                    Observable.fromIterable(userList)
                }
                .flatMap { user->
                    getUserDetailObservable(user.id)
                }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<UserDetail>>{
                    override fun onComplete() {
                        Log.d(TAG, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(TAG, "onSubscribe "+d.isDisposed)
                    }

                    override fun onNext(userDetailList: List<UserDetail>) {
                        // do anything with userDetail list

                        Log.d(TAG, "userDetailList size : " + userDetailList.size)
                        for (userDetail in userDetailList) {
                            Log.d(TAG, "userDetail : $userDetail")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Utils.logError(TAG, e)
                    }

                })

    }


    private fun getUserDetailObservable(id: Long): Observable<UserDetail> {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", id.toString())
                .build()
                .getObjectObservable(UserDetail::class.java)
    }
}