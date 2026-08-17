package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.data.model.CitizenCategory
import com.example.data.model.GovDocument
import com.example.data.model.Scheme
import com.example.data.model.SchemeSector
import com.example.ui.MainViewModel

/**
 * CompositionLocal providing the currently selected language name (e.g., "Hindi (हिंदी)", "English", etc.)
 */
val LocalAppLanguage = compositionLocalOf { "Hindi (हिंदी)" }

/**
 * CompositionLocal providing the structured [LanguageOption] metadata object (code, displayName, flagEmoji).
 */
val LocalLanguageOption = compositionLocalOf { LanguageSelectionState.HINDI }

/**
 * CompositionLocal providing a callback lambda to switch languages anywhere within the Compose hierarchy.
 */
val LocalSetAppLanguage = staticCompositionLocalOf<(String) -> Unit> { {} }

/**
 * Root Composable wrapper that provides the current language state and switch actions
 * down the entire composition tree via CompositionLocalProvider.
 *
 * When the user selects a new language in any screen or dropdown, all descendant Composables
 * reading from [LocalAppLanguage], [LocalLanguageOption], or using [localizedText] will
 * instantly recompose with updated translations.
 */
@Composable
fun ProvideAppLanguage(
    viewModel: MainViewModel,
    content: @Composable () -> Unit
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val languageOption = remember(selectedLanguage) {
        LanguageSelectionState.fromString(selectedLanguage)
    }

    CompositionLocalProvider(
        LocalAppLanguage provides selectedLanguage,
        LocalLanguageOption provides languageOption,
        LocalSetAppLanguage provides { newLang -> viewModel.setLanguage(newLang) }
    ) {
        content()
    }
}

/**
 * Composable helper function to retrieve localized text for a string resource key.
 */
@Composable
fun localizedText(key: String): String {
    val lang = LocalAppLanguage.current
    return LanguageTranslator.getLocalizedText(key, lang)
}

/**
 * Composable extension to get the localized title of a [Scheme].
 */
@Composable
fun Scheme.localizedTitle(): String {
    val lang = LocalAppLanguage.current
    return LanguageTranslator.getLocalizedTitle(this, lang)
}

/**
 * Composable extension to get the localized short description of a [Scheme].
 */
@Composable
fun Scheme.localizedDescription(): String {
    val lang = LocalAppLanguage.current
    return LanguageTranslator.getLocalizedShortDesc(this, lang)
}

/**
 * Composable extension to get the localized name of a [CitizenCategory].
 */
@Composable
fun CitizenCategory.localizedName(): String {
    val lang = LocalAppLanguage.current
    return LanguageTranslator.getLocalizedCategory(this, lang)
}

/**
 * Composable extension to get the localized name of a [SchemeSector].
 */
@Composable
fun SchemeSector.localizedName(): String {
    val lang = LocalAppLanguage.current
    return LanguageTranslator.getLocalizedSector(this, lang)
}
