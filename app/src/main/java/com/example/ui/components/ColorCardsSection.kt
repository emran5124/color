package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OklchColor
import com.example.viewmodel.ColorCardType
import com.example.viewmodel.StudioUiState

@Composable
fun ColorCardsSection(
    uiState: StudioUiState,
    onCopyHex: (ColorCardType, Boolean) -> Unit,
    onOpenColorPicker: (ColorCardType) -> Unit,
    onToggleBgLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFa = uiState.isPersianLanguage
    val palette = uiState.palette

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isFa) "کارت‌های رنگ فعال و استخراج کدها" else "Active Color Cards & Palette",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isFa) "۵ جزء رنگی" else "5 Elements",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Background Card
        SingleColorCard(
            cardType = ColorCardType.BACKGROUND,
            title = if (isFa) "رنگ پس‌زمینه" else "Background Canvas",
            subtitle = if (isFa) "پایه محاسبات هارمونی OKLCH" else "Foundation for Harmony Engine",
            oklch = palette.background,
            isLocked = uiState.isBgLocked,
            onLockToggle = onToggleBgLock,
            onCopyHex = { withHash -> onCopyHex(ColorCardType.BACKGROUND, withHash) },
            onPickColor = { onOpenColorPicker(ColorCardType.BACKGROUND) },
            isFa = isFa
        )

        // 2. Main Title Card
        SingleColorCard(
            cardType = ColorCardType.MAIN_TITLE,
            title = if (isFa) "عنوان اصلی (Main Title)" else "Main Title Typography",
            subtitle = if (isFa) "کنتراست قطعی (WCAG ${palette.score.wcagRatioTitle}:1)" else "Deterministic Contrast (${palette.score.wcagRatioTitle}:1)",
            oklch = palette.mainTitle,
            onCopyHex = { withHash -> onCopyHex(ColorCardType.MAIN_TITLE, withHash) },
            onPickColor = { onOpenColorPicker(ColorCardType.MAIN_TITLE) },
            isFa = isFa
        )

        // 3. Subtext Card
        SingleColorCard(
            cardType = ColorCardType.SUBTEXT,
            title = if (isFa) "متن فرعی (Subtext)" else "Subtext Typography",
            subtitle = if (isFa) "روشنایی هماهنگ (WCAG ${palette.score.wcagRatioSubtext}:1)" else "Harmonious Subordination (${palette.score.wcagRatioSubtext}:1)",
            oklch = palette.subText,
            onCopyHex = { withHash -> onCopyHex(ColorCardType.SUBTEXT, withHash) },
            onPickColor = { onOpenColorPicker(ColorCardType.SUBTEXT) },
            isFa = isFa
        )

        // 4. Stroke Card
        SingleColorCard(
            cardType = ColorCardType.STROKE,
            title = if (isFa) "استروک و کادر بیرونی (Stroke)" else "Outer Boundary Stroke",
            subtitle = if (isFa) "مرز وضوح تایپوگرافی" else "Boundary High Clarity",
            oklch = palette.stroke,
            onCopyHex = { withHash -> onCopyHex(ColorCardType.STROKE, withHash) },
            onPickColor = { onOpenColorPicker(ColorCardType.STROKE) },
            isFa = isFa
        )

        // 5. Shadow Card
        SingleColorCard(
            cardType = ColorCardType.SHADOW,
            title = if (isFa) "سایه ادراکی (Ambient Shadow)" else "Ambient Depth Shadow",
            subtitle = if (isFa) "سایه با فام محیطی، بدون سیاهی خام" else "Chromatically tinted ambient depth",
            oklch = palette.shadow,
            onCopyHex = { withHash -> onCopyHex(ColorCardType.SHADOW, withHash) },
            onPickColor = { onOpenColorPicker(ColorCardType.SHADOW) },
            isFa = isFa
        )
    }
}

@Composable
private fun SingleColorCard(
    cardType: ColorCardType,
    title: String,
    subtitle: String,
    oklch: OklchColor,
    isLocked: Boolean? = null,
    onLockToggle: (() -> Unit)? = null,
    onCopyHex: (Boolean) -> Unit,
    onPickColor: () -> Unit,
    isFa: Boolean
) {
    val composeColor = oklch.toComposeColor()
    val hexWithHash = oklch.toHex(includeHash = true)
    val hexNoHash = oklch.toHex(includeHash = false)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("color_card_${cardType.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Swatch thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(composeColor)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onPickColor() }
                    .testTag("swatch_${cardType.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Edit Color",
                    tint = if (oklch.l < 0.5f) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isLocked != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isLocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isLocked) (if (isFa) "قفل شده" else "Locked") else (if (isFa) "آزاد" else "Unlocked"),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isLocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "$hexWithHash  •  L:${(oklch.l * 100).toInt()}% C:${(oklch.c * 100).toInt()} H:${oklch.h.toInt()}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            // Action Buttons: Lock (if background), Copy with #, Copy without #
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLocked != null && onLockToggle != null) {
                    IconButton(
                        onClick = onLockToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("lock_toggle_card_btn")
                    ) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = "Toggle Lock",
                            tint = if (isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Copy with #
                FilledTonalButton(
                    onClick = { onCopyHex(true) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("copy_hash_${cardType.name.lowercase()}")
                ) {
                    Text(text = "#HEX", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Copy without #
                OutlinedButton(
                    onClick = { onCopyHex(false) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("copy_raw_${cardType.name.lowercase()}")
                ) {
                    Text(text = "HEX", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
