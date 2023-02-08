package com.avalith.seminariokotlin.repositories

import android.net.Uri
import com.avalith.seminariokotlin.model.UserData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseRepo {

    private val userDb = Firebase.firestore

    private val database = FirebaseDatabase.getInstance()

    private val storageReference = FirebaseStorage.getInstance().getReference("Post Images")

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

    fun getStorageReference() = storageReference

    fun saveImageToDb(reference: StorageReference, uri: Uri) = reference.putFile(uri)

}