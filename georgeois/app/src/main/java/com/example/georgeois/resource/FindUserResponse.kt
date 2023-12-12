package com.example.georgeois.resource

data class FindUserResponse(
    val result: Boolean,
    val ids: List<Map<String, String>>
)
