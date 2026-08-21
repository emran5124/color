package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.OklchColor
import com.example.ui.components.*
import com.example.viewmodel.ColorCardType
import com.example.viewmodel.StudioUiEvent
import com.example.viewmodel.StudioViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioMainScreen(
    viewModel: StudioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isFa = uiState.isPersianLanguage

    // Event Collector for Toasts & Clipboard
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is StudioUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is StudioUiEvent.CopyToClipboard -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(event.label, event.text)
                    clipboard.setPrimaryClip(clip)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = if (isFa) "استودیو رنگ و تایپوگرافی" else "Color & Typography Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (isFa) "هارمونی ریاضی قطعی بر پایه OKLCH" else "Deterministic OKLCH Perceptual Math",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Language Switch
                    TextButton(
                        onClick = { viewModel.toggleLanguage() },
                        modifier = Modifier.testTag("lang_toggle_btn")
                    ) {
                        Text(
                            text = if (isFa) "EN" else "فا",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Save Palette
                    IconButton(
                        onClick = { viewModel.saveCurrentPalette() },
                        modifier = Modifier.testTag("save_palette_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Save Palette",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Export Code
                    IconButton(
                        onClick = { viewModel.setExportDialogOpen(true) },
                        modifier = Modifier.testTag("export_code_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Export Code",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Central Live Preview Canvas
            LivePreviewCanvas(
                uiState = uiState,
                onScoreClick = { viewModel.setScoreDetailsOpen(true) },
                onQuickCycle = { viewModel.cycleHarmonyStep() }
            )

            // 2. Active Color Cards List (with copy with/without #)
            ColorCardsSection(
                uiState = uiState,
                onCopyHex = { cardType, withHash -> viewModel.copyHexCode(cardType, withHash) },
                onOpenColorPicker = { cardType -> viewModel.openColorPicker(cardType) },
                onToggleBgLock = { viewModel.toggleBgLock() }
            )

            // 3. Harmony, Search, Typography & Presets Controls
            HarmonyControlsSection(
                uiState = uiState,
                onToggleBgLock = { viewModel.toggleBgLock() },
                onSelectHarmonyMode = { mode -> viewModel.selectHarmonyMode(mode) },
                onSelectAestheticStyle = { style -> viewModel.selectAestheticStyle(style) },
                onUpdateScoreThreshold = { threshold -> viewModel.updateScoreThreshold(threshold) },
                onRunAutoSearch = { viewModel.runAutoSearch() },
                onApplyPreset = { preset -> viewModel.applyPreset(preset) },
                onOpenColorPicker = { cardType -> viewModel.openColorPicker(cardType) },
                onUpdateMainText = { text -> viewModel.updateMainText(text) },
                onUpdateSubText = { text -> viewModel.updateSubText(text) },
                onToggleSubtext = { viewModel.toggleSubtext() },
                onToggleStroke = { viewModel.toggleStroke() },
                onUpdateStrokeWidth = { width -> viewModel.updateStrokeWidth(width) },
                onToggleShadow = { viewModel.toggleShadow() },
                onUpdateShadowBlur = { blur -> viewModel.updateShadowBlur(blur) },
                onUpdateShadowOffsetY = { offset -> viewModel.updateShadowOffsetY(offset) },
                onUpdateFontSize = { size -> viewModel.updateFontSize(size) }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Active Color Picker Modal
        uiState.activeColorPickerCard?.let { targetCard ->
            val currentColor = when (targetCard) {
                ColorCardType.BACKGROUND -> uiState.palette.background
                ColorCardType.MAIN_TITLE -> uiState.palette.mainTitle
                ColorCardType.SUBTEXT -> uiState.palette.subText
                ColorCardType.STROKE -> uiState.palette.stroke
                ColorCardType.SHADOW -> uiState.palette.shadow
            }
            ColorPickerSheet(
                targetCard = targetCard,
                initialColor = currentColor,
                onColorConfirmed = { chosenColor ->
                    viewModel.updateCustomCardColor(targetCard, chosenColor)
                },
                onDismiss = { viewModel.closeColorPicker() },
                isFa = isFa
            )
        }

        // Score Details Modal
        if (uiState.isScoreDetailsOpen) {
            ScoreBreakdownDialog(
                palette = uiState.palette,
                onDismiss = { viewModel.setScoreDetailsOpen(false) },
                isFa = isFa
            )
        }

        // Export Dialog
        if (uiState.isExportDialogOpen) {
            ExportDialog(
                palette = uiState.palette,
                onCopyCode = { label, code ->
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(label, code))
                    Toast.makeText(context, if (isFa) "$label کپی شد!" else "$label copied!", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { viewModel.setExportDialogOpen(false) },
                isFa = isFa
            )
        }
    }
}
