package com.example.georgeois.resource

sealed class FieldState<T>(
    val data: T?= null,
    val message: String? = null
) {
    class Success<T>(data: T?): FieldState<T>(data)
    class Fail<T>(message: String) : FieldState<T>(message = message)
    class Error<T>(message: String) : FieldState<T>(message = message)
}
