package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.StudioPalette

@Composable
fun ExportDialog(
    palette: StudioPalette,
    onCopyCode: (String, String) -> Unit,
    onDismiss: () -> Unit,
    isFa: Boolean
) {
    var selectedFormatIndex by remember { mutableIntStateOf(0) } // 0: CSS Variables, 1: Tailwind CSS, 2: Jetpack Compose, 3: JSON

    val bgHex = palette.background.toHex(true)
    val titleHex = palette.mainTitle.toHex(true)
    val subHex = palette.subText.toHex(true)
    val strokeHex = palette.stroke.toHex(true)
    val shadowHex = palette.shadow.toHex(true)

    val codeText = when (selectedFormatIndex) {
        0 -> """
:root {
  --color-canvas-bg: $bgHex;
  --color-typography-title: $titleHex;
  --color-typography-subtext: $subHex;
  --color-typography-stroke: $strokeHex;
  --color-ambient-shadow: $shadowHex;
  /* Harmony Mode: ${palette.harmonyMode.name} */
  /* Quality Score: ${palette.score.totalScore}/100 */
}
        """.trimIndent()
        1 -> """
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        studio: {
          bg: '$bgHex',
          title: '$titleHex',
          subtext: '$subHex',
          stroke: '$strokeHex',
          shadow: '$shadowHex',
        }
      }
    }
  }
}
        """.trimIndent()
        2 -> """
// Jetpack Compose Color Scheme
val StudioBgColor = Color(0xFF${bgHex.removePrefix("#")})
val StudioTitleColor = Color(0xFF${titleHex.removePrefix("#")})
val StudioSubtextColor = Color(0xFF${subHex.removePrefix("#")})
val StudioStrokeColor = Color(0xFF${strokeHex.removePrefix("#")})
val StudioShadowColor = Color(0xFF${shadowHex.removePrefix("#")})
        """.trimIndent()
        else -> """
{
  "paletteName": "${palette.harmonyMode.name}",
  "score": ${palette.score.totalScore},
  "colors": {
    "background": "$bgHex",
    "mainTitle": "$titleHex",
    "subtext": "$subHex",
    "stroke": "$strokeHex",
    "shadow": "$shadowHex"
  }
}
        """.trimIndent()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("export_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) "استخراج کدهای پالت" else "Export Palette Codes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Format Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedFormatIndex,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(selected = selectedFormatIndex == 0, onClick = { selectedFormatIndex = 0 }, text = { Text("CSS Variables") })
                    Tab(selected = selectedFormatIndex == 1, onClick = { selectedFormatIndex = 1 }, text = { Text("Tailwind") })
                    Tab(selected = selectedFormatIndex == 2, onClick = { selectedFormatIndex = 2 }, text = { Text("Compose") })
                    Tab(selected = selectedFormatIndex == 3, onClick = { selectedFormatIndex = 3 }, text = { Text("JSON") })
                }

                // Code Display Area
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = codeText,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // Copy Action Button
                Button(
                    onClick = {
                        val formatName = when (selectedFormatIndex) {
                            0 -> "CSS Variables"
                            1 -> "Tailwind Config"
                            2 -> "Compose Colors"
                            else -> "JSON Tokens"
                        }
                        onCopyCode(formatName, codeText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("copy_export_code_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFa) "کپی کردن کد در کلیپ‌بورد" else "Copy Code to Clipboard",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
