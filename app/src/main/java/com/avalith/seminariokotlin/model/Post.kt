package com.avalith.seminariokotlin.model

data class Post(
    var userName: String? = null,
    val userImage: String? = null,
    var image: String? = null,
    var description: String? = null,
    var date: String? = null,
    var timestamp: Long? = null
)
