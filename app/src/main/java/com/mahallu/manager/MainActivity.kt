package com.mahallu.manager

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.repository.LanguageController
import com.mahallu.manager.core.database.repository.ThemeModeController
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.MahalluTheme
import com.mahallu.manager.navigation.MahalluNavGraph
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeModeController: ThemeModeController

    @Inject
    lateinit var languageController: LanguageController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appLanguage by languageController.language.collectAsStateWithLifecycle()
            LaunchedEffect(appLanguage) {
                applyLanguage(appLanguage)
            }

            val themeMode by themeModeController.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            MahalluTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LocalMahalluColors.current.background
                ) {
                    MahalluNavGraph()
                }
            }
        }
    }

    /**
     * Wraps the base context with the persisted language before the activity's
     * resources are created, so [androidx.compose.ui.res.stringResource] resolves
     * the selected locale. Works with [ComponentActivity] on every API level.
     */
    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences(LanguageController.PREFS_FILE, Context.MODE_PRIVATE)
            .getString(LanguageController.KEY, LanguageController.DEFAULT) ?: LanguageController.DEFAULT
        val locale = if (lang == "ml") Locale("ml") else Locale("en")
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    private fun applyLanguage(lang: String) {
        val target = if (lang == "ml") Locale("ml") else Locale("en")
        val current = resources.configuration.locales[0].language
        if (current == target.language) return
        recreate()
    }
}
