package com.cebolao.domain.util

import java.text.Normalizer
import java.util.Locale

object TimemaniaUtil {
    data class Team(val id: Int, val name: String, val uf: String)

    val teams =
        listOf(
            Team(1, "FLAMENGO", "RJ"),
            Team(2, "CORINTHIANS", "SP"),
            Team(3, "PALMEIRAS", "SP"),
            Team(4, "SANTOS", "SP"),
            Team(5, "VASCO DA GAMA", "RJ"),
            Team(6, "SÃO PAULO", "SP"),
            Team(7, "GRÊMIO", "RS"),
            Team(8, "CRUZEIRO", "MG"),
            Team(9, "ATLÉTICO", "MG"),
            Team(10, "BOTAFOGO", "RJ"),
            Team(11, "FLUMINENSE", "RJ"),
            Team(12, "INTERNACIONAL", "RS"),
            Team(13, "BAHIA", "BA"),
            Team(14, "VITÓRIA", "BA"),
            Team(15, "GOIÁS", "GO"),
            Team(16, "ATHLETICO", "PR"),
            Team(17, "FORTALEZA", "CE"),
            Team(18, "ATLÉTICO", "GO"),
            Team(19, "CORITIBA", "PR"),
            Team(20, "AMÉRICA", "MG"),
            Team(21, "ABC", "RN"),
            Team(22, "AVAÍ", "SC"),
            Team(23, "CEARÁ", "CE"),
            Team(24, "SANTA CRUZ", "PE"),
            Team(25, "REMO", "PA"),
            Team(26, "CUIABÁ", "MT"),
            Team(27, "SPORT", "PE"),
            Team(28, "AMÉRICA", "RN"),
            Team(29, "CRICIÚMA", "SC"),
            Team(30, "CRB", "AL"),
            Team(31, "CSA", "AL"),
            Team(32, "PAYSANDU", "PA"),
            Team(33, "FIGUEIRENSE", "SC"),
            Team(34, "VILA NOVA", "GO"),
            Team(35, "BRAGANTINO", "SP"),
            Team(36, "PARANÁ", "PR"),
            Team(37, "PONTE PRETA", "SP"),
            Team(38, "ALTOS", "PI"),
            Team(39, "NÁUTICO", "PE"),
            Team(40, "APARECIDENSE", "GO"),
            Team(41, "SAMPAIO CORRÊA", "MA"),
            Team(42, "BOTAFOGO", "PB"),
            Team(43, "AMAZONAS", "AM"),
            Team(44, "FERROVIÁRIA", "SP"),
            Team(45, "GUARANI", "SP"),
            Team(46, "NOVORIZONTINO", "SP"),
            Team(47, "SÃO JOSÉ", "RS"),
            Team(48, "LONDRINA", "PR"),
            Team(49, "CONFIANÇA", "SE"),
            Team(50, "FLORESTA", "CE"),
            Team(51, "JUVENTUDE", "RS"),
            Team(52, "CAXIAS", "RS"),
            Team(53, "FERROVIÁRIO", "CE"),
            Team(54, "YPIRANGA", "RS"),
            Team(55, "BAHIA DE FEIRA", "BA"),
            Team(56, "ITUANO", "SP"),
            Team(57, "BOTAFOGO", "SP"),
            Team(58, "CHAPECOENSE", "SC"),
            Team(59, "BRASIL DE PELOTAS", "RS"),
            Team(60, "ATHLETIC CLUB", "MG"),
            Team(61, "BRASILIENSE", "DF"),
            Team(62, "BRUSQUE", "SC"),
            Team(63, "OPERÁRIO", "PR"),
            Team(64, "CAMPINENSE", "PB"),
            Team(65, "OESTE", "SP"),
            Team(66, "SÃO BERNARDO", "SP"),
            Team(67, "CASCAVEL", "PR"),
            Team(68, "CEILÂNDIA", "DF"),
            Team(69, "POUSO ALEGRE", "MG"),
            Team(70, "RETRÔ", "PE"),
            Team(71, "TOCANTINÓPOLIS", "TO"),
            Team(72, "ATLÉTICO", "CE"),
            Team(73, "BOAVISTA", "RJ"),
            Team(74, "CIANORTE", "PR"),
            Team(75, "IMPERATRIZ", "MA"),
            Team(76, "JOINVILLE", "SC"),
            Team(77, "LUVERDENSE", "MT"),
            Team(78, "MOTO CLUB", "MA"),
            Team(79, "TREZE", "PB"),
            Team(80, "RIO BRANCO", "AC"),
        )

    fun getTeamName(id: Int): String {
        return teams.find { it.id == id }?.let { "${it.name} (${it.uf})" } ?: "DESCONHECIDO"
    }

    fun getTeamId(name: String): Int? {
        val normalized = normalize(name)
        val uf = extractUf(name)
        val normalizedBase =
            if (uf != null && normalized.endsWith(uf)) {
                normalized.dropLast(uf.length)
            } else {
                normalized
            }

        val candidates =
            teams.filter { team ->
                val teamNorm = normalize(team.name)
                teamNorm == normalizedBase || normalizedBase.contains(teamNorm) || teamNorm.contains(normalizedBase)
            }

        val withUf =
            if (uf != null) {
                candidates.filter { it.uf.equals(uf, ignoreCase = true) }
            } else {
                emptyList()
            }

        return (withUf.ifEmpty { candidates }).firstOrNull()?.id
    }

    private fun normalize(value: String): String {
        val normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
        return normalized.replace("\\p{Mn}+".toRegex(), "")
            .uppercase(Locale.Builder().setLanguage("pt").setRegion("BR").build())
            .replace("[^A-Z0-9]".toRegex(), "")
    }

    private fun extractUf(value: String): String? {
        val upper = value.uppercase(Locale.Builder().setLanguage("pt").setRegion("BR").build())
        val match = Regex("\\b([A-Z]{2})\\b").find(upper)
        return match?.groupValues?.get(1)
    }
}
