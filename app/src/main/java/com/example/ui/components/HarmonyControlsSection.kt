package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AestheticStyle
import com.example.model.HarmonyMode
import com.example.model.PresetPalettes
import com.example.model.PresetTheme
import com.example.viewmodel.ColorCardType
import com.example.viewmodel.StudioUiState

@Composable
fun HarmonyControlsSection(
    uiState: StudioUiState,
    onToggleBgLock: () -> Unit,
    onSelectHarmonyMode: (HarmonyMode) -> Unit,
    onSelectAestheticStyle: (AestheticStyle) -> Unit,
    onUpdateScoreThreshold: (Int) -> Unit,
    onRunAutoSearch: () -> Unit,
    onApplyPreset: (PresetTheme) -> Unit,
    onOpenColorPicker: (ColorCardType) -> Unit,
    // Typography actions
    onUpdateMainText: (String) -> Unit,
    onUpdateSubText: (String) -> Unit,
    onToggleSubtext: () -> Unit,
    onToggleStroke: () -> Unit,
    onUpdateStrokeWidth: (Float) -> Unit,
    onToggleShadow: () -> Unit,
    onUpdateShadowBlur: (Float) -> Unit,
    onUpdateShadowOffsetY: (Float) -> Unit,
    onUpdateFontSize: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFa = uiState.isPersianLanguage
    var activeTab by remember { mutableStateOf(0) } // 0: Harmony & Aesthetics, 1: Typography & Effects, 2: Presets

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tab selector
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = if (isFa) "هارمونی و زیبایی‌شناسی" else "Aesthetics & Math",
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = if (isFa) "متن و جلوه‌ها" else "Text & Effects",
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Text(
                        text = if (isFa) "تم‌های شاهکار" else "Masterpieces",
                        fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = { Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        when (activeTab) {
            0 -> HarmonyAndSearchTab(
                uiState = uiState,
                onToggleBgLock = onToggleBgLock,
                onSelectHarmonyMode = onSelectHarmonyMode,
                onSelectAestheticStyle = onSelectAestheticStyle,
                onUpdateScoreThreshold = onUpdateScoreThreshold,
                onRunAutoSearch = onRunAutoSearch,
                onOpenColorPicker = onOpenColorPicker,
                isFa = isFa
            )
            1 -> TypographyAndEffectsTab(
                uiState = uiState,
                onUpdateMainText = onUpdateMainText,
                onUpdateSubText = onUpdateSubText,
                onToggleSubtext = onToggleSubtext,
                onToggleStroke = onToggleStroke,
                onUpdateStrokeWidth = onUpdateStrokeWidth,
                onToggleShadow = onToggleShadow,
                onUpdateShadowBlur = onUpdateShadowBlur,
                onUpdateShadowOffsetY = onUpdateShadowOffsetY,
                onUpdateFontSize = onUpdateFontSize,
                isFa = isFa
            )
            2 -> PresetsTab(
                onApplyPreset = onApplyPreset,
                isFa = isFa
            )
        }
    }
}

@Composable
private fun HarmonyAndSearchTab(
    uiState: StudioUiState,
    onToggleBgLock: () -> Unit,
    onSelectHarmonyMode: (HarmonyMode) -> Unit,
    onSelectAestheticStyle: (AestheticStyle) -> Unit,
    onUpdateScoreThreshold: (Int) -> Unit,
    onRunAutoSearch: () -> Unit,
    onOpenColorPicker: (ColorCardType) -> Unit,
    isFa: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // 1. Aesthetic Style Choice (Editorial vs Vibrant vs Architectural)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isFa) "سبک زیبایی‌شناسی تایپوگرافی" else "Typography Aesthetic Style",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AestheticStyle.values().forEach { style ->
                        val isSelected = uiState.aestheticStyle == style
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectAestheticStyle(style) }
                                .testTag("style_${style.name.lowercase()}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isFa) {
                                        when (style) {
                                            AestheticStyle.CLEAN_EDITORIAL -> "لوکس سرمقاله‌ای"
                                            AestheticStyle.VIBRANT_ACCENT -> "رنگ شاخص"
                                            AestheticStyle.MINIMAL_ARCHITECTURAL -> "مینیمال سوئیسی"
                                        }
                                    } else style.titleEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Background Lock & Custom Background Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isBgLocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isBgLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = if (isFa) "قفل پس‌زمینه انتخابی" else "Lock Background Color",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isFa) "محاسبه دقیق هارمونی روی این پس‌زمینه" else "Generate harmony tailored to this BG",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = uiState.isBgLocked,
                        onCheckedChange = { onToggleBgLock() },
                        modifier = Modifier.testTag("bg_lock_switch")
                    )
                }

                Button(
                    onClick = { onOpenColorPicker(ColorCardType.BACKGROUND) },
                    modifier = Modifier.fillMaxWidth().testTag("pick_bg_color_btn")
                ) {
                    Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isFa) "انتخاب یا ویرایش رنگ پس‌زمینه" else "Custom BG Picker")
                }
            }
        }

        // 3. Harmony Mode Selector Matrix
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isFa) "ماتریس هارمونی چرخ رنگ (Color Wheel Matrix)" else "Color Wheel Angles Matrix",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HarmonyMode.values().forEach { mode ->
                        val isSelected = uiState.harmonyMode == mode
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectHarmonyMode(mode) }
                                .testTag("harmony_chip_${mode.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isFa) mode.persianTitle else mode.englishTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isFa) mode.descriptionFa else mode.englishTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Auto-Search with Quality Threshold
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Column {
                            Text(
                                text = if (isFa) "موتور هوشمند جستجوی ترکیب شاهکار" else "Masterpiece Auto-Search Engine",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isFa) "یافتن ترکیبی با بالاترین کنتراست ادراکی و بدون خستگی چشم" else "Search deterministic space for peak aesthetic score",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isFa) "حداقل آستانه امتیاز: ${uiState.targetScoreThreshold} / ۱۰۰" else "Min Score Threshold: ${uiState.targetScoreThreshold} / 100",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = uiState.targetScoreThreshold.toFloat(),
                    onValueChange = { onUpdateScoreThreshold(it.toInt()) },
                    valueRange = 80f..98f,
                    steps = 18,
                    modifier = Modifier.testTag("score_threshold_slider")
                )

                Button(
                    onClick = onRunAutoSearch,
                    enabled = !uiState.isSearching,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auto_search_btn")
                ) {
                    if (uiState.isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onTertiary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isFa) "در حال کاوش فضای ادراکی و محاسبه هارمونی..." else "Exploring perceptual color space...")
                    } else {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFa) "جستجو و تولید پالت شاهکار" else "Generate Masterpiece Palette",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypographyAndEffectsTab(
    uiState: StudioUiState,
    onUpdateMainText: (String) -> Unit,
    onUpdateSubText: (String) -> Unit,
    onToggleSubtext: () -> Unit,
    onToggleStroke: () -> Unit,
    onUpdateStrokeWidth: (Float) -> Unit,
    onToggleShadow: () -> Unit,
    onUpdateShadowBlur: (Float) -> Unit,
    onUpdateShadowOffsetY: (Float) -> Unit,
    onUpdateFontSize: (Float) -> Unit,
    isFa: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Text Input Fields
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isFa) "محتوای متنی بوم" else "Canvas Text Content",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.mainText,
                    onValueChange = onUpdateMainText,
                    label = { Text(if (isFa) "عنوان اصلی" else "Main Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("main_text_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) "نمایش متن فرعی (Subtext)" else "Enable Subtext",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = uiState.isSubtextEnabled,
                        onCheckedChange = { onToggleSubtext() },
                        modifier = Modifier.testTag("subtext_toggle_switch")
                    )
                }

                AnimatedVisibility(visible = uiState.isSubtextEnabled) {
                    OutlinedTextField(
                        value = uiState.subText,
                        onValueChange = onUpdateSubText,
                        label = { Text(if (isFa) "متن توضیحی / فرعی" else "Subtext Content") },
                        modifier = Modifier.fillMaxWidth().testTag("sub_text_input")
                    )
                }

                // Font Size Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isFa) "اندازه فونت عنوان" else "Font Size",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${uiState.fontSizeSp.toInt()} sp",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = uiState.fontSizeSp,
                        onValueChange = onUpdateFontSize,
                        valueRange = 18f..40f,
                        modifier = Modifier.testTag("font_size_slider")
                    )
                }
            }
        }

        // Visual Effects: Stroke & Shadow Controls
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isFa) "جلوه‌های بصری پیشرفته" else "Advanced Visual Effects",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // 1. Outer Stroke
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFa) "استروک و حاشیه بیرونی (Outer Stroke)" else "Outer Stroke Outline",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isFa) "افزایش تفکیک مرز متن از زمینه" else "Enhance glyph edge separation",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isStrokeEnabled,
                        onCheckedChange = { onToggleStroke() },
                        modifier = Modifier.testTag("stroke_toggle_switch")
                    )
                }

                AnimatedVisibility(visible = uiState.isStrokeEnabled) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isFa) "ضخامت استروک" else "Stroke Width", style = MaterialTheme.typography.bodySmall)
                            Text(text = String.format("%.1f dp", uiState.strokeWidth), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = uiState.strokeWidth,
                            onValueChange = onUpdateStrokeWidth,
                            valueRange = 0.5f..6f,
                            modifier = Modifier.testTag("stroke_width_slider")
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // 2. Ambient Shadow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFa) "سایه ادراکی (Ambient Shadow)" else "Perceptual Depth Shadow",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isFa) "عمق طبیعی بر مبنای فام محیط" else "Natural chromatic ambient drop",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isShadowEnabled,
                        onCheckedChange = { onToggleShadow() },
                        modifier = Modifier.testTag("shadow_toggle_switch")
                    )
                }

                AnimatedVisibility(visible = uiState.isShadowEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isFa) "میزان محوشدگی (Blur)" else "Shadow Blur Radius", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${uiState.shadowBlur.toInt()} dp", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = uiState.shadowBlur,
                            onValueChange = onUpdateShadowBlur,
                            valueRange = 0f..25f,
                            modifier = Modifier.testTag("shadow_blur_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isFa) "جابجایی عمودی (Offset Y)" else "Vertical Offset Y", style = MaterialTheme.typography.bodySmall)
                            Text(text = "${uiState.shadowOffsetY.toInt()} dp", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = uiState.shadowOffsetY,
                            onValueChange = onUpdateShadowOffsetY,
                            valueRange = 0f..20f,
                            modifier = Modifier.testTag("shadow_offset_slider")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetsTab(
    onApplyPreset: (PresetTheme) -> Unit,
    isFa: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isFa) "پالت‌های شاهکار تحریری و سرمقاله‌ای" else "Curated Masterpiece Palettes",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        PresetPalettes.items.forEach { preset ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onApplyPreset(preset) }
                    .testTag("preset_${preset.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(android.graphics.Color.parseColor(preset.bgHex)), CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isFa) preset.nameFa else preset.nameEn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${preset.bgHex} • ${if (isFa) preset.defaultMode.persianTitle else preset.defaultMode.englishTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Apply",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
