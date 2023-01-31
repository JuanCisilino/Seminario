package com.avalith.seminariokotlin.repositories

import com.avalith.seminariokotlin.model.UserData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FirebaseRepo {

    private val userDb = Firebase.firestore

    private val database = FirebaseDatabase.getInstance()

    fun saveUser(user: UserData){
        userDb.collection("users").document(user.email?:"").set(
            hashMapOf(
                "email" to user.email,
                "name" to user.name,
                "photo" to user.photo
            )
        )
    }

    fun getUser(email: String) = userDb.collection("users").document(email).get()

    fun getPosts() = database.getReference("posts")
}