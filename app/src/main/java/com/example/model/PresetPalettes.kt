package com.example.model

data class PresetTheme(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val bgHex: String,
    val defaultMode: HarmonyMode,
    val defaultStyle: AestheticStyle = AestheticStyle.CLEAN_EDITORIAL
)

object PresetPalettes {
    val items = listOf(
        PresetTheme(
            "editorial_luxury",
            "سرمقاله‌ای لوکس (Editorial Ivory & Obsidian)",
            "Editorial Luxury",
            "#0E131F",
            HarmonyMode.EDITORIAL_ELEGANT,
            AestheticStyle.CLEAN_EDITORIAL
        ),
        PresetTheme(
            "warm_parchment",
            "کاغذ کتان پاریسی (Warm Linen & Espresso)",
            "Warm Parisian Linen",
            "#F9F6F0",
            HarmonyMode.EDITORIAL_ELEGANT,
            AestheticStyle.CLEAN_EDITORIAL
        ),
        PresetTheme(
            "oceanic_navy",
            "سرمه‌ای سلطنتی مینیمال (Midnight Royal)",
            "Midnight Royal Navy",
            "#0B132B",
            HarmonyMode.COMPLEMENTARY,
            AestheticStyle.VIBRANT_ACCENT
        ),
        PresetTheme(
            "nordic_porcelain",
            "نوردیک پورسلین مدرن (Nordic Light)",
            "Nordic Porcelain",
            "#F4F6F9",
            HarmonyMode.TRIADIC,
            AestheticStyle.MINIMAL_ARCHITECTURAL
        ),
        PresetTheme(
            "spruce_forest",
            "مه زمردین و جنگل کاج (Deep Emerald)",
            "Deep Emerald Pine",
            "#072018",
            HarmonyMode.ANALOGOUS,
            AestheticStyle.CLEAN_EDITORIAL
        ),
        PresetTheme(
            "velvet_amber",
            "مخملی کهربایی (Velvet & Amber)",
            "Velvet Espresso & Amber",
            "#1A1412",
            HarmonyMode.SPLIT_COMPLEMENTARY,
            AestheticStyle.VIBRANT_ACCENT
        ),
        PresetTheme(
            "monochrome_architect",
            "مونوکروم معماری دقیق (Swiss Architect)",
            "Swiss Architectural Monochrome",
            "#121214",
            HarmonyMode.MONOCHROMATIC,
            AestheticStyle.MINIMAL_ARCHITECTURAL
        ),
        PresetTheme(
            "sunset_terracotta",
            "تراکوتا و رس گرم (Warm Terracotta)",
            "Sunset Terracotta & Clay",
            "#221410",
            HarmonyMode.SPLIT_COMPLEMENTARY,
            AestheticStyle.CLEAN_EDITORIAL
        )
    )
}
