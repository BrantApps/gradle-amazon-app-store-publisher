package io.github.angelstudios.ext

import retrofit2.Response

internal fun <T> Response<T>.bodyOrError() = body() ?: error("Response missing body: HTTP ${code()}")
