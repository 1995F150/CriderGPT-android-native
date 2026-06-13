package app.cridergpt.android

import android.app.Application

class CriderGPTApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        // Expose application instance for simple access to context where needed.
        // Prefer dependency injection in production code.
        lateinit var instance: CriderGPTApplication
            private set
    }
}
