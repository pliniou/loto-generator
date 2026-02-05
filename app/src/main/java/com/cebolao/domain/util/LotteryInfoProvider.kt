package com.cebolao.domain.util

import com.cebolao.domain.model.LotteryInfo
import com.cebolao.domain.model.LotteryType
import com.cebolao.domain.model.ProbabilityRow

object LotteryInfoProvider {
    fun getInfo(type: LotteryType): LotteryInfo {
        return when (type) {
            LotteryType.MEGA_SENA -> megaSenaInfo
            LotteryType.LOTOFACIL -> lotofacilInfo
            LotteryType.QUINA -> quinaInfo
            LotteryType.LOTOMANIA -> lotomaniaInfo
            LotteryType.TIMEMANIA -> timemaniaInfo
            LotteryType.DUPLA_SENA -> duplaSenaInfo
            LotteryType.DIA_DE_SORTE -> diaDeSorteInfo
            LotteryType.SUPER_SETE -> superSeteInfo
            LotteryType.MAIS_MILIONARIA -> maisMilionariaInfo
        }
    }

    private val megaSenaInfo =
        LotteryInfo(
            type = LotteryType.MEGA_SENA,
            howToPlay = "A Mega-Sena paga milhões para o acertador dos 6 números sorteados. Ainda é possível ganhar prêmios ao acertar 4 ou 5 números dentre os 60 disponíveis no volante de apostas. Para realizar o sonho de ser o próximo milionário, você deve marcar de 6 a 20 números do volante, podendo deixar que o sistema escolha os números para você (Surpresinha) e/ou concorrer com a mesma aposta por 2, 4 ou 8 concursos consecutivos (Teimosinha).",
            drawFrequency = "Os sorteios da Mega-Sena são realizados três vezes por semana, às terças, quintas e aos sábados.",
            betsInfo = "A aposta mínima, de 6 números, custa R$ 5,00. Quanto mais números marcar, maior o preço da aposta e maiores as chances de faturar o prêmio mais cobiçado do país.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(6, "1 em 50.063.860"),
                    ProbabilityRow(7, "1 em 7.151.980"),
                    ProbabilityRow(8, "1 em 1.787.995"),
                    ProbabilityRow(9, "1 em 595.998"),
                    ProbabilityRow(10, "1 em 238.399"),
                    ProbabilityRow(15, "1 em 10.003"),
                ),
            bolaoInfo = "O Bolão Caixa é a possibilidade que o apostador tem de realizar apostas em grupo. Basta preencher o campo próprio no volante ou solicitar ao atendente da lotérica. Na Mega-Sena, os bolões têm preço mínimo de R$ 15,00. Porém, cada cota não pode ser inferior a R$ 6,00.",
            prizeAllocation = "O prêmio bruto corresponde a 43,35% da arrecadação. Dessa porcentagem: 35% são distribuídos entre os acertadores dos 6 números (Sena); 19% entre os acertadores de 5 números (Quina); 19% entre os acertadores de 4 números (Quadra); 22% ficam acumulados e são distribuídos aos acertadores dos 6 números nos concursos de final 0 ou 5; 5% ficam acumulados para a primeira faixa (Sena) do último concurso do ano de final 0 ou 5 (Mega da Virada).",
        )

    private val lotofacilInfo =
        LotteryInfo(
            type = LotteryType.LOTOFACIL,
            howToPlay = "A Lotofácil é, como o próprio nome diz, fácil de apostar e principalmente de ganhar. Você marca entre 15 e 20 números, dentre os 25 disponíveis no volante, e fatura prêmio se acertar 11, 12, 13, 14 ou 15 números.",
            drawFrequency = "Os sorteios são realizados às segundas, terças, quartas, quintas, sextas-feiras e sábados, sempre às 20h.",
            betsInfo = "A aposta mínima, de 15 números, custa R$ 3,00. As apostas podem ir até 20 números, aumentando as chances de ganhar.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(15, "1 em 3.268.760"),
                    ProbabilityRow(16, "1 em 204.297"),
                    ProbabilityRow(17, "1 em 24.035"),
                    ProbabilityRow(18, "1 em 4.005"),
                    ProbabilityRow(19, "1 em 843"),
                    ProbabilityRow(20, "1 em 210"),
                ),
            bolaoInfo = "No Bolão da Lotofácil, o preço mínimo é de R$ 12,00, cada cota não pode ser inferior a R$ 4,00. É possível realizar um bolão de no mínimo 2 e no máximo 100 cotas.",
            prizeAllocation = "O prêmio bruto corresponde a 43,35% da arrecadação. Do valor destinado a prêmios, é deduzido o montante para pagamentos dos prêmios fixos (11, 12 e 13 acertos). O restante é distribuído para as demais faixas (14 e 15 acertos e acumulados).",
        )

    private val quinaInfo =
        LotteryInfo(
            type = LotteryType.QUINA,
            howToPlay = "Concorra a prêmios grandiosos com a Quina: basta marcar de 5 a 15 números dentre os 80 disponíveis no volante e torcer. Caso prefira, o sistema pode escolher os números para você através da Surpresinha.",
            drawFrequency = "São 6 sorteios semanais: de segunda-feira a sábado, às 20h.",
            betsInfo = "O preço da aposta com 5 números é de R$ 2,50. Quanto mais números marcar, maior o preço da aposta e maiores as chances de ganhar.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(5, "1 em 24.040.016"),
                    ProbabilityRow(6, "1 em 4.006.669"),
                    ProbabilityRow(7, "1 em 1.144.762"),
                    ProbabilityRow(15, "1 em 8.005"),
                ),
            bolaoInfo = "O Bolão da Quina tem preço mínimo de R$ 12,50. Porém, cada cota não pode ser inferior a R$ 3,50. É permitida a realização de no máximo 10 apostas por Bolão.",
            prizeAllocation = "35% são distribuídos entre os acertadores dos 5 números, 15% entre os acertadores de 4 números, 10% entre os acertadores de 3 números, 10% entre os acertadores de 2 números. O restante acumula.",
        )

    private val lotomaniaInfo =
        LotteryInfo(
            type = LotteryType.LOTOMANIA,
            howToPlay = "Na Lotomania, você escolhe 50 números e ganha se acertar 20, 19, 18, 17, 16, 15 ou nenhum número. É fácil de jogar e de ganhar.",
            drawFrequency = "Os sorteios são realizados às segundas, quartas e sextas-feiras, às 20h.",
            betsInfo = "O preço da aposta é único e custa R$ 3,00. Você escolhe 50 números.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(50, "1 em 11.372.635 (20 acertos)"),
                ),
            bolaoInfo = "Na Lotomania, os bolões têm preço mínimo de R$ 15,00, sendo que cada cota não pode ser inferior a R$ 3,00.",
            prizeAllocation = "O prêmio bruto corresponde a 43,35% da arrecadação. O valor é distribuído entre as faixas de 20 a 15 acertos e também para quem não acerta nenhum número (0 acertos).",
        )

    private val timemaniaInfo =
        LotteryInfo(
            type = LotteryType.TIMEMANIA,
            howToPlay = "É a loteria para os apaixonados por futebol. Escolha 10 números entre os 80 disponíveis e um Time do Coração. São sorteados 7 números e um Time do Coração por concurso.",
            drawFrequency = "Os sorteios ocorrem nas terças, quintas e sábados.",
            betsInfo = "Valor da aposta única: R$ 3,50.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(10, "1 em 26.472.637 (7 acertos)"),
                ),
            bolaoInfo = "Não há bolão oficial para Timemania, você aposta individualmente, mas pode fazer várias apostas.",
            prizeAllocation = "Ganha quem acertar de 3 a 7 números ou o Time do Coração.",
        )

    private val duplaSenaInfo =
        LotteryInfo(
            type = LotteryType.DUPLA_SENA,
            howToPlay = "Com o mesmo bilhete, você tem o dobro de chances de ganhar: são dois sorteios por concurso e ganha quem acertar 3, 4, 5 ou 6 números no primeiro e/ou segundo sorteios.",
            drawFrequency = "Sorteios às segundas, quartas e sextas-feiras.",
            betsInfo = "Aposta simples custa R$ 2,50 (6 números).",
            probabilityInfo =
                listOf(
                    ProbabilityRow(6, "1 em 15.890.700"),
                ),
            bolaoInfo = "Bolão mínimo de R$ 12,50, cada cota mínima R$ 2,50.",
            prizeAllocation = "A premiação é dividida entre os acertadores do primeiro e do segundo sorteio.",
        )

    private val diaDeSorteInfo =
        LotteryInfo(
            type = LotteryType.DIA_DE_SORTE,
            howToPlay = "Escolha de 7 a 15 números dentre os 31 disponíveis e mais 1 'Mês de Sorte'. São sorteados 7 números e um 'Mês de Sorte' por concurso.",
            drawFrequency = "Terças, quintas e sábados às 20h.",
            betsInfo = "Aposta mínima (7 números): R$ 2,50.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(7, "1 em 2.629.575"),
                ),
            bolaoInfo = "Bolão mínimo R$ 12,00.",
            prizeAllocation = "Prêmios para 4, 5, 6 e 7 acertos, mais o Mês de Sorte.",
        )

    private val superSeteInfo =
        LotteryInfo(
            type = LotteryType.SUPER_SETE,
            howToPlay = "O volante contém 7 colunas com números de 0 a 9. Você deve escolher no mínimo 1 número por coluna.",
            drawFrequency = "Segundas, quartas e sextas-feiras.",
            betsInfo = "Aposta mínima R$ 2,50.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(7, "1 em 10.000.000"),
                ),
            bolaoInfo = "Bolão disponível, mínimo R$ 12,50.",
            prizeAllocation = "Prêmios para 3 a 7 colunas acertadas.",
        )

    private val maisMilionariaInfo =
        LotteryInfo(
            type = LotteryType.MAIS_MILIONARIA,
            howToPlay = "A +Milionária possui a matriz de números e a matriz de trevos numerados. Para apostar, você deve escolher números na Matriz de Números (1 a 50) e trevos na Matriz de Trevos Numerados (1 a 6).",
            drawFrequency = "Sorteios semanais, aos sábados.",
            betsInfo = "Aposta simples (6 números + 2 trevos): R$ 6,00.",
            probabilityInfo =
                listOf(
                    ProbabilityRow(6, "1 em 238.360.500"),
                ),
            bolaoInfo = "Permite bolão com preço mínimo de R$ 12,00.",
            prizeAllocation = "Possui 10 faixas de premiação.",
        )
}
