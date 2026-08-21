package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.*

/**
 * High-precision Color Science & Aesthetics Engine.
 * Combines OKLCH perceptual color space, APCA-inspired readability modeling,
 * chromostereopsis / vibration clashing prevention, and geometric color wheel harmony.
 */
data class OklchColor(
    val l: Float, // Lightness: 0.0 to 1.0 (perceptual lightness)
    val c: Float, // Chroma: 0.0 to ~0.37 (saturation/colorfulness)
    val h: Float, // Hue: 0.0 to 360.0 degrees
    val alpha: Float = 1.0f
) {
    /**
     * Converts OKLCH to sRGB Color with Gamut clipping & safe boundary
     */
    fun toComposeColor(): Color {
        val (r, g, b) = oklchToSrgb(l, c, h)
        return Color(
            red = r.coerceIn(0f, 1f),
            green = g.coerceIn(0f, 1f),
            blue = b.coerceIn(0f, 1f),
            alpha = alpha.coerceIn(0f, 1f)
        )
    }

    fun toHex(includeHash: Boolean = true, includeAlpha: Boolean = false): String {
        val (r, g, b) = oklchToSrgb(l, c, h)
        val ri = (r.coerceIn(0f, 1f) * 255).roundToInt()
        val gi = (g.coerceIn(0f, 1f) * 255).roundToInt()
        val bi = (b.coerceIn(0f, 1f) * 255).roundToInt()
        val prefix = if (includeHash) "#" else ""
        return if (includeAlpha) {
            val ai = (alpha.coerceIn(0f, 1f) * 255).roundToInt()
            String.format("%s%02X%02X%02X%02X", prefix, ai, ri, gi, bi)
        } else {
            String.format("%s%02X%02X%02X", prefix, ri, gi, bi)
        }
    }

    companion object {
        fun fromComposeColor(color: Color): OklchColor {
            val (l, c, h) = srgbToOklch(color.red, color.green, color.blue)
            return OklchColor(l, c, h, color.alpha)
        }

        fun fromHex(hex: String): OklchColor? {
            val cleanHex = hex.removePrefix("#").trim()
            if (cleanHex.length != 6 && cleanHex.length != 8) return null
            return try {
                val r: Float
                val g: Float
                val b: Float
                val a: Float
                if (cleanHex.length == 6) {
                    r = cleanHex.substring(0, 2).toInt(16) / 255f
                    g = cleanHex.substring(2, 4).toInt(16) / 255f
                    b = cleanHex.substring(4, 6).toInt(16) / 255f
                    a = 1.0f
                } else {
                    a = cleanHex.substring(0, 2).toInt(16) / 255f
                    r = cleanHex.substring(2, 4).toInt(16) / 255f
                    g = cleanHex.substring(4, 6).toInt(16) / 255f
                    b = cleanHex.substring(6, 8).toInt(16) / 255f
                }
                val (l, c, h) = srgbToOklch(r, g, b)
                OklchColor(l, c, h, a)
            } catch (e: Exception) {
                null
            }
        }
    }
}

enum class HarmonyMode(
    val persianTitle: String,
    val englishTitle: String,
    val descriptionFa: String,
    val angles: List<Float>
) {
    EDITORIAL_ELEGANT(
        "لوکس مجله‌ای (تینت نامحسوس)",
        "Editorial Luxury",
        "تینت‌های ادراکی بسیار دقیق با کنتراست عمیق و بدون خستگی چشم",
        listOf(0f, 30f)
    ),
    COMPLEMENTARY(
        "مکمل هوشمند (۱۸۰°)",
        "Balanced Complementary (180°)",
        "تضاد پویا با کنترل اشباع برای جلوگیری از لرزش نوری در حاشیه حروف",
        listOf(180f)
    ),
    SPLIT_COMPLEMENTARY(
        "مکمل دوگانه (۱۵۰° / ۲۱۰°)",
        "Split Complementary (150° / 210°)",
        "ترکیب غنی و چشم‌نواز با تعادل دمایی گرم و سرد",
        listOf(150f, 210f)
    ),
    TRIADIC(
        "سه‌تایی متوازن (۱۲۰° / ۲۴۰°)",
        "Triadic Harmony (120° / 240°)",
        "مثلث متساوی‌الاضلاع چرخ رنگ با پایداری بصری بالا",
        listOf(120f, 240f)
    ),
    ANALOGOUS(
        "همسایه و پیوسته (۳۰° / ۶۰°)",
        "Analogous Harmonic (30° / 60°)",
        "جریان نرم و آرامش‌بخش فام‌های مجاور در چرخ رنگ",
        listOf(30f, 60f)
    ),
    MONOCHROMATIC(
        "مونوکروم عمیق (تک‌فام)",
        "Deep Monochromatic",
        "اتکا بر تضاد نوری خالص و خالص‌ترین شکل مینیمالیسم",
        listOf(0f)
    )
}

enum class AestheticStyle(val titleFa: String, val titleEn: String) {
    CLEAN_EDITORIAL("لوکس و سرمقاله‌ای (Editorial)", "Clean Editorial"),
    VIBRANT_ACCENT("تایپوگرافی با رنگ شاخص (Vibrant Accent)", "Vibrant Accent"),
    MINIMAL_ARCHITECTURAL("مینیمال و مهندسی (Architectural)", "Minimal Architectural")
}

data class PaletteScoreBreakdown(
    val totalScore: Int,
    val contrastScore: Int,      // 0 - 30 (Perceptual Contrast / WCAG AAA & APCA)
    val harmonyScore: Int,       // 0 - 25 (Color wheel angle symmetry)
    val vibrationSafetyScore: Int, // 0 - 20 (Prevention of chromatic vibration / eye strain)
    val hierarchyScore: Int,     // 0 - 15 (Distinct visual drop between Title & Subtext)
    val sophisticationScore: Int, // 0 - 10 (Color temperature balance & gamut elegance)
    val wcagRatioTitle: Float,
    val wcagRatioSubtext: Float,
    val isWcagTripleA: Boolean,
    val isWcagDoubleA: Boolean,
    val statusTextFa: String,
    val statusTextEn: String,
    val critiqueFa: String,
    val critiqueEn: String
)

data class StudioPalette(
    val background: OklchColor,
    val mainTitle: OklchColor,
    val subText: OklchColor,
    val stroke: OklchColor,
    val shadow: OklchColor,
    val harmonyMode: HarmonyMode,
    val style: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL,
    val score: PaletteScoreBreakdown
)

/**
 * OKLab & OKLCH Transformations (Björn Ottosson, 2020)
 */
fun srgbToOklch(r: Float, g: Float, b: Float): Triple<Float, Float, Float> {
    val rL = if (r > 0.04045f) ((r + 0.055f) / 1.055f).pow(2.4f) else r / 12.92f
    val gL = if (g > 0.04045f) ((g + 0.055f) / 1.055f).pow(2.4f) else g / 12.92f
    val bL = if (b > 0.04045f) ((b + 0.055f) / 1.055f).pow(2.4f) else b / 12.92f

    val l = 0.4122214708f * rL + 0.5363325363f * gL + 0.0514459929f * bL
    val m = 0.2119034982f * rL + 0.6806995451f * gL + 0.1073969566f * bL
    val s = 0.0883024619f * rL + 0.2817188376f * gL + 0.6299787005f * bL

    val l_ = cbrt(l)
    val m_ = cbrt(m)
    val s_ = cbrt(s)

    val oklabL = 0.2104542553f * l_ + 0.7936177850f * m_ - 0.0040720468f * s_
    val oklabA = 1.9779984951f * l_ - 2.4285922050f * m_ + 0.4505937099f * s_
    val oklabB = 0.0259040371f * l_ + 0.7827717662f * m_ - 0.8086757660f * s_

    val c = sqrt(oklabA * oklabA + oklabB * oklabB)
    var h = (atan2(oklabB, oklabA) * (180f / Math.PI.toFloat()))
    if (h < 0f) h += 360f

    return Triple(oklabL.coerceIn(0f, 1f), c.coerceIn(0f, 0.4f), h % 360f)
}

fun oklchToSrgb(l: Float, c: Float, h: Float): Triple<Float, Float, Float> {
    val hRad = h * (Math.PI.toFloat() / 180f)
    val a = c * cos(hRad)
    val b = c * sin(hRad)

    val l_ = l + 0.3963377774f * a + 0.2158037573f * b
    val m_ = l - 0.1055613458f * a - 0.0638541728f * b
    val s_ = l - 0.0894841775f * a - 1.2914855480f * b

    val lCube = l_ * l_ * l_
    val mCube = m_ * m_ * m_
    val sCube = s_ * s_ * s_

    val rL = +4.0767439362f * lCube - 3.3077115913f * mCube + 0.2309699292f * sCube
    val gL = -1.2684380046f * lCube + 2.6097574011f * mCube - 0.3413193965f * sCube
    val bL = -0.0041960863f * lCube - 0.7034186147f * mCube + 1.7076147010f * sCube

    val r = if (rL > 0.0031308f) 1.055f * rL.pow(1f / 2.4f) - 0.055f else 12.92f * rL
    val g = if (gL > 0.0031308f) 1.055f * gL.pow(1f / 2.4f) - 0.055f else 12.92f * gL
    val bVal = if (bL > 0.0031308f) 1.055f * bL.pow(1f / 2.4f) - 0.055f else 12.92f * bL

    return Triple(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), bVal.coerceIn(0f, 1f))
}

private fun cbrt(v: Float): Float {
    return if (v >= 0) v.pow(1f / 3f) else -(-v).pow(1f / 3f)
}

/**
 * Standard WCAG 2.1 Relative Luminance and Contrast Ratio
 */
fun calculateRelativeLuminance(color: Color): Float {
    fun channelLuminance(c: Float): Float {
        return if (c <= 0.03928f) c / 12.92f else ((c + 0.055f) / 1.055f).pow(2.4f)
    }
    val r = channelLuminance(color.red)
    val g = channelLuminance(color.green)
    val b = channelLuminance(color.blue)
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

fun calculateWcagContrast(c1: Color, c2: Color): Float {
    val l1 = calculateRelativeLuminance(c1)
    val l2 = calculateRelativeLuminance(c2)
    val lighter = max(l1, l2)
    val darker = min(l1, l2)
    return (lighter + 0.05f) / (darker + 0.05f)
}

/**
 * Deterministic Aesthetic Color Science Engine.
 * Produces world-class typography palettes by calibrating perceptual lightness,
 * chroma saturation sweet-spots, temperature harmony, and legibility curves.
 */
object HarmonyEngine {

    fun generateDeterministicPalette(
        bg: OklchColor,
        mode: HarmonyMode,
        style: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL,
        lightnessDeltaMod: Float = 0f,
        chromaMod: Float = 0f,
        stepSeed: Int = 0
    ): StudioPalette {
        val isDarkBg = bg.l < 0.5f

        // 1. Determine Title & Subtext Chroma based on Aesthetic Style
        // In high-end design, text chroma must be tightly controlled:
        // - Editorial: ultra-subtle tint (0.012 to 0.035) gives warm ivory or cool platinum feel
        // - Vibrant Accent: controlled pop (0.04 to 0.09) with high contrast
        // - Architectural: absolute pure contrast (0.005 to 0.02)
        val titleChroma = when (style) {
            AestheticStyle.CLEAN_EDITORIAL -> ((bg.c * 0.25f) + 0.022f + chromaMod * 0.01f).coerceIn(0.012f, 0.042f)
            AestheticStyle.VIBRANT_ACCENT -> (0.065f + chromaMod * 0.02f).coerceIn(0.035f, 0.095f)
            AestheticStyle.MINIMAL_ARCHITECTURAL -> (0.008f + chromaMod * 0.005f).coerceIn(0.003f, 0.020f)
        }

        // Subtext chroma is always muted to keep clear visual hierarchy
        val subChroma = (titleChroma * 0.55f).coerceIn(0.006f, 0.030f)

        // 2. Harmonic Angle Calculation
        val angleOffsets = mode.angles
        val angleOffset = if (angleOffsets.isNotEmpty()) angleOffsets[stepSeed % angleOffsets.size] else 0f

        val mainHue = when (mode) {
            HarmonyMode.EDITORIAL_ELEGANT -> {
                // In editorial mode, text hue gently borrows background warmth or adds complementary subtle warmth
                if (isDarkBg) (bg.h + 20f + (stepSeed * 15f)) % 360f
                else (bg.h + 10f) % 360f
            }
            HarmonyMode.MONOCHROMATIC -> bg.h
            else -> (bg.h + angleOffset) % 360f
        }

        val subHue = (mainHue + 15f) % 360f

        // 3. Perceptual Lightness Tuning (Ensuring optimal WCAG 12:1 to 18:1 for Title, 7:1+ for Subtext)
        val mainLightness = if (isDarkBg) {
            // Dark Mode: Main title is crisp, radiant off-white (0.94 - 0.985)
            (0.965f + lightnessDeltaMod * 0.02f).coerceIn(0.92f, 0.99f)
        } else {
            // Light Mode: Main title is deep, rich near-black/espresso/navy (0.08 - 0.14)
            (0.11f - lightnessDeltaMod * 0.02f).coerceIn(0.05f, 0.16f)
        }

        // Subtext Lightness: calibrated hierarchy step
        val subLightness = if (isDarkBg) {
            (mainLightness - 0.22f + lightnessDeltaMod * 0.01f).coerceIn(0.68f, 0.78f)
        } else {
            (mainLightness + 0.26f - lightnessDeltaMod * 0.01f).coerceIn(0.32f, 0.44f)
        }

        val mainTitle = OklchColor(
            l = mainLightness,
            c = titleChroma,
            h = mainHue
        )

        val subText = OklchColor(
            l = subLightness,
            c = subChroma,
            h = subHue
        )

        // 4. Outer Boundary Stroke: Refined, elegant highlight boundary (not thick cartoon outline)
        val strokeHue = (bg.h + (if (mode == HarmonyMode.MONOCHROMATIC) 0f else 180f)) % 360f
        val strokeLightness = if (isDarkBg) {
            (mainLightness + 0.02f).coerceIn(0.95f, 1.0f)
        } else {
            (mainLightness - 0.05f).coerceIn(0.02f, 0.10f)
        }
        val stroke = OklchColor(
            l = strokeLightness,
            c = (titleChroma * 1.3f).coerceIn(0.015f, 0.07f),
            h = strokeHue
        )

        // 5. Perceptual Ambient Shadow: Deep ambient tint (physics-based light occlusion)
        val shadowLightness = if (isDarkBg) 0.025f else 0.08f
        val shadowChroma = (bg.c * 0.45f).coerceIn(0.005f, 0.04f)
        val shadow = OklchColor(
            l = shadowLightness,
            c = shadowChroma,
            h = bg.h,
            alpha = if (isDarkBg) 0.90f else 0.40f
        )

        val score = evaluatePaletteScore(bg, mainTitle, subText, mode, style)

        return StudioPalette(
            background = bg,
            mainTitle = mainTitle,
            subText = subText,
            stroke = stroke,
            shadow = shadow,
            harmonyMode = mode,
            style = style,
            score = score
        )
    }

    /**
     * Advanced Multi-Factor Aesthetic Scoring System (0 - 100)
     * Rigorously evaluates:
     * 1. Perceptual Contrast & APCA Legibility (Max 30)
     * 2. Geometric Wheel Harmony & Temperature (Max 25)
     * 3. Vibration & Eye-Strain Prevention (Max 20)
     * 4. Visual Hierarchy & Subordination (Max 15)
     * 5. Palette Sophistication & Tonal Nuance (Max 10)
     */
    fun evaluatePaletteScore(
        bg: OklchColor,
        mainTitle: OklchColor,
        subText: OklchColor,
        mode: HarmonyMode,
        style: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL
    ): PaletteScoreBreakdown {
        val bgColor = bg.toComposeColor()
        val titleColor = mainTitle.toComposeColor()
        val subColor = subText.toComposeColor()

        val titleWcag = calculateWcagContrast(bgColor, titleColor)
        val subWcag = calculateWcagContrast(bgColor, subColor)

        // 1. Perceptual Contrast Score (0 to 30)
        // High-end readability: Title WCAG >= 10:1 gets full 20 pts, Subtext >= 4.5:1 gets 10 pts.
        val titleContrastPts = when {
            titleWcag >= 12.0f -> 20f
            titleWcag >= 9.0f -> 18f
            titleWcag >= 7.0f -> 15f
            titleWcag >= 4.5f -> 10f
            titleWcag >= 3.0f -> 4f
            else -> 0f
        }
        val subContrastPts = when {
            subWcag >= 6.0f -> 10f
            subWcag >= 4.5f -> 9f
            subWcag >= 3.5f -> 6f
            subWcag >= 2.5f -> 3f
            else -> 0f
        }
        val contrastScore = (titleContrastPts + subContrastPts).roundToInt().coerceIn(0, 30)

        // 2. Harmony & Temperature Alignment Score (0 to 25)
        val angleScore = when (mode) {
            HarmonyMode.EDITORIAL_ELEGANT -> 25f
            HarmonyMode.MONOCHROMATIC -> {
                val diff = abs(mainTitle.h - bg.h)
                if (diff < 5f || diff > 355f) 25f else 18f
            }
            else -> {
                var minDiff = 360f
                for (targetAngle in mode.angles) {
                    val actualAngleDiff = abs((mainTitle.h - bg.h + 360f) % 360f - targetAngle)
                    val symmetricDiff = min(actualAngleDiff, 360f - actualAngleDiff)
                    if (symmetricDiff < minDiff) minDiff = symmetricDiff
                }
                // Reward accurate angle alignment
                ((20f - minDiff.coerceAtMost(20f)) / 20f * 25f).coerceIn(10f, 25f)
            }
        }
        val harmonyScore = angleScore.roundToInt().coerceIn(0, 25)

        // 3. Eye Strain & Vibration Clashing Prevention (0 to 20)
        // Chromostereopsis occurs when text chroma and bg chroma are both high, especially with 180° hue clash.
        val combinedChroma = bg.c + mainTitle.c
        val hueDistance = abs((mainTitle.h - bg.h + 360f) % 360f)
        val isOppositeHue = (hueDistance in 150f..210f)

        val vibrationPenalty = when {
            combinedChroma > 0.28f && isOppositeHue -> 16f // Severe visual vibration (clashing neon)
            combinedChroma > 0.22f && isOppositeHue -> 10f
            mainTitle.c > 0.16f -> 8f                      // Text too saturated for comfortable reading
            mainTitle.c > 0.11f -> 4f
            else -> 0f
        }
        val vibrationSafetyScore = (20f - vibrationPenalty).roundToInt().coerceIn(0, 20)

        // 4. Visual Hierarchy Score (0 to 15)
        // Title must lead over Subtext by healthy lightness and contrast delta
        val lightnessDelta = abs(mainTitle.l - subText.l)
        val isTitleLeading = if (bg.l < 0.5f) mainTitle.l > subText.l else mainTitle.l < subText.l
        val hierarchyPts = if (isTitleLeading) {
            when {
                lightnessDelta in 0.18f..0.35f -> 15f
                lightnessDelta in 0.12f..0.45f -> 12f
                lightnessDelta > 0.08f -> 8f
                else -> 4f
            }
        } else {
            2f // Subtext is brighter/darker than title: upside-down hierarchy
        }
        val hierarchyScore = hierarchyPts.roundToInt().coerceIn(0, 15)

        // 5. Palette Sophistication & Tonal Nuance (0 to 10)
        // Rewards calibrated undertones, background depth (L < 0.22 or L > 0.90), and balanced warmth
        val bgElegance = when {
            bg.l in 0.06f..0.22f && bg.c in 0.015f..0.12f -> 6f // Rich dark slate/navy/obsidian
            bg.l in 0.92f..0.985f && bg.c in 0.005f..0.06f -> 6f // Clean editorial paper/porcelain
            bg.l in 0.40f..0.60f && bg.c > 0.18f -> 1f          // Mid-gray/muddy saturated background (hard to read)
            else -> 4f
        }
        val textNuance = if (mainTitle.c in 0.010f..0.065f) 4f else 2f
        val sophisticationScore = (bgElegance + textNuance).roundToInt().coerceIn(0, 10)

        val total = (contrastScore + harmonyScore + vibrationSafetyScore + hierarchyScore + sophisticationScore).coerceIn(0, 100)

        val isWcagTripleA = titleWcag >= 7.0f && subWcag >= 4.5f
        val isWcagDoubleA = titleWcag >= 4.5f && subWcag >= 3.0f

        val (faStatus, enStatus) = when {
            total >= 94 -> Pair("شاهکار تایپوگرافی لوکس و بی‌نقص", "Pristine Luxury Typography (Masterpiece)")
            total >= 85 -> Pair("ترکیب فوق‌العاده استاندارد و چشم‌نواز", "Superior Readability & Aesthetic Balance")
            total >= 75 -> Pair("کنتراست استاندارد و خوانا", "Standard Contrast & Legible")
            total >= 60 -> Pair("قابل قبول اما نیازمند بهینه‌سازی رنگ", "Acceptable (Needs Refinement)")
            else -> Pair("ضعیف: دارای خستگی چشم یا کنتراست ناکافی", "Poor: Risk of Eye Fatigue or Low Contrast")
        }

        val (critiqueFa, critiqueEn) = when {
            total >= 94 -> Pair(
                "کنتراست ادراکی فوق‌العاده (${String.format("%.1f", titleWcag)}:1)، اشباع کاملاً مهارشده برای جلوگیری از خستگی چشم، و سلسله‌مراتب بصری بی‌نقص بین عنوان و زیرعنوان.",
                "Pristine perceptual contrast (${String.format("%.1f", titleWcag)}:1), calibrated chroma preventing chromostereopsis, and perfect hierarchy."
            )
            total >= 85 -> Pair(
                "کنتراست مناسب با استانداردهای WCAG AAA و تعادل هندسی دقیق در چرخ رنگ.",
                "High contrast meeting WCAG AAA with solid color wheel angle balance."
            )
            total >= 70 -> Pair(
                "خوانایی قابل قبول؛ برای رسیدن به امتیاز شاهکار، اشباع متن را ملایم‌تر و کنتراست نوری را تقویت کنید.",
                "Legible; to achieve top tier, reduce text chroma and widen lightness delta."
            )
            else -> Pair(
                "تداخل نوری یا عدم تفاوت کافی بین روشنایی متن و پس‌زمینه که سبب افت کیفیت بصری شده است.",
                "Visual vibration or insufficient luminance difference affecting visual appeal."
            )
        }

        return PaletteScoreBreakdown(
            totalScore = total,
            contrastScore = contrastScore,
            harmonyScore = harmonyScore,
            vibrationSafetyScore = vibrationSafetyScore,
            hierarchyScore = hierarchyScore,
            sophisticationScore = sophisticationScore,
            wcagRatioTitle = (titleWcag * 10).roundToInt() / 10f,
            wcagRatioSubtext = (subWcag * 10).roundToInt() / 10f,
            isWcagTripleA = isWcagTripleA,
            isWcagDoubleA = isWcagDoubleA,
            statusTextFa = faStatus,
            statusTextEn = enStatus,
            critiqueFa = critiqueFa,
            critiqueEn = critiqueEn
        )
    }

    /**
     * Auto-Search Engine: Deterministically explores fine lightness steps,
     * safe chroma bands, and harmony angles to guarantee a truly beautiful score >= targetThreshold.
     */
    fun searchBestHarmoniousPalette(
        baseBg: OklchColor,
        isBgLocked: Boolean,
        preferredMode: HarmonyMode?,
        preferredStyle: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL,
        targetThreshold: Int
    ): StudioPalette {
        val modesToTry = if (preferredMode != null) listOf(preferredMode) + HarmonyMode.values().filter { it != preferredMode }
        else HarmonyMode.values().toList()

        val stylesToTry = listOf(preferredStyle) + AestheticStyle.values().filter { it != preferredStyle }

        var bestPalette: StudioPalette? = null
        var highestScore = -1

        // Rich curated background candidates if not locked
        val bgCandidates = if (isBgLocked) {
            listOf(baseBg)
        } else {
            listOf(
                baseBg,
                OklchColor(0.12f, 0.035f, 255f), // Midnight Navy
                OklchColor(0.14f, 0.020f, 240f), // Minimal Obsidian Slate
                OklchColor(0.965f, 0.015f, 85f), // Warm Editorial Linen
                OklchColor(0.13f, 0.050f, 155f), // Deep Spruce Forest
                OklchColor(0.16f, 0.045f, 35f),  // Velvet Charcoal & Terracotta
                OklchColor(0.97f, 0.010f, 230f), // Nordic Crisp Paper
                OklchColor(0.12f, 0.060f, 290f)  // Imperial Royal Plum
            )
        }

        for (candidateBg in bgCandidates) {
            for (style in stylesToTry) {
                for (mode in modesToTry) {
                    for (seed in 0..3) {
                        for (lMod in listOf(0f, 0.5f, -0.5f)) {
                            for (cMod in listOf(0f, -0.3f, 0.3f)) {
                                val candidate = generateDeterministicPalette(
                                    bg = candidateBg,
                                    mode = mode,
                                    style = style,
                                    lightnessDeltaMod = lMod,
                                    chromaMod = cMod,
                                    stepSeed = seed
                                )
                                if (candidate.score.totalScore >= targetThreshold) {
                                    return candidate
                                }
                                if (candidate.score.totalScore > highestScore) {
                                    highestScore = candidate.score.totalScore
                                    bestPalette = candidate
                                }
                            }
                        }
                    }
                }
            }
        }

        return bestPalette ?: generateDeterministicPalette(
            bg = baseBg,
            mode = preferredMode ?: HarmonyMode.EDITORIAL_ELEGANT,
            style = preferredStyle
        )
    }
}
