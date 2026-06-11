package com.dartscore

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Punkt startowy grafu zależności Hilta. Wskazany w AndroidManifest jako android:name.
@HiltAndroidApp
class DartScoreApplication : Application()
