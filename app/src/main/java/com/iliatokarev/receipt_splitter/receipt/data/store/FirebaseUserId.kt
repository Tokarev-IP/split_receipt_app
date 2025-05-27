package com.iliatokarev.receipt_splitter.receipt.data.store

import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class FirebaseUserId : FirebaseUserIdInterface {

    override fun getUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }
}

interface FirebaseUserIdInterface{
    fun getUserId(): String?
}