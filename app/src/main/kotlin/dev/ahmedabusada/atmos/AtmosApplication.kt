package dev.ahmedabusada.atmos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The composition root. Hilt generates the dependency graph from here, which is how the
 * :app module connects :domain use cases to their :data implementations.
 */
@HiltAndroidApp
class AtmosApplication : Application()
