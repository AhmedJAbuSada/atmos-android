package dev.ahmedabusada.atmos.data.di

import javax.inject.Qualifier

/** The dispatcher for blocking I/O. Injected, so tests can replace it (CODING_STANDARDS). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** The User-Agent sent with every request, for example `Atmos/1.0.0 (Android)`. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserAgent
