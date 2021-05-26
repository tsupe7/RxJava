package com.rxjava2.android.samples.model


data class User(var id: Long = 0L,
                var firstname: String,
                var lastname: String,
                var isFollowing: Boolean = false)
