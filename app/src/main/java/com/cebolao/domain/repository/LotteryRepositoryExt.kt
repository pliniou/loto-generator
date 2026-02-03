package com.cebolao.domain.repository

import com.cebolao.domain.model.Contest
import com.cebolao.domain.model.LotteryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun LotteryRepository.observeLatestContests(types: List<LotteryType>): Flow<Map<LotteryType, Contest?>> {
    val ordered = types.distinct()
    val latestFlows: List<Flow<Contest?>> =
        ordered.map { type ->
            observeLatestContest(type)
        }
    return combine(latestFlows) { latest ->
        ordered.zip(latest.toList()).associate { (type, contest) -> type to contest }
    }
}
