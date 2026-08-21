package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PaletteScoreBreakdown
import com.example.model.StudioPalette

@Composable
fun ScoreBreakdownDialog(
    palette: StudioPalette,
    onDismiss: () -> Unit,
    isFa: Boolean
) {
    val score = palette.score

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("score_breakdown_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
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
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isFa) "ارزیابی چندبُعدی زیبایی‌شناسی و خوانایی" else "Aesthetic & Readability Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Total Score Banner
                val isMasterpiece = score.totalScore >= 90
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isMasterpiece) Color(0xFF059669).copy(alpha = 0.14f) else Color(0xFFF59E0B).copy(alpha = 0.14f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "${score.totalScore}",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isMasterpiece) Color(0xFF059669) else Color(0xFFD97706)
                            )
                            Column {
                                Text(
                                    text = if (isFa) "شاخص زیبایی‌شناسی و هارمونی (از ۱۰۰)" else "Overall Aesthetic Harmony Score",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isFa) score.statusTextFa else score.statusTextEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Detailed Critique Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isFa) score.critiqueFa else score.critiqueEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // 5 Criteria Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // 1. Contrast & APCA
                    CriterionRow(
                        title = if (isFa) "۱. کنتراست ادراکی و وضوح (APCA & WCAG)" else "1. Perceptual Contrast & Legibility",
                        subtext = if (isFa) "نسبت عنوان: ${score.wcagRatioTitle}:1 (AAA) | زیرعنوان: ${score.wcagRatioSubtext}:1" else "Title: ${score.wcagRatioTitle}:1 | Subtext: ${score.wcagRatioSubtext}:1",
                        scoreText = "${score.contrastScore} / ۳۰",
                        progress = score.contrastScore / 30f,
                        color = Color(0xFF2563EB)
                    )

                    // 2. Harmonic Angle
                    CriterionRow(
                        title = if (isFa) "۲. تقارن زوایای فام در چرخ رنگ (Hue Geometry)" else "2. Color Wheel Angle Balance",
                        subtext = if (isFa) palette.harmonyMode.persianTitle else palette.harmonyMode.englishTitle,
                        scoreText = "${score.harmonyScore} / ۲۵",
                        progress = score.harmonyScore / 25f,
                        color = Color(0xFF8B5CF6)
                    )

                    // 3. Vibration & Eye Strain Prevention
                    CriterionRow(
                        title = if (isFa) "۳. مهار لرزش نوری در حاشیه حروف (Anti-Vibration)" else "3. Vibration & Eye-Strain Safety",
                        subtext = if (isFa) "جلوگیری از خستگی چشم ناشی از تضاد فام‌های نئونی" else "Calibrated text chroma prevents optical chromostereopsis",
                        scoreText = "${score.vibrationSafetyScore} / ۲۰",
                        progress = score.vibrationSafetyScore / 20f,
                        color = Color(0xFF10B981)
                    )

                    // 4. Visual Hierarchy
                    CriterionRow(
                        title = if (isFa) "۴. تفکیک سلسله‌مراتب بصری (Visual Hierarchy)" else "4. Visual Hierarchy & Drop",
                        subtext = if (isFa) "تضاد وزنی مناسب عنوان نسبت به زیرعنوان" else "Balanced weight difference between header and subtext",
                        scoreText = "${score.hierarchyScore} / ۱۵",
                        progress = score.hierarchyScore / 15f,
                        color = Color(0xFFF59E0B)
                    )

                    // 5. Palette Sophistication
                    CriterionRow(
                        title = if (isFa) "۵. ظرافت و پختگی پالت (Tonal Sophistication)" else "5. Tonal Sophistication & Gamut",
                        subtext = if (isFa) "عمق پس‌زمینه و تطابق دمایی رنگ‌ها" else "Background depth and temperature harmony",
                        scoreText = "${score.sophisticationScore} / ۱۰",
                        progress = score.sophisticationScore / 10f,
                        color = Color(0xFFEC4899)
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (isFa) "بستن و ادامه طراحی" else "Close")
                }
            }
        }
    }
}

@Composable
private fun CriterionRow(
    title: String,
    subtext: String,
    scoreText: String,
    progress: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = scoreText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
        }
        Text(text = subtext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
