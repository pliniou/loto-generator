package com.cebolao.app.theme

import androidx.compose.ui.graphics.Color
import com.cebolao.domain.model.LotteryType

/**
 * Cores oficiais (aproximadas) das Loterias CAIXA, atualizadas para o tema Neon/Premium.
 *
 * Mapeamento direto na camada de UI, desacoplando o Domain de dependências gráficas.
 */
object LotteryColors {
    // Cores Primárias (oficiais CAIXA)
    private val MegaSenaColor = Color(0xFF00AB67) // Verde oficial
    private val LotofacilColor = Color(0xFF803594) // Roxo oficial
    private val QuinaColor = Color(0xFF005DA4) // Azul oficial
    private val LotomaniaColor = Color(0xFFF99D1C) // Laranja oficial
    private val TimemaniaColor = Color(0xFFFFDD00) // Amarelo oficial
    private val DuplaSenaColor = Color(0xFFA62A52) // Vinho/Rosa oficial
    private val SuperSeteColor = Color(0xFFA6CE39) // Verde limão oficial

    // Cores "On" (Texto/Ícones sobre a primária) - branco para todas, exceto Timemania
    private val OnMegaSenaColor = Color(0xFFFFFFFF)
    private val OnLotofacilColor = Color(0xFFFFFFFF)
    private val OnQuinaColor = Color(0xFFFFFFFF)
    private val OnLotomaniaColor = Color(0xFFFFFFFF)
    private val OnTimemaniaColor = Color(0xFF00AB67) // Verde Mega-Sena sobre amarelo
    private val OnDuplaSenaColor = Color(0xFFFFFFFF)
    private val OnSuperSeteColor = Color(0xFFFFFFFF)
    private val OnDiaDeSorteColor = Color(0xFFFFFFFF)
    private val OnMaisMilionariaColor = Color(0xFFFFFFFF)

    // Cores específicas para as novas loterias
    private val DiaDeSorteColor = Color(0xFFCB8829)
    private val MaisMilionariaColor = Color(0xFF112349)

    fun getColor(type: LotteryType): Color =
        when (type) {
            LotteryType.MEGA_SENA -> MegaSenaColor
            LotteryType.LOTOFACIL -> LotofacilColor
            LotteryType.QUINA -> QuinaColor
            LotteryType.LOTOMANIA -> LotomaniaColor
            LotteryType.TIMEMANIA -> TimemaniaColor
            LotteryType.DUPLA_SENA -> DuplaSenaColor
            LotteryType.SUPER_SETE -> SuperSeteColor
            LotteryType.DIA_DE_SORTE -> DiaDeSorteColor
            LotteryType.MAIS_MILIONARIA -> MaisMilionariaColor
        }

    fun getOnColor(type: LotteryType): Color =
        when (type) {
            LotteryType.MEGA_SENA -> OnMegaSenaColor
            LotteryType.LOTOFACIL -> OnLotofacilColor
            LotteryType.QUINA -> OnQuinaColor
            LotteryType.LOTOMANIA -> OnLotomaniaColor
            LotteryType.TIMEMANIA -> OnTimemaniaColor
            LotteryType.DUPLA_SENA -> OnDuplaSenaColor
            LotteryType.SUPER_SETE -> OnSuperSeteColor
            LotteryType.DIA_DE_SORTE -> OnDiaDeSorteColor
            LotteryType.MAIS_MILIONARIA -> OnMaisMilionariaColor
        }
}
