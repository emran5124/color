package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.StudioUiState

@Composable
fun LivePreviewCanvas(
    uiState: StudioUiState,
    onScoreClick: () -> Unit,
    onQuickCycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = uiState.palette
    val bgColor = palette.background.toComposeColor()
    val mainColor = palette.mainTitle.toComposeColor()
    val subColor = palette.subText.toComposeColor()
    val strokeColor = palette.stroke.toComposeColor()
    val shadowColor = palette.shadow.toComposeColor()

    val animatedScore by animateFloatAsState(
        targetValue = palette.score.totalScore.toFloat(),
        animationSpec = tween(durationMillis = 500),
        label = "score_anim"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("preview_canvas_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 260.dp)
                .background(bgColor)
                .padding(20.dp)
        ) {
            // Top Bar inside canvas: Score Badge & WCAG indicator + Quick Cycle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onScoreClick() }
                        .testTag("score_badge_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Score Info",
                            tint = if (palette.score.totalScore >= 85) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (uiState.isPersianLanguage) {
                                "امتیاز: ${animatedScore.toInt()} / ۱۰۰"
                            } else {
                                "Score: ${animatedScore.toInt()} / 100"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // WCAG pill
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (palette.score.isWcagTripleA) Color(0xFF059669) else Color(0xFF2563EB)
                        ) {
                            Text(
                                text = if (palette.score.isWcagTripleA) "AAA" else "AA",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                // Quick variation seed cycle button
                IconButton(
                    onClick = onQuickCycle,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .testTag("quick_cycle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Cycle Variation",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Central Typography Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Title with optional Stroke and Shadow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val shadowEffect = if (uiState.isShadowEnabled) {
                        Shadow(
                            color = shadowColor,
                            offset = Offset(0f, uiState.shadowOffsetY * 2f),
                            blurRadius = uiState.shadowBlur * 2f
                        )
                    } else {
                        Shadow.None
                    }

                    // Stroke background layer
                    if (uiState.isStrokeEnabled) {
                        Text(
                            text = uiState.mainText,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = uiState.fontSizeSp.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = strokeColor,
                                drawStyle = Stroke(width = uiState.strokeWidth * 2.5f),
                                shadow = shadowEffect
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Fill foreground layer
                    Text(
                        text = uiState.mainText,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = uiState.fontSizeSp.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = mainColor,
                            drawStyle = Fill,
                            shadow = if (!uiState.isStrokeEnabled) shadowEffect else Shadow.None
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("canvas_main_title_text")
                    )
                }

                // Subtitle / Subtext (toggleable)
                AnimatedVisibility(
                    visible = uiState.isSubtextEnabled,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    val subShadowEffect = if (uiState.isShadowEnabled) {
                        Shadow(
                            color = shadowColor,
                            offset = Offset(0f, (uiState.shadowOffsetY * 0.8f)),
                            blurRadius = (uiState.shadowBlur * 0.8f)
                        )
                    } else {
                        Shadow.None
                    }

                    Text(
                        text = uiState.subText,
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = (uiState.fontSizeSp * 0.58f).sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = (uiState.fontSizeSp * 0.85f).sp,
                            color = subColor,
                            shadow = subShadowEffect
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .testTag("canvas_subtext_text")
                    )
                }
            }

            // Bottom subtle watermark/mode pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.3f),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = if (uiState.isPersianLanguage) uiState.harmonyMode.persianTitle else uiState.harmonyMode.englishTitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
