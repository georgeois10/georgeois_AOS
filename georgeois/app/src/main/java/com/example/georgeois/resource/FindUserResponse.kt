package com.example.georgeois.resource

data class FindUserResponse(
    val result: Boolean,
    val user: List<Map<String, String>>
)
