package com.avalith.seminariokotlin.model

import java.util.*

data class Post(
    var userName: String? = null,
    val userImage: String? = null,
    var image: String? = null,
    var description: String? = null,
    var date: String? = null,
    var timestamp: Long? = null
) {

    fun map(date: String, description: String, url: String, userData: UserData) = Post(
        date = date,
        userName = userData.name,
        userImage = userData.photo,
        description = description,
        image = url,
        timestamp = Calendar.getInstance().timeInMillis
    )
}
