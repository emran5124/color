package com.example.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudioUiState(
    val palette: StudioPalette,
    val isBgLocked: Boolean = false,
    val harmonyMode: HarmonyMode = HarmonyMode.EDITORIAL_ELEGANT,
    val aestheticStyle: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL,
    val targetScoreThreshold: Int = 90,
    // Typography settings
    val mainText: String = "تایپوگرافی هوشمند استودیو",
    val subText: String = "هارمونی ادراکی OKLCH، کنتراست قطعی و زیبایی‌شناسی خالص",
    val isSubtextEnabled: Boolean = true,
    val isStrokeEnabled: Boolean = false, // Clean default for high elegance
    val strokeWidth: Float = 1.8f,
    val isShadowEnabled: Boolean = true,
    val shadowBlur: Float = 10f,
    val shadowOffsetY: Float = 4f,
    val fontSizeSp: Float = 26f,
    // System UI & Dialogs
    val isSearching: Boolean = false,
    val activeColorPickerCard: ColorCardType? = null,
    val isExportDialogOpen: Boolean = false,
    val isScoreDetailsOpen: Boolean = false,
    val isPersianLanguage: Boolean = true,
    val savedPalettes: List<StudioPalette> = emptyList(),
    val stepSeed: Int = 0
)

enum class ColorCardType(val titleFa: String, val titleEn: String) {
    BACKGROUND("رنگ پس‌زمینه", "Background"),
    MAIN_TITLE("عنوان اصلی", "Main Title"),
    SUBTEXT("متن فرعی", "Subtext"),
    STROKE("استروک بیرونی", "Outer Stroke"),
    SHADOW("سایه ادراکی", "Ambient Shadow")
}

sealed class StudioUiEvent {
    data class ShowToast(val message: String) : StudioUiEvent()
    data class CopyToClipboard(val label: String, val text: String) : StudioUiEvent()
}

class StudioViewModel : ViewModel() {

    private val initialBg = OklchColor.fromHex("#0E131F") ?: OklchColor(0.14f, 0.035f, 255f)
    private val initialPalette = HarmonyEngine.generateDeterministicPalette(
        bg = initialBg,
        mode = HarmonyMode.EDITORIAL_ELEGANT,
        style = AestheticStyle.CLEAN_EDITORIAL
    )

    private val _uiState = MutableStateFlow(
        StudioUiState(
            palette = initialPalette,
            isBgLocked = false,
            harmonyMode = HarmonyMode.EDITORIAL_ELEGANT,
            aestheticStyle = AestheticStyle.CLEAN_EDITORIAL
        )
    )
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<StudioUiEvent>()
    val events: SharedFlow<StudioUiEvent> = _events.asSharedFlow()

    fun toggleBgLock() {
        _uiState.update { it.copy(isBgLocked = !it.isBgLocked) }
    }

    fun setBgColor(hexOrColor: String) {
        val oklch = OklchColor.fromHex(hexOrColor) ?: return
        applyNewBackground(oklch)
    }

    fun setBgColorOklch(newBg: OklchColor) {
        applyNewBackground(newBg)
    }

    private fun applyNewBackground(newBg: OklchColor) {
        val currentState = _uiState.value
        val newPalette = HarmonyEngine.generateDeterministicPalette(
            bg = newBg,
            mode = currentState.harmonyMode,
            style = currentState.aestheticStyle,
            stepSeed = currentState.stepSeed
        )
        _uiState.update {
            it.copy(
                palette = newPalette,
                activeColorPickerCard = null
            )
        }
    }

    fun selectHarmonyMode(mode: HarmonyMode) {
        val currentState = _uiState.value
        val newPalette = HarmonyEngine.generateDeterministicPalette(
            bg = currentState.palette.background,
            mode = mode,
            style = currentState.aestheticStyle,
            stepSeed = currentState.stepSeed
        )
        _uiState.update {
            it.copy(
                harmonyMode = mode,
                palette = newPalette
            )
        }
    }

    fun selectAestheticStyle(style: AestheticStyle) {
        val currentState = _uiState.value
        val newPalette = HarmonyEngine.generateDeterministicPalette(
            bg = currentState.palette.background,
            mode = currentState.harmonyMode,
            style = style,
            stepSeed = currentState.stepSeed
        )
        _uiState.update {
            it.copy(
                aestheticStyle = style,
                palette = newPalette
            )
        }
    }

    fun cycleHarmonyStep() {
        val currentState = _uiState.value
        val nextSeed = currentState.stepSeed + 1
        val newPalette = HarmonyEngine.generateDeterministicPalette(
            bg = currentState.palette.background,
            mode = currentState.harmonyMode,
            style = currentState.aestheticStyle,
            stepSeed = nextSeed
        )
        _uiState.update {
            it.copy(
                stepSeed = nextSeed,
                palette = newPalette
            )
        }
    }

    fun updateScoreThreshold(threshold: Int) {
        _uiState.update { it.copy(targetScoreThreshold = threshold.coerceIn(70, 99)) }
    }

    fun runAutoSearch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(280) // Smooth tactile response
            val currentState = _uiState.value
            val result = HarmonyEngine.searchBestHarmoniousPalette(
                baseBg = currentState.palette.background,
                isBgLocked = currentState.isBgLocked,
                preferredMode = currentState.harmonyMode,
                preferredStyle = currentState.aestheticStyle,
                targetThreshold = currentState.targetScoreThreshold
            )
            _uiState.update {
                it.copy(
                    palette = result,
                    harmonyMode = result.harmonyMode,
                    aestheticStyle = result.style,
                    isSearching = false
                )
            }
            val msg = if (currentState.isPersianLanguage) {
                "شاهکار پیدا شد: امتیاز ${result.score.totalScore}/۱۰۰"
            } else {
                "Aesthetic Masterpiece Found: Score ${result.score.totalScore}/100"
            }
            _events.emit(StudioUiEvent.ShowToast(msg))
        }
    }

    fun applyPreset(preset: PresetTheme) {
        val oklch = OklchColor.fromHex(preset.bgHex) ?: return
        val newPalette = HarmonyEngine.generateDeterministicPalette(
            bg = oklch,
            mode = preset.defaultMode,
            style = preset.defaultStyle
        )
        _uiState.update {
            it.copy(
                palette = newPalette,
                harmonyMode = preset.defaultMode,
                aestheticStyle = preset.defaultStyle
            )
        }
    }

    // Typography toggles and values
    fun updateMainText(text: String) {
        _uiState.update { it.copy(mainText = text) }
    }

    fun updateSubText(text: String) {
        _uiState.update { it.copy(subText = text) }
    }

    fun toggleSubtext() {
        _uiState.update { it.copy(isSubtextEnabled = !it.isSubtextEnabled) }
    }

    fun toggleStroke() {
        _uiState.update { it.copy(isStrokeEnabled = !it.isStrokeEnabled) }
    }

    fun updateStrokeWidth(width: Float) {
        _uiState.update { it.copy(strokeWidth = width.coerceIn(0.5f, 6f)) }
    }

    fun toggleShadow() {
        _uiState.update { it.copy(isShadowEnabled = !it.isShadowEnabled) }
    }

    fun updateShadowBlur(blur: Float) {
        _uiState.update { it.copy(shadowBlur = blur.coerceIn(0f, 25f)) }
    }

    fun updateShadowOffsetY(offsetY: Float) {
        _uiState.update { it.copy(shadowOffsetY = offsetY.coerceIn(0f, 20f)) }
    }

    fun updateFontSize(size: Float) {
        _uiState.update { it.copy(fontSizeSp = size.coerceIn(16f, 44f)) }
    }

    // Copying codes
    fun copyHexCode(cardType: ColorCardType, includeHash: Boolean) {
        val palette = _uiState.value.palette
        val color = when (cardType) {
            ColorCardType.BACKGROUND -> palette.background
            ColorCardType.MAIN_TITLE -> palette.mainTitle
            ColorCardType.SUBTEXT -> palette.subText
            ColorCardType.STROKE -> palette.stroke
            ColorCardType.SHADOW -> palette.shadow
        }
        val hex = color.toHex(includeHash = includeHash)
        viewModelScope.launch {
            _events.emit(StudioUiEvent.CopyToClipboard(cardType.name, hex))
            val prefix = if (includeHash) "با نشانه #" else "بدون نشانه #"
            val label = if (_uiState.value.isPersianLanguage) cardType.titleFa else cardType.titleEn
            _events.emit(StudioUiEvent.ShowToast("$label: $hex ($prefix) کپی شد"))
        }
    }

    fun openColorPicker(cardType: ColorCardType) {
        _uiState.update { it.copy(activeColorPickerCard = cardType) }
    }

    fun closeColorPicker() {
        _uiState.update { it.copy(activeColorPickerCard = null) }
    }

    fun updateCustomCardColor(cardType: ColorCardType, newColor: OklchColor) {
        val current = _uiState.value
        val p = current.palette
        val updatedPalette = when (cardType) {
            ColorCardType.BACKGROUND -> {
                HarmonyEngine.generateDeterministicPalette(
                    bg = newColor,
                    mode = current.harmonyMode,
                    style = current.aestheticStyle,
                    stepSeed = current.stepSeed
                )
            }
            ColorCardType.MAIN_TITLE -> p.copy(
                mainTitle = newColor,
                score = HarmonyEngine.evaluatePaletteScore(p.background, newColor, p.subText, current.harmonyMode, current.aestheticStyle)
            )
            ColorCardType.SUBTEXT -> p.copy(
                subText = newColor,
                score = HarmonyEngine.evaluatePaletteScore(p.background, p.mainTitle, newColor, current.harmonyMode, current.aestheticStyle)
            )
            ColorCardType.STROKE -> p.copy(stroke = newColor)
            ColorCardType.SHADOW -> p.copy(shadow = newColor)
        }
        _uiState.update {
            it.copy(
                palette = updatedPalette,
                activeColorPickerCard = null
            )
        }
    }

    fun toggleLanguage() {
        _uiState.update {
            val isPersian = !it.isPersianLanguage
            it.copy(
                isPersianLanguage = isPersian,
                mainText = if (isPersian) "تایپوگرافی هوشمند استودیو" else "Intelligent Typography Studio",
                subText = if (isPersian) "هارمونی ادراکی OKLCH، کنتراست قطعی و زیبایی‌شناسی خالص" else "Perceptual OKLCH harmony, deterministic contrast & pure aesthetics"
            )
        }
    }

    fun setExportDialogOpen(open: Boolean) {
        _uiState.update { it.copy(isExportDialogOpen = open) }
    }

    fun setScoreDetailsOpen(open: Boolean) {
        _uiState.update { it.copy(isScoreDetailsOpen = open) }
    }

    fun saveCurrentPalette() {
        val current = _uiState.value
        if (!current.savedPalettes.contains(current.palette)) {
            _uiState.update { it.copy(savedPalettes = it.savedPalettes + it.palette) }
            viewModelScope.launch {
                val msg = if (current.isPersianLanguage) "پالت به علاقه‌مندی‌ها ذخیره شد" else "Palette saved to favorites"
                _events.emit(StudioUiEvent.ShowToast(msg))
            }
        }
    }

    fun removeSavedPalette(palette: StudioPalette) {
        _uiState.update { it.copy(savedPalettes = it.savedPalettes - palette) }
    }
}
