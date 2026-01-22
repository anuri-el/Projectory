package com.projectory.domain.model

import java.time.LocalDateTime

data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: LocalDateTime?
)