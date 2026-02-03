package com.cebolao.app.theme

import androidx.compose.ui.graphics.Color

// ========================================
// Esquema de Cores Escuro (Premium)
// ========================================
// Palette cyberpunk/neon com alto contraste para modo escuro.
// Cores seguem diretrizes WCAG AA para acessibilidade.

// Primary - Violeta Elétrico
val Primary = Color(0xFF8F00FF)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF480085)
val OnPrimaryContainer = Color(0xFFEADDFF)

// Secondary - Ciano Cyber
val Secondary = Color(0xFF00E5FF)
val OnSecondary = Color(0xFF00363D)
val SecondaryContainer = Color(0xFF004F58)
val OnSecondaryContainer = Color(0xFF97F0FF)

// Tertiary - Rosa/Vermelho Neon
val Tertiary = Color(0xFFFF0055)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFF650021)
val OnTertiaryContainer = Color(0xFFFFD8E4)

// Background & Surface - Dark com glassmorphism
val Background = Color(0xFF0A0A0E)
val OnBackground = Color(0xFFE6E1E5)
val Surface = Color(0xFF141419)
val OnSurface = Color(0xFFE6E1E5)
val SurfaceVariant = Color(0xFF2A2A35)
val OnSurfaceVariant = Color(0xFFCAC4D0)

// Outline & Error
val Outline = Color(0xFF938F99)
val OutlineVariant = Color(0xFF46464F)
val Error = Color(0xFFFFB4AB)
val OnError = Color(0xFF690005)
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

// ========================================
// Transparências Modernas (Glassmorphism)
// ========================================
// Níveis de transparência para efeitos de vidro e sobreposições

object AlphaLevels {
    // Níveis de transparência padrão
    const val FULL = 1.0f
    const val HIGH = 0.9f
    const val MEDIUM_HIGH = 0.8f
    const val MEDIUM = 0.7f
    const val MEDIUM_LOW = 0.6f
    const val LOW = 0.5f
    const val VERY_LOW = 0.3f
    const val MINIMAL = 0.15f
    const val FAINT = 0.08f
    const val GHOST = 0.04f
    
    // Transparências específicas para componentes
    const val GLASS_HIGH = 0.95f
    const val GLASS_MEDIUM = 0.85f
    const val GLASS_LOW = 0.75f
    const val GLASS_DIM = 0.6f
    
    // Transparências para overlays e modais
    const val OVERLAY_DARK = 0.8f
    const val OVERLAY_MEDIUM = 0.6f
    const val OVERLAY_LIGHT = 0.4f
    
    // Transparências para borders e strokes
    const val BORDER_HIGH = 0.4f
    const val BORDER_MEDIUM = 0.25f
    const val BORDER_LOW = 0.15f
    const val BORDER_FAINT = 0.08f
    
    // Transparências para backgrounds de cards
    const val CARD_HIGH = 0.12f
    const val CARD_MEDIUM = 0.08f
    const val CARD_LOW = 0.05f
    const val CARD_FAINT = 0.03f
    
    // Transparências para estados hover/pressed
    const val HOVER = 0.12f
    const val PRESSED = 0.2f
    const val FOCUSED = 0.16f
    
    // Transparências para disabled states
    const val DISABLED = 0.38f
    
    // Transparências para textos secundários
    const val TEXT_HIGH = 0.9f
    const val TEXT_MEDIUM = 0.7f
    const val TEXT_LOW = 0.5f
    const val TEXT_FAINT = 0.3f
}

// ========================================
// Esquema de Cores Claro
// ========================================
// Mantido para compatibilidade com preferência do sistema.

// Primary Light
val PrimaryLight = Color(0xFF6200EE)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFEADDFF)
val OnPrimaryContainerLight = Color(0xFF21005D)

// Secondary Light
val SecondaryLight = Color(0xFF006874)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFF97F0FF)
val OnSecondaryContainerLight = Color(0xFF001F24)

// Tertiary Light
val TertiaryLight = Color(0xFFB0003A)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD9DF)
val OnTertiaryContainerLight = Color(0xFF3E0010)

// Background & Surface Light
val BackgroundLight = Color(0xFFFDFBFF)
val OnBackgroundLight = Color(0xFF1A1C1E)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1C1E)
val SurfaceVariantLight = Color(0xFFE7E0EC)
val OnSurfaceVariantLight = Color(0xFF49454F)

// Outline & Error Light
val OutlineLight = Color(0xFF79747E)
val OutlineVariantLight = Color(0xFFCAC4D0)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)
