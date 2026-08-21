package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    targetCard: ColorCardType,
    initialColor: OklchColor,
    onColorConfirmed: (OklchColor) -> Unit,
    onDismiss: () -> Unit,
    isFa: Boolean
) {
    var lightness by remember { mutableFloatStateOf(initialColor.l) }
    var chroma by remember { mutableFloatStateOf(initialColor.c) }
    var hue by remember { mutableFloatStateOf(initialColor.h) }
    var hexInput by remember { mutableStateOf(initialColor.toHex(includeHash = true)) }

    val currentColor = remember(lightness, chroma, hue) {
        OklchColor(lightness, chroma, hue)
    }

    val quickColors = remember {
        listOf(
            "#0F172A", "#18181B", "#1E1B4B", "#064E3B", "#7F1D1D", "#78350F",
            "#F8FAFC", "#FDFBF7", "#FEF08A", "#BAE6FD", "#DDD6FE", "#FECDD3"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFa) "تنظیم رنگ: ${targetCard.titleFa}" else "Select Color: ${targetCard.titleEn}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Live Swatch & Hex Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(currentColor.toComposeColor())
                        .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentColor.toHex(includeHash = true),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "OKLCH(L: ${(lightness * 100).toInt()}%, C: ${(chroma * 100).toInt()}, H: ${hue.toInt()}°)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Swatches
            Text(
                text = if (isFa) "رنگ‌های سریع" else "Quick Swatches",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickColors.forEach { hex ->
                    val okl = OklchColor.fromHex(hex)
                    if (okl != null) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(okl.toComposeColor())
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .clickable {
                                    lightness = okl.l
                                    chroma = okl.c
                                    hue = okl.h
                                    hexInput = hex
                                }
                        )
                    }
                }
            }

            // Sliders: Lightness, Chroma, Hue
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Lightness
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (isFa) "روشنایی ادراکی (Lightness)" else "Perceptual Lightness", style = MaterialTheme.typography.bodySmall)
                    Text(text = "${(lightness * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = lightness,
                    onValueChange = {
                        lightness = it
                        hexInput = OklchColor(it, chroma, hue).toHex(includeHash = true)
                    },
                    valueRange = 0.02f..0.98f,
                    modifier = Modifier.testTag("slider_lightness")
                )

                // Chroma
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (isFa) "اشباع و غلظت (Chroma)" else "Chroma Saturation", style = MaterialTheme.typography.bodySmall)
                    Text(text = String.format("%.2f", chroma), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = chroma,
                    onValueChange = {
                        chroma = it
                        hexInput = OklchColor(lightness, it, hue).toHex(includeHash = true)
                    },
                    valueRange = 0.0f..0.32f,
                    modifier = Modifier.testTag("slider_chroma")
                )

                // Hue
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (isFa) "زاویه فام چرخ رنگ (Hue Angle)" else "Hue Wheel Angle", style = MaterialTheme.typography.bodySmall)
                    Text(text = "${hue.toInt()}°", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = hue,
                    onValueChange = {
                        hue = it
                        hexInput = OklchColor(lightness, chroma, it).toHex(includeHash = true)
                    },
                    valueRange = 0f..360f,
                    modifier = Modifier.testTag("slider_hue")
                )
            }

            // Hex Direct Input
            OutlinedTextField(
                value = hexInput,
                onValueChange = { input ->
                    hexInput = input
                    val parsed = OklchColor.fromHex(input)
                    if (parsed != null) {
                        lightness = parsed.l
                        chroma = parsed.c
                        hue = parsed.h
                    }
                },
                label = { Text(if (isFa) "کد هگز دلخواه" else "Custom HEX") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("direct_hex_input")
            )

            // Confirm Button
            Button(
                onClick = { onColorConfirmed(currentColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_color_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFa) "اعمال و محاسبه آنی هارمونی" else "Apply & Harmonize Palette",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
